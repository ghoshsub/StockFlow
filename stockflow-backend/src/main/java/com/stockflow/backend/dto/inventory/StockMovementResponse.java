package com.stockflow.backend.dto.inventory;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * StockMovementResponse — Full audit record of a single stock movement.
 * Returned in inventory history and movement search results.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Stock movement audit record")
public class StockMovementResponse {

    @Schema(description = "Movement ID", example = "42")
    private Long id;

    @Schema(description = "Movement type: STOCK_IN, STOCK_OUT, or ADJUSTMENT", example = "STOCK_IN")
    private String movementType;

    @Schema(description = "Quantity moved", example = "50")
    private Integer quantity;

    @Schema(description = "Stock quantity before this movement", example = "35")
    private Integer quantityBefore;

    @Schema(description = "Stock quantity after this movement", example = "85")
    private Integer quantityAfter;

    @Schema(description = "Optional note or reason", example = "PO-2026-0001 received")
    private String remarks;

    @Schema(description = "Timestamp of movement", example = "2026-07-22T20:55:00")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime movementDate;

    // ── Related Entity Summaries ──────────────────────────────────────────────

    @Schema(description = "Product ID", example = "1")
    private Long productId;

    @Schema(description = "Product SKU", example = "PRD-20260722-A3F7K2")
    private String productSku;

    @Schema(description = "Product name", example = "Logitech MX Master 3S")
    private String productName;

    @Schema(description = "Warehouse ID", example = "1")
    private Long warehouseId;

    @Schema(description = "Warehouse name", example = "Central Logistics Hub")
    private String warehouseName;

    @Schema(description = "Warehouse code", example = "WH-MUM-01")
    private String warehouseCode;

    @Schema(description = "User who performed the movement", example = "admin")
    private String performedBy;

    @Schema(description = "Record creation timestamp", example = "2026-07-22T20:55:01")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
}
