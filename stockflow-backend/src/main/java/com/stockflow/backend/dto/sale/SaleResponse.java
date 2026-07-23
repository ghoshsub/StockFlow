package com.stockflow.backend.dto.sale;

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
 * SaleResponse — Outbound DTO for full sales transaction details.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Sales order response details")
public class SaleResponse {

    @Schema(description = "Sale ID", example = "1")
    private Long id;

    @Schema(description = "Sale Number (Auto-generated)", example = "SO-20260722-X9Y8Z7")
    private String saleNumber;

    @Schema(description = "Customer Name", example = "John Doe")
    private String customerName;

    @Schema(description = "Customer Email", example = "john.doe@example.com")
    private String customerEmail;

    @Schema(description = "Customer Phone", example = "+1234567890")
    private String customerPhone;

    @Schema(description = "Warehouse ID", example = "1")
    private Long warehouseId;

    @Schema(description = "Warehouse Name", example = "Central Logistics Hub")
    private String warehouseName;

    @Schema(description = "Sale Date", example = "2026-07-22T21:20:00")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime saleDate;

    @Schema(description = "Payment Method", example = "CREDIT_CARD")
    private String paymentMethod;

    @Schema(description = "Payment Status", example = "PAID")
    private String paymentStatus;

    @Schema(description = "Discount", example = "10.00")
    private BigDecimal discount;

    @Schema(description = "Tax", example = "5.00")
    private BigDecimal tax;

    @Schema(description = "Final Total Amount (sum - discount + tax)", example = "194.98")
    private BigDecimal totalAmount;

    @Schema(description = "Remarks", example = "Retail sale at store counter")
    private String remarks;

    @Schema(description = "Username who processed the sale", example = "admin")
    private String createdBy;

    @Schema(description = "Creation timestamp", example = "2026-07-22T21:20:00")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @Schema(description = "Line items in sale transaction")
    private List<SaleItemResponse> items;
}
