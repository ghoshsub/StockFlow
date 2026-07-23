package com.stockflow.backend.dto.dashboard;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * PurchaseAnalyticsResponse — Procurement analytics, supplier spending, purchase trends.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Purchase procurement analytics")
public class PurchaseAnalyticsResponse {

    @Schema(description = "Today's purchase cost total", example = "2500.00")
    private BigDecimal todaysPurchases;

    @Schema(description = "This month's purchase cost total", example = "18500.00")
    private BigDecimal monthlyPurchases;

    @Schema(description = "Top suppliers by order volume/spending")
    private List<ChartDataResponse> topSuppliers;

    @Schema(description = "Purchase cost trend grouped by Month (for Line Chart)")
    private List<ChartDataResponse> purchaseTrend;
}
