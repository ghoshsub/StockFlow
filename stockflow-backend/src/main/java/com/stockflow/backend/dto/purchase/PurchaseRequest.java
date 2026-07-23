package com.stockflow.backend.dto.purchase;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * PurchaseRequest — DTO for creating a new Purchase order.
 */
@Data
@Schema(description = "Purchase order creation request")
public class PurchaseRequest {

    @Schema(description = "Supplier ID", example = "1")
    @NotNull(message = "Supplier ID is required")
    private Long supplierId;

    @Schema(description = "Warehouse ID", example = "1")
    @NotNull(message = "Warehouse ID is required")
    private Long warehouseId;

    @Schema(description = "Invoice number (unique)", example = "INV-2026-0089")
    @Size(max = 100, message = "Invoice number cannot exceed 100 characters")
    private String invoiceNumber;

    @Schema(description = "Payment status (PAID, PENDING, UNPAID)", example = "PAID")
    @NotNull(message = "Payment status is required")
    @Size(max = 30, message = "Payment status cannot exceed 30 characters")
    private String paymentStatus;

    @Schema(description = "Payment method (CASH, BANK_TRANSFER, CREDIT_CARD)", example = "BANK_TRANSFER")
    @Size(max = 30, message = "Payment method cannot exceed 30 characters")
    private String paymentMethod;

    @Schema(description = "Optional remarks", example = "Bulk restocking order")
    @Size(max = 500, message = "Remarks cannot exceed 500 characters")
    private String remarks;

    @Schema(description = "List of purchase line items (at least 1 required)")
    @NotEmpty(message = "Purchase must contain at least one item")
    @Valid
    private List<PurchaseItemRequest> items;
}
