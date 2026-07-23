package com.stockflow.backend.dto.report;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * FinancialSummaryReportResponse — DTO payload for Financial Summary Report.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Financial summary report")
public class FinancialSummaryReportResponse {

    @Schema(description = "Total revenue from sales", example = "45000.50")
    private BigDecimal totalRevenue;

    @Schema(description = "Total purchase cost", example = "28000.00")
    private BigDecimal totalPurchaseCost;

    @Schema(description = "Gross Profit (Revenue - Purchase Cost)", example = "17000.50")
    private BigDecimal grossProfit;

    @Schema(description = "Current total inventory valuation", example = "35000.00")
    private BigDecimal currentInventoryValue;
}
