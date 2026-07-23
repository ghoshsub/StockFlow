package com.stockflow.backend.dto.report;

import com.stockflow.backend.dto.sale.SaleResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * SalesReportResponse — DTO payload for Sales Report.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Sales transactions report")
public class SalesReportResponse {

    @Schema(description = "Total sales transactions in period", example = "50")
    private Long totalSales;

    @Schema(description = "Total sales revenue in period", example = "32000.00")
    private BigDecimal totalRevenue;

    @Schema(description = "List of matching sales records")
    private List<SaleResponse> sales;
}
