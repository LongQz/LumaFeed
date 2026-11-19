package com.lumafeed.service;

import com.lumafeed.dto.AnalyticsOverviewDTO;
import com.lumafeed.dto.MerchantMetricDTO;

import java.util.List;

/**
 * Read-only analytics exposed to the operations Agent.
 *
 * @author qzh
 */
public interface IAnalyticsService {
    AnalyticsOverviewDTO getOverview(int days);

    List<MerchantMetricDTO> getMerchantMetrics(int limit);
}
