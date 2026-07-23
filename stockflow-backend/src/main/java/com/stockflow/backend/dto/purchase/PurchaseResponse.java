package com.stockflow.backend.dto.purchase;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * PurchaseResponse — Outbound DTO for full purchase details.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Purchase order response details")
public class PurchaseResponse {

    @Schema(description = "Purchase ID", example = "1")
    private Long id;

    @Schema(description = "Purchase Number (Auto-generated)", example = "PO-20260722-B8K9L1")
    private String purchaseNumber;

    @Schema(description = "Supplier ID", example = "1")
    private Long supplierId;

    @Schema(description = "Supplier name", example = "TechDistributors Inc")
    private String supplierName;

    @Schema(description = "Warehouse ID", example = "1")
    private Long warehouseId;

    @Schema(description = "Warehouse name", example = "Central Logistics Hub")
    private String warehouseName;

    @Schema(description = "Purchase Date", example = "2026-07-22T21:15:00")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime purchaseDate;

    @Schema(description = "Invoice number", example = "INV-2026-0089")
    private String invoiceNumber;

    @Schema(description = "Payment status", example = "PAID")
    private String paymentStatus;

    @Schema(description = "Payment method", example = "BANK_TRANSFER")
    private String paymentMethod;

    @Schema(description = "Remarks", example = "Bulk restocking order")
    private String remarks;

    @Schema(description = "Total purchase amount", example = "1500.00")
    private BigDecimal totalAmount;

    @Schema(description = "Username who created the purchase", example = "admin")
    private String createdBy;

    @Schema(description = "Creation timestamp", example = "2026-07-22T21:15:00")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @Schema(description = "Line items in purchase order")
    private List<PurchaseItemResponse> items;
}
