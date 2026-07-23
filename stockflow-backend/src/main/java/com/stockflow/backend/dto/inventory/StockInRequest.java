package com.stockflow.backend.dto.inventory;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * StockInRequest — Payload for recording a stock-in movement.
 * Increases the product's available quantity.
 */
@Data
@Schema(description = "Request to add stock for a product (Stock In)")
public class StockInRequest {

    @Schema(description = "Product ID to stock in", example = "1")
    @NotNull(message = "Product ID is required")
    private Long productId;

    @Schema(description = "Warehouse ID where stock is received", example = "1")
    @NotNull(message = "Warehouse ID is required")
    private Long warehouseId;

    @Schema(description = "Number of units to add (must be > 0)", example = "50")
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Stock-in quantity must be at least 1")
    private Integer quantity;

    @Schema(description = "Optional note or reason (e.g. purchase order ref)", example = "PO-2026-0001 received")
    @Size(max = 500, message = "Remarks cannot exceed 500 characters")
    private String remarks;
}
