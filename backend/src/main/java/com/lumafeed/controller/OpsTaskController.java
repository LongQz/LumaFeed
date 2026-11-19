package com.lumafeed.controller;

import cn.hutool.core.util.StrUtil;
import com.lumafeed.config.AgentAccessGuard;
import com.lumafeed.dto.OpsTaskCreateDTO;
import com.lumafeed.dto.Result;
import com.lumafeed.entity.OpsTask;
import com.lumafeed.service.IOpsTaskService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * Human-confirmed operations task endpoint.
 *
 * @author qzh
 */
@RestController
@RequestMapping("/admin/ops/tasks")
public class OpsTaskController {

    @Resource
    private IOpsTaskService opsTaskService;
    @Resource
    private AgentAccessGuard agentAccessGuard;

    @PostMapping
    public Result create(
            @RequestHeader(value = "X-LumaFeed-Agent-Token", required = false) String token,
            @RequestBody OpsTaskCreateDTO request) {
        if (!agentAccessGuard.isAuthorized(token)) {
            return Result.fail("Unauthorized Agent request");
        }
        if (request == null || StrUtil.isBlank(request.getTitle())) {
            return Result.fail("Task title is required");
        }

        LocalDateTime now = LocalDateTime.now();
        OpsTask task = new OpsTask()
                .setTitle(request.getTitle().trim())
                .setDescription(StrUtil.blankToDefault(request.getDescription(), ""))
                .setPriority(StrUtil.blankToDefault(request.getPriority(), "MEDIUM"))
                .setStatus("PENDING")
                .setCreator("LumenPilot")
                .setCreateTime(now)
                .setUpdateTime(now);
        opsTaskService.save(task);
        return Result.ok(task);
    }

    @GetMapping
    public Result list(
            @RequestHeader(value = "X-LumaFeed-Agent-Token", required = false) String token) {
        if (!agentAccessGuard.isAuthorized(token)) {
            return Result.fail("Unauthorized Agent request");
        }
        return Result.ok(opsTaskService.query()
                .orderByDesc("create_time")
                .last("limit 100")
                .list());
    }
}
