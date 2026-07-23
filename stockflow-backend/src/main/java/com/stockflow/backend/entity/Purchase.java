package com.stockflow.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Purchase entity — represents a purchase order from a supplier.
 */
@Entity
@Table(name = "purchases")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"purchaseItems", "supplier", "warehouse", "createdBy"})
public class Purchase extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 3, max = 50)
    @Column(name = "purchase_number", nullable = false, unique = true, length = 50)
    private String purchaseNumber;

    @NotNull
    @Column(name = "purchase_date", nullable = false)
    private LocalDateTime purchaseDate;

    @Size(max = 100)
    @Column(name = "invoice_number", unique = true, length = 100)
    private String invoiceNumber;

    @NotBlank
    @Size(max = 30)
    @Column(name = "payment_status", nullable = false, length = 30)
    private String paymentStatus;

    @Size(max = 30)
    @Column(name = "payment_method", length = 30)
    private String paymentMethod;

    @Size(max = 500)
    @Column(name = "remarks", length = 500)
    private String remarks;

    @NotNull
    @DecimalMin("0.0")
    @Column(name = "total_amount", nullable = false, precision = 14, scale = 2)
    private BigDecimal totalAmount;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id", nullable = false)
    private User createdBy;

    @Builder.Default
    @OneToMany(mappedBy = "purchase", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<PurchaseItem> purchaseItems = new ArrayList<>();

    public void addPurchaseItem(PurchaseItem item) {
        purchaseItems.add(item);
        item.setPurchase(this);
    }

    public void removePurchaseItem(PurchaseItem item) {
        purchaseItems.remove(item);
        item.setPurchase(null);
    }
}
