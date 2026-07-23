package com.stockflow.backend.dto.sale;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * SaleItemResponse — Outbound DTO for a sale line item.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Sale item line response")
public class SaleItemResponse {

    @Schema(description = "Item ID", example = "1")
    private Long id;

    @Schema(description = "Product ID", example = "1")
    private Long productId;

    @Schema(description = "Product SKU", example = "PRD-20260722-A3F7K2")
    private String productSku;

    @Schema(description = "Product name", example = "Logitech MX Master 3S")
    private String productName;

    @Schema(description = "Quantity sold", example = "2")
    private Integer quantity;

    @Schema(description = "Selling price per item", example = "99.99")
    private BigDecimal sellingPrice;

    @Schema(description = "Line subtotal (quantity x sellingPrice)", example = "199.98")
    private BigDecimal subtotal;
}
