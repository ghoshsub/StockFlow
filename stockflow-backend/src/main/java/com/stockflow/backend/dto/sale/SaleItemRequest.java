package com.stockflow.backend.dto.sale;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * SaleItemRequest — Payload for a single item line in a sales request.
 */
@Data
@Schema(description = "Sale item line request")
public class SaleItemRequest {

    @Schema(description = "Product ID", example = "1")
    @NotNull(message = "Product ID is required")
    private Long productId;

    @Schema(description = "Quantity sold (must be > 0)", example = "2")
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    @Schema(description = "Selling price per item (must be > 0)", example = "99.99")
    @NotNull(message = "Selling price is required")
    @DecimalMin(value = "0.01", message = "Selling price must be greater than 0")
    private BigDecimal sellingPrice;
}
