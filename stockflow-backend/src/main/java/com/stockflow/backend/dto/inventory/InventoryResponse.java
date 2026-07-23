package com.stockflow.backend.dto.inventory;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * InventoryResponse — Snapshot of a product's current stock state.
 * Returned by GET /api/inventory/product/{productId}.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Current inventory snapshot for a product")
public class InventoryResponse {

    @Schema(description = "Product ID", example = "1")
    private Long productId;

    @Schema(description = "Product SKU", example = "PRD-20260722-A3F7K2")
    private String sku;

    @Schema(description = "Product name", example = "Logitech MX Master 3S")
    private String productName;

    @Schema(description = "Warehouse ID", example = "1")
    private Long warehouseId;

    @Schema(description = "Warehouse name", example = "Central Logistics Hub")
    private String warehouseName;

    @Schema(description = "Warehouse code", example = "WH-MUM-01")
    private String warehouseCode;

    @Schema(description = "Current stock quantity", example = "85")
    private Integer currentQuantity;

    @Schema(description = "Minimum stock threshold for alerts", example = "10")
    private Integer minimumStock;

    @Schema(description = "Whether stock is at or below minimum threshold", example = "false")
    private Boolean isLowStock;

    @Schema(description = "Unit of measure", example = "PCS")
    private String unit;

    @Schema(description = "Timestamp of last stock movement", example = "2026-07-22T20:55:00")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime lastMovementAt;
}
