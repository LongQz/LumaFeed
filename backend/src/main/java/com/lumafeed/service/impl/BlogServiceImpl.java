package com.lumafeed.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lumafeed.dto.Result;
import com.lumafeed.dto.ScrollResult;
import com.lumafeed.dto.UserDTO;
import com.lumafeed.entity.Blog;
import com.lumafeed.entity.Follow;
import com.lumafeed.entity.User;
import com.lumafeed.mapper.BlogMapper;
import com.lumafeed.service.IBlogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lumafeed.service.IFollowService;
import com.lumafeed.service.IUserService;
import com.lumafeed.utils.SystemConstants;
import com.lumafeed.utils.UserHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.lumafeed.utils.RedisConstants.BLOG_LIKED_USERS_KEY;
import static com.lumafeed.utils.RedisConstants.BLOG_LIKE_COUNT_KEY;
import static com.lumafeed.utils.RedisConstants.FEED_KEY;
import static com.lumafeed.utils.RedisConstants.HOT_BLOG_KEY;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author qzh
 * @since 2021-12-22
 */
@Service
public class BlogServiceImpl extends ServiceImpl<BlogMapper, Blog> implements IBlogService {
    private static final DefaultRedisScript<Long> LIKE_TOGGLE_SCRIPT;

    static {
        LIKE_TOGGLE_SCRIPT = new DefaultRedisScript<>();
        LIKE_TOGGLE_SCRIPT.setLocation(new ClassPathResource("like_toggle.lua"));
        LIKE_TOGGLE_SCRIPT.setResultType(Long.class);
    }

    @Autowired
    private IUserService userService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private IFollowService followService;


    @Override
    public Result likeBlog(Long id) {
        Long userId = UserHolder.getUser().getId();
        Blog blog = getById(id);
        if (blog == null) {
            return Result.fail("笔记不存在");
        }

        String likedUsersKey = BLOG_LIKED_USERS_KEY + id;
        String countKey = BLOG_LIKE_COUNT_KEY + id;
        stringRedisTemplate.opsForValue().setIfAbsent(
                countKey,
                String.valueOf(blog.getLiked() == null ? 0 : blog.getLiked())
        );
        Long delta = stringRedisTemplate.execute(
                LIKE_TOGGLE_SCRIPT,
                Arrays.asList(likedUsersKey, countKey, HOT_BLOG_KEY),
                userId.toString(),
                id.toString()
        );
        if (delta == null) {
            return Result.fail("点赞失败，请稍后重试");
        }

        boolean updated;
        if (delta > 0) {
            updated = update().setSql("liked = liked + 1").eq("id", id).update();
        } else {
            updated = update().setSql("liked = GREATEST(liked - 1, 0)").eq("id", id).update();
        }
        if (!updated) {
            // Redis 是点赞切换的原子入口；数据库更新失败时反向执行一次切换。
            stringRedisTemplate.execute(
                    LIKE_TOGGLE_SCRIPT,
                    Arrays.asList(likedUsersKey, countKey, HOT_BLOG_KEY),
                    userId.toString(),
                    id.toString()
            );
            return Result.fail("点赞状态保存失败");
        }
        return Result.ok(delta > 0);
    }

    @Override
    public Result queryBlogLikes(Long id) {
        Set<String> likedUsers = stringRedisTemplate.opsForSet().members(BLOG_LIKED_USERS_KEY + id);
        if (likedUsers == null || likedUsers.isEmpty()) {
            return Result.ok(Collections.emptyList());
        }
        List<Long> ids = likedUsers.stream()
                .map(Long::valueOf)
                .sorted(Comparator.naturalOrder())
                .limit(5)
                .collect(Collectors.toList());
        String idStr = StrUtil.join(",", ids);
        List<UserDTO> userDTOS = userService.query()
                .in("id", ids).last("ORDER BY FIELD(id," + idStr + ")").list()
                .stream()
                .map(user -> BeanUtil.copyProperties(user, UserDTO.class))
                .collect(Collectors.toList());
        return Result.ok(userDTOS);
    }

