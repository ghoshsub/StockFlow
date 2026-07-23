package com.stockflow.backend.dto.product;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

/**
 * ProductRequest — Inbound DTO for creating and updating a Product.
 */
@Data
@Schema(description = "Request payload for creating or updating a product")
public class ProductRequest {

    @Schema(description = "Unique Barcode (EAN/UPC/Custom)", example = "8901234567890")
    @Size(max = 50, message = "Barcode cannot exceed 50 characters")
    private String barcode;

    @Schema(description = "Product name", example = "Logitech MX Master 3S Wireless Mouse")
    @NotBlank(message = "Product name is required")
    @Size(min = 2, max = 150, message = "Product name must be between 2 and 150 characters")
    private String name;

    @Schema(description = "Product description", example = "Ergonomic wireless mouse with 8K DPI sensor")
    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    @Schema(description = "Cost / Buying Price", example = "75.00")
    @NotNull(message = "Buying price is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Buying price cannot be negative")
    private BigDecimal buyingPrice;

    @Schema(description = "Retail / Selling Price", example = "99.99")
    @NotNull(message = "Selling price is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Selling price cannot be negative")
    private BigDecimal sellingPrice;

    @Schema(description = "Initial inventory quantity", example = "50")
    @NotNull(message = "Quantity is required")
    @Min(value = 0, message = "Quantity cannot be negative")
    private Integer quantity;

    @Schema(description = "Low stock alert minimum threshold", example = "10")
    @NotNull(message = "Minimum stock threshold is required")
    @Min(value = 0, message = "Minimum stock cannot be negative")
    private Integer minimumStock;

    @Schema(description = "Unit of measurement", example = "PCS")
    @Size(max = 20, message = "Unit cannot exceed 20 characters")
    private String unit;

    @Schema(description = "Weight in kg/lbs", example = "0.14")
    private Double weight;

    @Schema(description = "Product image URL", example = "https://images.stockflow.com/mx-master-3s.jpg")
    @Size(max = 500, message = "Image URL cannot exceed 500 characters")
    private String imageUrl;

    @Schema(description = "Category ID", example = "1")
    @NotNull(message = "Category ID is required")
    private Long categoryId;

    @Schema(description = "Brand ID", example = "1")
    @NotNull(message = "Brand ID is required")
    private Long brandId;

    @Schema(description = "Supplier ID", example = "1")
    @NotNull(message = "Supplier ID is required")
    private Long supplierId;

    @Schema(description = "Warehouse ID", example = "1")
    @NotNull(message = "Warehouse ID is required")
    private Long warehouseId;

    @Schema(description = "Active status", example = "true", defaultValue = "true")
    private Boolean active;
}
