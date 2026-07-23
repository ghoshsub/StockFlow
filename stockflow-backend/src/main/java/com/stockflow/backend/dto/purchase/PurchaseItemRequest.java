package com.stockflow.backend.dto.purchase;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * PurchaseItemRequest — DTO for single line item in purchase request.
 */
@Data
@Schema(description = "Purchase item line request")
public class PurchaseItemRequest {

    @Schema(description = "Product ID", example = "1")
    @NotNull(message = "Product ID is required")
    private Long productId;

    @Schema(description = "Quantity purchased (must be > 0)", example = "20")
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    @Schema(description = "Unit price per item (must be > 0)", example = "75.00")
    @NotNull(message = "Unit price is required")
    @DecimalMin(value = "0.01", message = "Unit price must be greater than 0")
    private BigDecimal unitPrice;
}
