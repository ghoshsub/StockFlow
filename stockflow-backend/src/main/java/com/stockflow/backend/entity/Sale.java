package com.stockflow.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Sale entity — represents a customer sales transaction.
 */
@Entity
@Table(name = "sales")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"saleItems", "createdBy", "warehouse"})
public class Sale extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 3, max = 50)
    @Column(name = "sale_number", nullable = false, unique = true, length = 50)
    private String saleNumber;

    @Size(max = 100)
    @Column(name = "customer_name", length = 100)
    private String customerName;

    @Size(max = 100)
    @Column(name = "customer_email", length = 100)
    private String customerEmail;

    @Size(max = 30)
    @Column(name = "customer_phone", length = 30)
    private String customerPhone;

    @NotNull
    @Column(name = "sale_date", nullable = false)
    private LocalDateTime saleDate;

    @NotBlank
    @Size(max = 30)
    @Column(name = "payment_method", nullable = false, length = 30)
    private String paymentMethod;

    @NotBlank
    @Size(max = 30)
    @Column(name = "payment_status", nullable = false, length = 30)
    private String paymentStatus;

    @Builder.Default
    @DecimalMin("0.0")
    @Column(name = "discount", nullable = false, precision = 12, scale = 2)
    private BigDecimal discount = BigDecimal.ZERO;

    @Builder.Default
    @DecimalMin("0.0")
    @Column(name = "tax", nullable = false, precision = 12, scale = 2)
    private BigDecimal tax = BigDecimal.ZERO;

    @NotNull
    @DecimalMin("0.0")
    @Column(name = "total_amount", nullable = false, precision = 14, scale = 2)
    private BigDecimal totalAmount;

    @Size(max = 500)
    @Column(name = "remarks", length = 500)
    private String remarks;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id", nullable = false)
    private User createdBy;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    @Builder.Default
    @OneToMany(mappedBy = "sale", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<SaleItem> saleItems = new ArrayList<>();

    public void addSaleItem(SaleItem item) {
        saleItems.add(item);
        item.setSale(this);
    }

    public void removeSaleItem(SaleItem item) {
        saleItems.remove(item);
        item.setSale(null);
    }
}
