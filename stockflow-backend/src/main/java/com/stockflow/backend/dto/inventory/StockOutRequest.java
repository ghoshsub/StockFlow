package com.stockflow.backend.dto.inventory;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * StockOutRequest — Payload for recording a stock-out movement.
 * Decreases product quantity. Fails if available quantity is insufficient.
 */
@Data
@Schema(description = "Request to dispatch stock for a product (Stock Out)")
public class StockOutRequest {

    @Schema(description = "Product ID to stock out", example = "1")
    @NotNull(message = "Product ID is required")
    private Long productId;

    @Schema(description = "Warehouse ID from which stock is dispatched", example = "1")
    @NotNull(message = "Warehouse ID is required")
    private Long warehouseId;

    @Schema(description = "Number of units to dispatch (must be > 0)", example = "10")
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Stock-out quantity must be at least 1")
    private Integer quantity;

    @Schema(description = "Optional note (e.g. delivery order ref)", example = "DO-2026-0022 dispatched")
    @Size(max = 500, message = "Remarks cannot exceed 500 characters")
    private String remarks;
}
