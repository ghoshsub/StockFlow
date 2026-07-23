package com.stockflow.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * Product entity — represents a master product catalog item in StockFlow.
 *
 * Stored in the `products` table.
 * Inherits createdAt / updatedAt audit fields from BaseEntity.
 */
@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"category", "brand", "supplier", "warehouse"})
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Unique Stock Keeping Unit — Auto generated (e.g., PROD-2026-X8F9A). */
    @NotBlank
    @Size(min = 3, max = 50)
    @Column(name = "sku", nullable = false, unique = true, length = 50)
    private String sku;

    /** Unique Barcode (EAN/UPC/Custom). */
    @Size(max = 50)
    @Column(name = "barcode", unique = true, length = 50)
    private String barcode;

    /** Product name. */
    @NotBlank
    @Size(min = 2, max = 150)
    @Column(name = "name", nullable = false, length = 150)
    private String name;

    /** Product detailed description. */
    @Size(max = 1000)
    @Column(name = "description", length = 1000)
    private String description;

    /** Cost / Buying price — cannot be negative. */
    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    @Column(name = "buying_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal buyingPrice;

    /** Retail / Selling price — must be greater than or equal to buying price. */
    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    @Column(name = "selling_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal sellingPrice;

    /** Current available stock quantity — non-negative. */
    @NotNull
    @Min(0)
    @Builder.Default
    @Column(name = "quantity", nullable = false)
    private Integer quantity = 0;

    /** Minimum threshold for low stock alert — non-negative. */
    @NotNull
    @Min(0)
    @Builder.Default
    @Column(name = "minimum_stock", nullable = false)
    private Integer minimumStock = 5;

    /** Unit of measure (e.g., PCS, KG, BOX, LTR). */
    @Size(max = 20)
    @Column(name = "unit", length = 20)
    private String unit;

    /** Item weight in kg/units. */
    @Column(name = "weight")
    private Double weight;

    /** Image URL for product preview. */
    @Size(max = 500)
    @Column(name = "image_url", length = 500)
    private String imageUrl;

    /** Soft-delete active flag. */
    @Builder.Default
    @Column(name = "active", nullable = false)
    private Boolean active = true;

    // ── Relationships ─────────────────────────────────────────────────────────

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id", nullable = false)
    private Brand brand;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;
}
