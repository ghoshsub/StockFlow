package com.stockflow.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * Warehouse entity — represents a storage facility in the StockFlow system.
 *
 * Stored in the `warehouses` table.
 * Inherits createdAt / updatedAt audit fields from BaseEntity.
 *
 * Soft delete strategy:
 *   Instead of physically removing a row, we set active = false.
 */
@Entity
@Table(name = "warehouses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Warehouse extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Warehouse name — must be unique and required. */
    @NotBlank
    @Size(min = 2, max = 100)
    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;

    /** Unique warehouse identification code (e.g., WH-MUM-01). */
    @NotBlank
    @Size(min = 2, max = 20)
    @Column(name = "code", nullable = false, unique = true, length = 20)
    private String code;

    /** Legacy location text field (kept for DB compatibility). */
    @Size(max = 255)
    @Column(name = "location", length = 255)
    private String location;

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

    /** Postal / ZIP code. */
    @Size(max = 20)
    @Column(name = "postal_code", length = 20)
    private String postalCode;

    /** Total storage capacity (in units or sq ft) — non-negative. */
    @Min(0)
    @Column(name = "capacity")
    private Integer capacity;

    /** Warehouse manager's full name. */
    @Size(max = 100)
    @Column(name = "manager_name", length = 100)
    private String managerName;

    /** Warehouse manager's email address. */
    @Email
    @Size(max = 100)
    @Column(name = "manager_email", length = 100)
    private String managerEmail;

    /** Warehouse manager's phone number. */
    @Size(max = 20)
    @Column(name = "manager_phone", length = 20)
    private String managerPhone;

    /**
     * Soft-delete flag.
     * true  = warehouse is active.
     * false = warehouse is deactivated.
     * Defaults to true on creation.
     */
    @Builder.Default
    @Column(name = "active", nullable = false)
    private Boolean active = true;
}
