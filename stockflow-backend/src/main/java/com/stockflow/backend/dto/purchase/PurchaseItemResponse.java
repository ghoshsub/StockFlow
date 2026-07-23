package com.stockflow.backend.dto.purchase;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * PurchaseItemResponse — Outbound DTO for a purchase line item.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Purchase line item response")
public class PurchaseItemResponse {

    @Schema(description = "Item ID", example = "1")
    private Long id;

    @Schema(description = "Product ID", example = "1")
    private Long productId;

    @Schema(description = "Product SKU", example = "PRD-20260722-A3F7K2")
    private String productSku;

    @Schema(description = "Product name", example = "Logitech MX Master 3S")
    private String productName;

    @Schema(description = "Quantity purchased", example = "20")
    private Integer quantity;

    @Schema(description = "Unit price per item", example = "75.00")
    private BigDecimal unitPrice;

    @Schema(description = "Line subtotal (quantity x unitPrice)", example = "1500.00")
    private BigDecimal subtotal;
}
