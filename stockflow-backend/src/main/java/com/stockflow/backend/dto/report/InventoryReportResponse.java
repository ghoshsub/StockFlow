package com.stockflow.backend.dto.report;

import com.stockflow.backend.dto.product.ProductResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * InventoryReportResponse — DTO payload for Inventory Report.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Inventory status report")
public class InventoryReportResponse {

    @Schema(description = "Total active products count", example = "150")
    private Long totalProducts;

    @Schema(description = "Total inventory valuation", example = "35000.00")
    private BigDecimal totalInventoryValue;

    @Schema(description = "Count of low stock products", example = "5")
    private Long lowStockCount;

    @Schema(description = "Count of out of stock products", example = "2")
    private Long outOfStockCount;

    @Schema(description = "Current active inventory items")
    private List<ProductResponse> currentInventory;

    @Schema(description = "Low stock items list")
    private List<ProductResponse> lowStockProducts;

    @Schema(description = "Out of stock items list")
    private List<ProductResponse> outOfStockProducts;
}
