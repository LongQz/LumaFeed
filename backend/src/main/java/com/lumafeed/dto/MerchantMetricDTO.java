package com.lumafeed.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Merchant reputation snapshot for operations analysis.
 *
 * @author qzh
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MerchantMetricDTO {
    private Long shopId;
    private String name;
    private Double score;
    private Integer comments;
    private Integer sold;
    private String area;
    private String riskLevel;
}
