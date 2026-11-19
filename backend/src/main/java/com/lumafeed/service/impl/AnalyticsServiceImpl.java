package com.lumafeed.service.impl;

import com.lumafeed.dto.AnalyticsOverviewDTO;
import com.lumafeed.dto.MerchantMetricDTO;
import com.lumafeed.entity.Blog;
import com.lumafeed.entity.Shop;
import com.lumafeed.service.IAnalyticsService;
import com.lumafeed.service.IBlogService;
import com.lumafeed.service.IShopService;
import com.lumafeed.service.IUserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Builds stable, read-only aggregates for LumenPilot.
 *
 * @author qzh
 */
@Service
public class AnalyticsServiceImpl implements IAnalyticsService {

    @Resource
    private IBlogService blogService;
    @Resource
    private IShopService shopService;
    @Resource
    private IUserService userService;

    @Override
    public AnalyticsOverviewDTO getOverview(int days) {
        int safeDays = Math.max(1, Math.min(days, 90));
        LocalDateTime to = LocalDateTime.now();
        LocalDateTime from = to.minusDays(safeDays);

        List<Blog> blogs = blogService.query()
                .ge("create_time", from)
                .orderByDesc("create_time")
                .list();

        long totalLikes = blogs.stream().mapToLong(item -> valueOrZero(item.getLiked())).sum();
        long totalComments = blogs.stream().mapToLong(item -> valueOrZero(item.getComments())).sum();
        double avgLikes = blogs.isEmpty() ? 0 : Math.round(totalLikes * 100.0 / blogs.size()) / 100.0;

        Map<LocalDate, List<Blog>> byDate = blogs.stream()
                .filter(item -> item.getCreateTime() != null)
                .collect(Collectors.groupingBy(item -> item.getCreateTime().toLocalDate(), LinkedHashMap::new, Collectors.toList()));

        List<AnalyticsOverviewDTO.DailyMetric> trend = new ArrayList<>();
        for (LocalDate date = from.toLocalDate(); !date.isAfter(to.toLocalDate()); date = date.plusDays(1)) {
            List<Blog> dailyBlogs = byDate.getOrDefault(date, new ArrayList<>());
            long likes = dailyBlogs.stream().mapToLong(item -> valueOrZero(item.getLiked())).sum();
            long comments = dailyBlogs.stream().mapToLong(item -> valueOrZero(item.getComments())).sum();
            trend.add(new AnalyticsOverviewDTO.DailyMetric(date, dailyBlogs.size(), likes, comments));
        }

        List<AnalyticsOverviewDTO.HotBlogMetric> hotBlogs = blogs.stream()
                .sorted(Comparator.comparingInt((Blog item) -> item.getLiked() == null ? 0 : item.getLiked()).reversed())
                .limit(5)
                .map(item -> new AnalyticsOverviewDTO.HotBlogMetric(
                        item.getId(), item.getUserId(), item.getTitle(), item.getLiked(), item.getComments(), item.getCreateTime()))
                .collect(Collectors.toList());

        return new AnalyticsOverviewDTO(
                safeDays,
                from,
                to,
                userService.count(),
                blogs.size(),
                totalLikes,
                totalComments,
                avgLikes,
                trend,
                hotBlogs,
                getMerchantMetrics(5)
        );
    }

    @Override
    public List<MerchantMetricDTO> getMerchantMetrics(int limit) {
        int safeLimit = Math.max(1, Math.min(limit, 20));
        List<Shop> shops = shopService.query()
                .orderByAsc("score")
                .orderByDesc("comments")
                .last("limit " + safeLimit)
                .list();

        return shops.stream().map(shop -> {
            double score = shop.getScore() == null ? 0 : shop.getScore() / 10.0;
            String riskLevel = score < 4.0 ? "HIGH" : score < 4.5 ? "MEDIUM" : "NORMAL";
            return new MerchantMetricDTO(
                    shop.getId(),
                    shop.getName(),
                    score,
                    shop.getComments(),
                    shop.getSold(),
                    shop.getArea(),
                    riskLevel
            );
        }).collect(Collectors.toList());
    }

    private long valueOrZero(Integer value) {
        return value == null ? 0 : value.longValue();
    }
}
