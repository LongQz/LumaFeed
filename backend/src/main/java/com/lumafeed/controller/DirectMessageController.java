package com.lumafeed.controller;

import com.lumafeed.dto.Result;
import com.lumafeed.dto.UserDTO;
import com.lumafeed.utils.UserHolder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/** Direct messages between LumaFeed users. */
@RestController
@RequestMapping("/direct-messages")
public class DirectMessageController {
    private final JdbcTemplate jdbcTemplate;

    public DirectMessageController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/contacts")
    public Result contacts() {
        Long userId = currentUserId();
        String sql = "select u.id, u.nick_name as nickname, u.icon, "
                + "coalesce((select m.content from tb_direct_message m "
                + "where (m.from_user_id=u.id and m.to_user_id=?) "
                + "or (m.from_user_id=? and m.to_user_id=u.id) "
                + "order by m.id desc limit 1), '') as last_message, "
                + "(select m.create_time from tb_direct_message m "
                + "where (m.from_user_id=u.id and m.to_user_id=?) "
                + "or (m.from_user_id=? and m.to_user_id=u.id) "
                + "order by m.id desc limit 1) as last_time, "
                + "(select count(*) from tb_direct_message m "
                + "where m.from_user_id=u.id and m.to_user_id=? and m.is_read=0) as unread "
                + "from tb_user u where u.id<>? "
                + "order by unread desc, last_time desc, u.id limit 30";
        return Result.ok(jdbcTemplate.queryForList(
                sql, userId, userId, userId, userId, userId, userId));
    }

    @GetMapping("/with/{otherUserId}")
    public Result conversation(@PathVariable Long otherUserId) {
        Long userId = currentUserId();
        jdbcTemplate.update(
                "update tb_direct_message set is_read=1 where from_user_id=? and to_user_id=?",
                otherUserId, userId);
        String sql = "select id, from_user_id, to_user_id, content, is_read, create_time "
                + "from tb_direct_message where (from_user_id=? and to_user_id=?) "
                + "or (from_user_id=? and to_user_id=?) order by id asc";
        List<Map<String, Object>> messages = jdbcTemplate.queryForList(
                sql, userId, otherUserId, otherUserId, userId);
        messages.forEach(message -> message.put(
                "mine",
                userId.equals(((Number) message.get("from_user_id")).longValue())
        ));
        return Result.ok(messages);
    }

    @PostMapping("/send")
    public Result send(@RequestBody Map<String, Object> request) {
        Long userId = currentUserId();
        Object target = request.get("toUserId");
        String content = request.get("content") == null ? "" : request.get("content").toString().trim();
        if (target == null || content.trim().isEmpty() || content.length() > 1000) {
            return Result.fail("消息内容不能为空且不能超过1000字");
        }
        Long toUserId = Long.valueOf(target.toString());
        if (toUserId.equals(userId)) {
            return Result.fail("不能给自己发送私信");
        }
        Integer count = jdbcTemplate.queryForObject(
                "select count(*) from tb_user where id=?",
                Integer.class,
                toUserId
        );
        if (count == null || count == 0) {
            return Result.fail("接收用户不存在");
        }
        jdbcTemplate.update(
                "insert into tb_direct_message(from_user_id,to_user_id,content) values(?,?,?)",
                userId,
                toUserId,
                content
        );
        return Result.ok(true);
    }

    private Long currentUserId() {
        UserDTO user = UserHolder.getUser();
        if (user == null) {
            throw new IllegalStateException("请先登录");
        }
        return user.getId();
    }
}
