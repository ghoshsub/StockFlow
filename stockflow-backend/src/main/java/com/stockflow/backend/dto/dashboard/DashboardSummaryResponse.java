package com.stockflow.backend.dto.dashboard;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DashboardSummaryResponse — Executive summary KPI metrics.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Executive dashboard KPI summary")
public class DashboardSummaryResponse {

    @Schema(description = "Total active products count", example = "150")
    private Long totalProducts;

    @Schema(description = "Total active categories count", example = "12")
    private Long totalCategories;

    @Schema(description = "Total active brands count", example = "25")
    private Long totalBrands;

    @Schema(description = "Total active suppliers count", example = "8")
    private Long totalSuppliers;

    @Schema(description = "Total active warehouses count", example = "3")
    private Long totalWarehouses;

    @Schema(description = "Total purchases count", example = "45")
    private Long totalPurchases;

    @Schema(description = "Total sales count", example = "120")
    private Long totalSales;

    @Schema(description = "Total revenue from sales", example = "45000.50")
    private BigDecimal totalRevenue;

    @Schema(description = "Total procurement cost from purchases", example = "28000.00")
    private BigDecimal totalPurchaseCost;

    @Schema(description = "Current total inventory valuation (quantity * buyingPrice)", example = "35000.00")
    private BigDecimal currentInventoryValue;
}
