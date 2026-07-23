package com.stockflow.backend.mapper;

import com.stockflow.backend.dto.sale.SaleItemResponse;
import com.stockflow.backend.dto.sale.SaleResponse;
import com.stockflow.backend.entity.Sale;
import com.stockflow.backend.entity.SaleItem;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * SaleMapper — Converts Sale and SaleItem entities into response DTOs.
 */
@Component
public class SaleMapper {

    public SaleItemResponse toSaleItemResponse(SaleItem item) {
        if (item == null) {
            return null;
        }

        return SaleItemResponse.builder()
                .id(item.getId())
                .productId(item.getProduct() != null ? item.getProduct().getId() : null)
                .productSku(item.getProduct() != null ? item.getProduct().getSku() : null)
                .productName(item.getProduct() != null ? item.getProduct().getName() : null)
                .quantity(item.getQuantity())
                .sellingPrice(item.getSellingPrice())
                .subtotal(item.getSubtotal())
                .build();
    }

    public SaleResponse toSaleResponse(Sale sale) {
        if (sale == null) {
            return null;
        }

        List<SaleItemResponse> itemResponses = sale.getSaleItems() != null
                ? sale.getSaleItems().stream().map(this::toSaleItemResponse).toList()
                : List.of();

        return SaleResponse.builder()
                .id(sale.getId())
                .saleNumber(sale.getSaleNumber())
                .customerName(sale.getCustomerName())
                .customerEmail(sale.getCustomerEmail())
                .customerPhone(sale.getCustomerPhone())
                .warehouseId(sale.getWarehouse() != null ? sale.getWarehouse().getId() : null)
                .warehouseName(sale.getWarehouse() != null ? sale.getWarehouse().getName() : null)
                .saleDate(sale.getSaleDate())
                .paymentMethod(sale.getPaymentMethod())
                .paymentStatus(sale.getPaymentStatus())
                .discount(sale.getDiscount())
                .tax(sale.getTax())
                .totalAmount(sale.getTotalAmount())
                .remarks(sale.getRemarks())
                .createdBy(sale.getCreatedBy() != null ? sale.getCreatedBy().getUsername() : null)
                .createdAt(sale.getCreatedAt())
                .items(itemResponses)
                .build();
    }

    public List<SaleResponse> toSaleResponseList(List<Sale> sales) {
        if (sales == null) {
            return List.of();
        }
        return sales.stream().map(this::toSaleResponse).toList();
    }

    public Page<SaleResponse> toSaleResponsePage(Page<Sale> page) {
        if (page == null) {
            return Page.empty();
        }
        return page.map(this::toSaleResponse);
    }
}
