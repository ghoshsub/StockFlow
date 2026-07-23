package com.stockflow.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * Supplier entity — represents a goods/product supplier in the StockFlow system.
 *
 * Stored in the `suppliers` table.
 * Inherits createdAt / updatedAt audit fields from BaseEntity.
 *
 * Soft delete strategy:
 *   Instead of physically removing a row (which would break FK references from purchases/products),
 *   we set active = false. All normal list queries filter on active = true by default.
 */
@Entity
@Table(name = "suppliers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Supplier extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Supplier company/business name — unique and required. */
    @NotBlank
    @Size(min = 2, max = 100)
    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;

    /** Primary point of contact person's name. */
    @Size(max = 100)
    @Column(name = "contact_person", length = 100)
    private String contactPerson;

    /** Supplier official email address — unique and required. */
    @NotBlank
    @Email
    @Size(max = 100)
    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    /** Supplier phone/mobile number. */
    @Size(max = 20)
    @Column(name = "phone", length = 20)
    private String phone;

    /** Goods and Services Tax Identification Number (GSTIN) — unique if provided. */
    @Size(max = 20)
    @Column(name = "gst_number", unique = true, length = 20)
    private String gstNumber;

    /** Physical street address. */
    @Size(max = 255)
    @Column(name = "address", length = 255)
    private String address;

    /** City name. */
    @Size(max = 50)
    @Column(name = "city", length = 50)
    private String city;

    /** State or Province name. */
    @Size(max = 50)
    @Column(name = "state", length = 50)
    private String state;

    /** Country name. */
    @Size(max = 50)
    @Column(name = "country", length = 50)
    private String country;

    /** Postal / PIN code. */
    @Size(max = 20)
    @Column(name = "postal_code", length = 20)
    private String postalCode;

    /** Official website URL. */
    @Size(max = 100)
    @Column(name = "website", length = 100)
    private String website;

    /**
     * Soft-delete flag.
     * true  = supplier is active and visible.
     * false = supplier has been "deleted" (hidden from normal queries).
     * Defaults to true on creation via @Builder.Default.
     */
    @Builder.Default
    @Column(name = "active", nullable = false)
    private Boolean active = true;
}