    @Override
    public Result saveBlog(Blog blog) {
        // 1.获取登录用户
        UserDTO user = UserHolder.getUser();
        blog.setUserId(user.getId());
        // 2.保存探店笔记
        boolean isSuccess = save(blog);
        if(!isSuccess){
            return Result.fail("新增笔记失败!");
        }
        int initialLiked = blog.getLiked() == null ? 0 : blog.getLiked();
        stringRedisTemplate.opsForZSet().add(HOT_BLOG_KEY, blog.getId().toString(), initialLiked);
        stringRedisTemplate.opsForValue().set(BLOG_LIKE_COUNT_KEY + blog.getId(), String.valueOf(initialLiked));
        // 3.查询笔记作者的所有粉丝 select * from tb_follow where follow_user_id = ?
        List<Follow> follows = followService.query().eq("follow_user_id", user.getId()).list();
        // 4.推送笔记id给所有粉丝
        for (Follow follow : follows) {
            // 4.1.获取粉丝id
            Long userId = follow.getUserId();
            // 4.2.推送
            String key = FEED_KEY + userId;
            stringRedisTemplate.opsForZSet().add(key, blog.getId().toString(), System.currentTimeMillis());
        }
        // 5.返回id
        return Result.ok(blog.getId());
    }

    @Override
    public Result queryHotBlog(Integer current) {
        int safeCurrent = Math.max(1, current == null ? 1 : current);
        int from = (safeCurrent - 1) * SystemConstants.MAX_PAGE_SIZE;
        int to = safeCurrent * SystemConstants.MAX_PAGE_SIZE - 1;
        Set<String> idSet = stringRedisTemplate.opsForZSet().reverseRange(HOT_BLOG_KEY, from, to);
        if (idSet == null || idSet.isEmpty()) {
            return Result.ok(Collections.emptyList());
        }

        List<Long> ids = idSet.stream().map(Long::valueOf).collect(Collectors.toList());
        String idStr = StrUtil.join(",", ids);
        List<Blog> records = query()
                .in("id", ids)
                .last("ORDER BY FIELD(id," + idStr + ")")
                .list();
        records.forEach(blog -> {
            this.queryBlogUser(blog);
            this.isBlogLiked(blog);
        });
        return Result.ok(records);
    }
    @Override
    public Result queryBlogById(Long id) {
        // 1.查询blog
        Blog blog = getById(id);
        if (blog == null) {
            return Result.fail("笔记不存在！");
        }
        // 2.查询blog有关的用户
        queryBlogUser(blog);
        // 3.查询blog是否被点赞
        isBlogLiked(blog);
        return Result.ok(blog);
    }

    @Override
    public Result queryBlogOfFollow(Long max, Integer offset) {
        // 1.获取当前用户
        Long userId = UserHolder.getUser().getId();
        // 2.查询收件箱 ZREVRANGEBYSCORE key Max Min LIMIT offset count
        String key = FEED_KEY + userId;
        Set<ZSetOperations.TypedTuple<String>> typedTuples = stringRedisTemplate.opsForZSet()
                .reverseRangeByScoreWithScores(key, 0, max, offset, 2);
        // 3.非空判断
        if (typedTuples == null || typedTuples.isEmpty()) {
            return Result.ok();
        }
        // 4.解析数据：blogId、minTime（时间戳）、offset
        List<Long> ids = new ArrayList<>(typedTuples.size());
        long minTime = 0; // 2
        int os = 1; // 2
        for (ZSetOperations.TypedTuple<String> tuple : typedTuples) { // 5 4 4 2 2
            // 4.1.获取id
            ids.add(Long.valueOf(tuple.getValue()));
            // 4.2.获取分数(时间戳）
            long time = tuple.getScore().longValue();
            if(time == minTime){
                os++;
            }else{
                minTime = time;
                os = 1;
            }
        }

        // 5.根据id查询blog
        String idStr = StrUtil.join(",", ids);
        List<Blog> blogs = query().in("id", ids).last("ORDER BY FIELD(id," + idStr + ")").list();

        for (Blog blog : blogs) {
            // 5.1.查询blog有关的用户
            queryBlogUser(blog);
            // 5.2.查询blog是否被点赞
            isBlogLiked(blog);
        }

        // 6.封装并返回
        ScrollResult r = new ScrollResult();
        r.setList(blogs);
        r.setOffset(os);
        r.setMinTime(minTime);

        return Result.ok(r);
    }

    private void queryBlogUser(Blog blog) {
        Long userId = blog.getUserId();
        User user = userService.getById(userId);
        blog.setName(user.getNickName());
        blog.setIcon(user.getIcon());
    }

    private void isBlogLiked(Blog blog) {
        // 1.获取登录用户
        UserDTO user = UserHolder.getUser();
        if (user == null) {
            // 用户未登录，无需查询是否点赞
            return;
        }
        Long userId = user.getId();
        Boolean liked = stringRedisTemplate.opsForSet().isMember(
                BLOG_LIKED_USERS_KEY + blog.getId(),
                userId.toString()
        );
        blog.setIsLike(Boolean.TRUE.equals(liked));
    }
}
