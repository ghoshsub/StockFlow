package com.stockflow.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * StockMovement entity — records every change to a product's stock quantity.
 *
 * Design Principle:
 *   Product.quantity is NEVER modified directly by CRUD operations.
 *   All quantity changes flow through StockMovement records:
 *    - STOCK_IN      → increases product.quantity
 *    - STOCK_OUT     → decreases product.quantity (fails if insufficient)
 *    - ADJUSTMENT    → sets product.quantity to a specific value
 *
 * Every movement is linked to:
 *    - The product affected
 *    - The warehouse where the movement occurred
 *    - The user who performed the operation
 */
@Entity
@Table(name = "stock_movements")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"product", "warehouse", "user"})
public class StockMovement extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Quantity of units moved.
     * For STOCK_IN / STOCK_OUT: always a positive delta.
     * For ADJUSTMENT: the new target quantity (negative means reducing to a value).
     */
    @NotNull
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    /** The type of movement: STOCK_IN, STOCK_OUT, or ADJUSTMENT. */
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "movement_type", nullable = false, length = 20)
    private MovementType movementType;

    /** Quantity BEFORE this movement was applied. Useful for audit/history. */
    @Column(name = "quantity_before", nullable = false)
    private Integer quantityBefore;

    /** Quantity AFTER this movement was applied. Snapshot for audit/history. */
    @Column(name = "quantity_after", nullable = false)
    private Integer quantityAfter;

    /** Timestamp of when the movement occurred. */
    @NotNull
    @Column(name = "movement_date", nullable = false)
    private LocalDateTime movementDate;

    /** Optional note / reason for the movement. */
    @Size(max = 500)
    @Column(name = "remarks", length = 500)
    private String remarks;

    /** The product whose stock was changed. */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    /** The warehouse where this stock movement occurred. */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    /** The user who performed this movement. */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
