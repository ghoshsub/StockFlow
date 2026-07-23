package com.stockflow.backend.dto.dashboard;

import com.stockflow.backend.dto.product.ProductResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * InventoryAnalyticsResponse — Detailed analytics for stock levels and distribution.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Inventory stock analytics and category/warehouse breakdown")
public class InventoryAnalyticsResponse {

    @Schema(description = "Count of low stock products (quantity <= minimumStock)", example = "5")
    private Long lowStockCount;

    @Schema(description = "Count of out of stock products (quantity == 0)", example = "2")
    private Long outOfStockCount;

    @Schema(description = "List of low stock products")
    private List<ProductResponse> lowStockProducts;

    @Schema(description = "List of out of stock products")
    private List<ProductResponse> outOfStockProducts;

    @Schema(description = "Top 10 highest stock products")
    private List<ProductResponse> top10HighestStockProducts;

    @Schema(description = "Top 10 lowest stock products")
    private List<ProductResponse> top10LowestStockProducts;

    @Schema(description = "Inventory valuation grouped by Category (for Pie/Donut Chart)")
    private List<ChartDataResponse> inventoryValueByCategory;

    @Schema(description = "Inventory valuation grouped by Warehouse (for Bar Chart)")
    private List<ChartDataResponse> inventoryValueByWarehouse;
}
