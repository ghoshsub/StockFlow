package com.stockflow.backend.dto.sale;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * SaleRequest — Payload for creating a new sales transaction.
 */
@Data
@Schema(description = "Sales order creation request")
public class SaleRequest {

    @Schema(description = "Customer Name", example = "John Doe")
    @Size(max = 100, message = "Customer name cannot exceed 100 characters")
    private String customerName;

    @Schema(description = "Customer Email", example = "john.doe@example.com")
    @Size(max = 100, message = "Customer email cannot exceed 100 characters")
    private String customerEmail;

    @Schema(description = "Customer Phone", example = "+1234567890")
    @Size(max = 30, message = "Customer phone cannot exceed 30 characters")
    private String customerPhone;

    @Schema(description = "Warehouse ID stock is dispatched from", example = "1")
    @NotNull(message = "Warehouse ID is required")
    private Long warehouseId;

    @Schema(description = "Payment method (CASH, BANK_TRANSFER, CREDIT_CARD)", example = "CREDIT_CARD")
    @NotBlank(message = "Payment method is required")
    @Size(max = 30, message = "Payment method cannot exceed 30 characters")
    private String paymentMethod;

    @Schema(description = "Payment status (PAID, PENDING, FAILED)", example = "PAID")
    @NotBlank(message = "Payment status is required")
    @Size(max = 30, message = "Payment status cannot exceed 30 characters")
    private String paymentStatus;

    @Schema(description = "Discount amount (>= 0)", example = "10.00")
    @DecimalMin(value = "0.0", message = "Discount cannot be negative")
    private BigDecimal discount = BigDecimal.ZERO;

    @Schema(description = "Tax amount (>= 0)", example = "5.00")
    @DecimalMin(value = "0.0", message = "Tax cannot be negative")
    private BigDecimal tax = BigDecimal.ZERO;

    @Schema(description = "Optional remarks", example = "Retail sale at store counter")
    @Size(max = 500, message = "Remarks cannot exceed 500 characters")
    private String remarks;

    @Schema(description = "List of sale line items (at least 1 required)")
    @NotEmpty(message = "Sale must contain at least one item")
    @Valid
    private List<SaleItemRequest> items;
}
