package com.stockflow.backend.mapper;

import com.stockflow.backend.dto.purchase.PurchaseItemResponse;
import com.stockflow.backend.dto.purchase.PurchaseResponse;
import com.stockflow.backend.entity.Purchase;
import com.stockflow.backend.entity.PurchaseItem;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * PurchaseMapper — Converts Purchase and PurchaseItem entities to outbound DTOs.
 */
@Component
public class PurchaseMapper {

    public PurchaseItemResponse toPurchaseItemResponse(PurchaseItem item) {
        if (item == null) {
            return null;
        }

        return PurchaseItemResponse.builder()
                .id(item.getId())
                .productId(item.getProduct() != null ? item.getProduct().getId() : null)
                .productSku(item.getProduct() != null ? item.getProduct().getSku() : null)
                .productName(item.getProduct() != null ? item.getProduct().getName() : null)
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .subtotal(item.getSubtotal())
                .build();
    }

    public PurchaseResponse toPurchaseResponse(Purchase purchase) {
        if (purchase == null) {
            return null;
        }

        List<PurchaseItemResponse> itemResponses = purchase.getPurchaseItems() != null
                ? purchase.getPurchaseItems().stream().map(this::toPurchaseItemResponse).toList()
                : List.of();

        return PurchaseResponse.builder()
                .id(purchase.getId())
                .purchaseNumber(purchase.getPurchaseNumber())
                .supplierId(purchase.getSupplier() != null ? purchase.getSupplier().getId() : null)
                .supplierName(purchase.getSupplier() != null ? purchase.getSupplier().getName() : null)
                .warehouseId(purchase.getWarehouse() != null ? purchase.getWarehouse().getId() : null)
                .warehouseName(purchase.getWarehouse() != null ? purchase.getWarehouse().getName() : null)
                .purchaseDate(purchase.getPurchaseDate())
                .invoiceNumber(purchase.getInvoiceNumber())
                .paymentStatus(purchase.getPaymentStatus())
                .paymentMethod(purchase.getPaymentMethod())
                .remarks(purchase.getRemarks())
                .totalAmount(purchase.getTotalAmount())
                .createdBy(purchase.getCreatedBy() != null ? purchase.getCreatedBy().getUsername() : null)
                .createdAt(purchase.getCreatedAt())
                .items(itemResponses)
                .build();
    }

    public List<PurchaseResponse> toPurchaseResponseList(List<Purchase> purchases) {
        if (purchases == null) {
            return List.of();
        }
        return purchases.stream().map(this::toPurchaseResponse).toList();
    }

    public Page<PurchaseResponse> toPurchaseResponsePage(Page<Purchase> page) {
        if (page == null) {
            return Page.empty();
        }
        return page.map(this::toPurchaseResponse);
    }
}
