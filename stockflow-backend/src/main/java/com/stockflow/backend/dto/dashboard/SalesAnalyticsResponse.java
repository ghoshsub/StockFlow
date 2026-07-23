package com.stockflow.backend.dto.dashboard;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * SalesAnalyticsResponse — Detailed sales analytics, periodic revenue, top products & customers.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Sales revenue and customer analytics")
public class SalesAnalyticsResponse {

    @Schema(description = "Today's revenue total", example = "1250.00")
    private BigDecimal todaysSales;

    @Schema(description = "This week's revenue total", example = "8500.00")
    private BigDecimal thisWeeksSales;

    @Schema(description = "This month's revenue total", example = "32000.00")
    private BigDecimal thisMonthsSales;

    @Schema(description = "Yearly revenue total", example = "180000.00")
    private BigDecimal yearlySales;

    @Schema(description = "Sales revenue grouped by Month (for Line/Area Chart)")
    private List<ChartDataResponse> salesByMonth;

    @Schema(description = "Top selling products by volume/revenue")
    private List<ChartDataResponse> topSellingProducts;

    @Schema(description = "Top customers by revenue contribution")
    private List<ChartDataResponse> topCustomers;
}
