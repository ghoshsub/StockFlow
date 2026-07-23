package com.stockflow.backend.dto.inventory;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * StockAdjustmentRequest — Payload for a manual stock correction.
 * Sets the product's quantity to a specific absolute value.
 * Use when physical count differs from system count (e.g. after stocktake).
 */
@Data
@Schema(description = "Request to adjust stock quantity to an absolute value")
public class StockAdjustmentRequest {

    @Schema(description = "Product ID to adjust", example = "1")
    @NotNull(message = "Product ID is required")
    private Long productId;

    @Schema(description = "Warehouse ID where adjustment is applied", example = "1")
    @NotNull(message = "Warehouse ID is required")
    private Long warehouseId;

    @Schema(description = "New absolute quantity after adjustment (must be >= 0)", example = "75")
    @NotNull(message = "Adjusted quantity is required")
    @Min(value = 0, message = "Adjusted quantity cannot be negative")
    private Integer adjustedQuantity;

    @Schema(description = "Reason for the adjustment (mandatory for audit)", example = "Physical stocktake — July 2026")
    @NotNull(message = "Remarks are required for stock adjustments")
    @Size(min = 5, max = 500, message = "Remarks must be between 5 and 500 characters")
    private String remarks;
}
