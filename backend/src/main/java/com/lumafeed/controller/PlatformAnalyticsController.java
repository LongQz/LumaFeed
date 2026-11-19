package com.lumafeed.controller;

import com.lumafeed.config.AgentAccessGuard;
import com.lumafeed.dto.Result;
import com.lumafeed.service.IAnalyticsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * Read-only analytics API used by LumenPilot.
 *
 * @author qzh
 */
@RestController
@RequestMapping("/admin/analytics")
public class PlatformAnalyticsController {

    @Resource
    private IAnalyticsService analyticsService;
    @Resource
    private AgentAccessGuard agentAccessGuard;

    @GetMapping("/overview")
    public Result overview(
            @RequestHeader(value = "X-LumaFeed-Agent-Token", required = false) String token,
            @RequestParam(value = "days", defaultValue = "7") Integer days) {
        if (!agentAccessGuard.isAuthorized(token)) {
            return Result.fail("Unauthorized Agent request");
        }
        return Result.ok(analyticsService.getOverview(days));
    }

    @GetMapping("/merchants")
    public Result merchants(
            @RequestHeader(value = "X-LumaFeed-Agent-Token", required = false) String token,
            @RequestParam(value = "limit", defaultValue = "5") Integer limit) {
        if (!agentAccessGuard.isAuthorized(token)) {
            return Result.fail("Unauthorized Agent request");
        }
        return Result.ok(analyticsService.getMerchantMetrics(limit));
    }
}
