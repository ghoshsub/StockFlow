package com.stockflow.backend.mapper;

import com.stockflow.backend.dto.product.ProductRequest;
import com.stockflow.backend.dto.product.ProductResponse;
import com.stockflow.backend.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ProductMapper — Converts Product entities to DTOs and handles request mapping.
 */
@Component
public class ProductMapper {

    private String trimOrNull(String str) {
        return (str != null && !str.isBlank()) ? str.trim() : null;
    }

    public Product toEntity(ProductRequest request, Category category, Brand brand, Supplier supplier, Warehouse warehouse, String sku) {
        return Product.builder()
                .sku(sku)
                .barcode(trimOrNull(request.getBarcode()))
                .name(trimOrNull(request.getName()))
                .description(trimOrNull(request.getDescription()))
                .buyingPrice(request.getBuyingPrice())
                .sellingPrice(request.getSellingPrice())
                .quantity(request.getQuantity() != null ? request.getQuantity() : 0)
                .minimumStock(request.getMinimumStock() != null ? request.getMinimumStock() : 5)
                .unit(trimOrNull(request.getUnit()))
                .weight(request.getWeight())
                .imageUrl(trimOrNull(request.getImageUrl()))
                .active(true)
                .category(category)
                .brand(brand)
                .supplier(supplier)
                .warehouse(warehouse)
                .build();
    }

    public void updateEntity(ProductRequest request, Product product, Category category, Brand brand, Supplier supplier, Warehouse warehouse) {
        if (request.getBarcode() != null) {
            product.setBarcode(trimOrNull(request.getBarcode()));
        }
        product.setName(trimOrNull(request.getName()));
        product.setDescription(trimOrNull(request.getDescription()));
        product.setBuyingPrice(request.getBuyingPrice());
        product.setSellingPrice(request.getSellingPrice());
        if (request.getQuantity() != null) {
            product.setQuantity(request.getQuantity());
        }
        if (request.getMinimumStock() != null) {
            product.setMinimumStock(request.getMinimumStock());
        }
        product.setUnit(trimOrNull(request.getUnit()));
        product.setWeight(request.getWeight());
        product.setImageUrl(trimOrNull(request.getImageUrl()));
        if (request.getActive() != null) {
            product.setActive(request.getActive());
        }
        product.setCategory(category);
        product.setBrand(brand);
        product.setSupplier(supplier);
        product.setWarehouse(warehouse);
    }

    public ProductResponse toResponse(Product product) {
        boolean isLowStock = product.getQuantity() <= product.getMinimumStock();

        return ProductResponse.builder()
                .id(product.getId())
                .sku(product.getSku())
                .barcode(product.getBarcode())
                .name(product.getName())
                .description(product.getDescription())
                .buyingPrice(product.getBuyingPrice())
                .sellingPrice(product.getSellingPrice())
                .quantity(product.getQuantity())
                .minimumStock(product.getMinimumStock())
                .isLowStock(isLowStock)
                .unit(product.getUnit())
                .weight(product.getWeight())
                .imageUrl(product.getImageUrl())
                .active(product.getActive())
                .category(product.getCategory() != null ? ProductResponse.CategorySummary.builder()
                        .id(product.getCategory().getId())
                        .name(product.getCategory().getName())
                        .build() : null)
                .brand(product.getBrand() != null ? ProductResponse.BrandSummary.builder()
                        .id(product.getBrand().getId())
                        .name(product.getBrand().getName())
                        .build() : null)
                .supplier(product.getSupplier() != null ? ProductResponse.SupplierSummary.builder()
                        .id(product.getSupplier().getId())
                        .name(product.getSupplier().getName())
                        .email(product.getSupplier().getEmail())
                        .build() : null)
                .warehouse(product.getWarehouse() != null ? ProductResponse.WarehouseSummary.builder()
                        .id(product.getWarehouse().getId())
                        .name(product.getWarehouse().getName())
                        .code(product.getWarehouse().getCode())
                        .build() : null)
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }

    public List<ProductResponse> toResponseList(List<Product> products) {
        return products.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public Page<ProductResponse> toResponsePage(Page<Product> page) {
        return page.map(this::toResponse);
    }
}
