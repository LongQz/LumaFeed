package com.lumafeed.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lumafeed.dto.Result;
import com.lumafeed.dto.UserDTO;
import com.lumafeed.entity.Blog;
import com.lumafeed.entity.User;
import com.lumafeed.service.IBlogService;
import com.lumafeed.service.IUserService;
import com.lumafeed.service.RedisRateLimitService;
import com.lumafeed.utils.SystemConstants;
import com.lumafeed.utils.UserHolder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author qzh
 * @since 2021-12-22
 */
@RestController
@RequestMapping("/blog")
public class BlogController {

    @Resource
    private IBlogService blogService;
    @Resource
    private IUserService userService;
    @Resource
    private RedisRateLimitService rateLimitService;

    //保存博文
    @PostMapping
    public Result saveBlog(@RequestBody Blog blog, HttpServletRequest request) {
        if (!rateLimitService.tryAcquire("blog:publish:" + rateSubject(request), 20, 60)) {
            return Result.fail("发布过于频繁，请稍后重试");
        }
        return blogService.saveBlog(blog);
    }

    //点赞
    @PutMapping("/like/{id}")
    public Result likeBlog(@PathVariable("id") Long id, HttpServletRequest request) {
        if (!rateLimitService.tryAcquire("blog:like:" + rateSubject(request), 120, 60)) {
            return Result.fail("点赞过于频繁，请稍后重试");
        }
        return blogService.likeBlog(id);
    }

    @GetMapping("/of/me")
    public Result queryMyBlog(@RequestParam(value = "current", defaultValue = "1") Integer current) {
        // 获取登录用户
        UserDTO user = UserHolder.getUser();
        // 根据用户查询
        Page<Blog> page = blogService.query()
                .eq("user_id", user.getId()).page(new Page<>(current, SystemConstants.MAX_PAGE_SIZE));
        // 获取当前页数据
        List<Blog> records = page.getRecords();
        return Result.ok(records);
    }

    // 查询最热博文
    @GetMapping("/hot")
    public Result queryHotBlog(
            @RequestParam(value = "current", defaultValue = "1") Integer current,
            HttpServletRequest request) {
        if (!rateLimitService.tryAcquire("blog:hot:" + rateSubject(request), 50000, 60)) {
            return Result.fail("请求过于频繁，请稍后重试");
        }
        return blogService.queryHotBlog(current);
    }

    //根据id查询博文详情
    @GetMapping("/{id}")
    public Result queryBlogById(@PathVariable("id") Long id) {
        return blogService.queryBlogById(id);
    }

    // 查询用户的所有博文
    @GetMapping("/of/user")
    public Result queryBlogByUserId(
            @RequestParam(value = "current", defaultValue = "1") Integer current,
            @RequestParam("id") Long id) {
        // 根据用户查询
        Page<Blog> page = blogService.query()
                .eq("user_id", id).page(new Page<>(current, SystemConstants.MAX_PAGE_SIZE));
        // 获取当前页数据
        List<Blog> records = page.getRecords();
        return Result.ok(records);
    }

    //查询用户点赞的所有博文
    @GetMapping("/likes/{id}")
    public Result queryBlogLikes(@PathVariable("id") Long id) {
        return blogService.queryBlogLikes(id);
    }

    @GetMapping("/of/follow")
    public Result queryBlogOfFollow(
            @RequestParam("lastId") Long max,
            @RequestParam(value = "offset", defaultValue = "0") Integer offset,
            HttpServletRequest request) {
        if (!rateLimitService.tryAcquire("blog:feed:" + rateSubject(request), 30000, 60)) {
            return Result.fail("请求过于频繁，请稍后重试");
        }
        return blogService.queryBlogOfFollow(max, offset);
    }

    @GetMapping("/test")
    public Result test(){
        System.out.println("test");
        return Result.ok("test");
    }

    private String rateSubject(HttpServletRequest request) {
        UserDTO user = UserHolder.getUser();
        return user == null ? "ip:" + request.getRemoteAddr() : "user:" + user.getId();
    }
}
