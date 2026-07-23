package com.stockflow.backend.dto.product;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * ProductResponse — Outbound DTO for product API responses.
 * Never exposes raw Entity objects.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Product data returned in API responses")
public class ProductResponse {

    @Schema(description = "Auto-generated product ID", example = "1")
    private Long id;

    @Schema(description = "Auto-generated SKU code", example = "PROD-2026-X8F9A")
    private String sku;

    @Schema(description = "Unique Barcode", example = "8901234567890")
    private String barcode;

    @Schema(description = "Product name", example = "Logitech MX Master 3S Wireless Mouse")
    private String name;

    @Schema(description = "Product description", example = "Ergonomic wireless mouse with 8K DPI sensor")
    private String description;

    @Schema(description = "Buying price", example = "75.00")
    private BigDecimal buyingPrice;

    @Schema(description = "Selling price", example = "99.99")
    private BigDecimal sellingPrice;

    @Schema(description = "Current stock quantity", example = "50")
    private Integer quantity;

    @Schema(description = "Minimum stock threshold for alerts", example = "10")
    private Integer minimumStock;

    @Schema(description = "Low stock alert indicator", example = "false")
    private Boolean isLowStock;

    @Schema(description = "Unit of measure", example = "PCS")
    private String unit;

    @Schema(description = "Weight in kg/lbs", example = "0.14")
    private Double weight;

    @Schema(description = "Product image URL", example = "https://images.stockflow.com/mx-master-3s.jpg")
    private String imageUrl;

    @Schema(description = "Active status", example = "true")
    private Boolean active;

    // ── Nested Summaries for Relationships ────────────────────────────────────

    @Schema(description = "Category details")
    private CategorySummary category;

    @Schema(description = "Brand details")
    private BrandSummary brand;

    @Schema(description = "Supplier details")
    private SupplierSummary supplier;

    @Schema(description = "Warehouse details")
    private WarehouseSummary warehouse;

    @Schema(description = "Creation timestamp", example = "2026-07-22T10:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @Schema(description = "Last update timestamp", example = "2026-07-22T12:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    // ── Static Inner Summary Classes ──────────────────────────────────────────

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CategorySummary {
        private Long id;
        private String name;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BrandSummary {
        private Long id;
        private String name;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SupplierSummary {
        private Long id;
        private String name;
        private String email;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class WarehouseSummary {
        private Long id;
        private String name;
        private String code;
    }
}
