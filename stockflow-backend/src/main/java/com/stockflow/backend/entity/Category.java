package com.stockflow.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * Category entity — represents a product category in the StockFlow system.
 *
 * Stored in the `categories` table.
 * Inherits createdAt / updatedAt audit fields from BaseEntity.
 *
 * Soft delete strategy:
 *   Instead of physically removing a row (which would break FK references from products),
 *   we set active = false. All queries filter on active = true by default.
 */
@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Category name — must be unique and between 2-100 characters. */
    @NotBlank
    @Size(min = 2, max = 100)
    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;

    /** Optional human-readable description of the category. */
    @Size(max = 255)
    @Column(name = "description")
    private String description;

    /**
     * Soft-delete flag.
     * true  = category is active and visible.
     * false = category has been "deleted" (hidden from normal queries).
     * Defaults to true on creation via @Builder.Default.
     */
    @Builder.Default
    @Column(name = "active", nullable = false)
    private Boolean active = true;
}
