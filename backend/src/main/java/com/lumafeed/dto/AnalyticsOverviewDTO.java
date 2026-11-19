package com.lumafeed.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Aggregated platform metrics consumed by the LumenPilot operations agent.
 *
 * @author qzh
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsOverviewDTO {

    private Integer days;
    private LocalDateTime from;
    private LocalDateTime to;
    private long totalUsers;
    private long totalBlogs;
    private long totalLikes;
    private long totalComments;
    private double avgLikesPerBlog;
    private List<DailyMetric> dailyTrend;
    private List<HotBlogMetric> hotBlogs;
    private List<MerchantMetricDTO> merchantRisks;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyMetric {
        private LocalDate date;
        private long blogs;
        private long likes;
        private long comments;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HotBlogMetric {
        private Long id;
        private Long userId;
        private String title;
        private Integer liked;
        private Integer comments;
        private LocalDateTime createTime;
    }
}
