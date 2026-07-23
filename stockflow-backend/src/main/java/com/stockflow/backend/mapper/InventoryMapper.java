package com.stockflow.backend.mapper;

import com.stockflow.backend.dto.inventory.InventoryResponse;
import com.stockflow.backend.dto.inventory.StockMovementResponse;
import com.stockflow.backend.entity.Product;
import com.stockflow.backend.entity.StockMovement;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * InventoryMapper — Converts StockMovement entities and Product entities into Inventory DTOs.
 */
@Component
public class InventoryMapper {

    public InventoryResponse toInventoryResponse(Product product, LocalDateTime lastMovementAt) {
        if (product == null) {
            return null;
        }

        boolean isLowStock = product.getQuantity() != null && product.getMinimumStock() != null
                && product.getQuantity() <= product.getMinimumStock();

        return InventoryResponse.builder()
                .productId(product.getId())
                .sku(product.getSku())
                .productName(product.getName())
                .warehouseId(product.getWarehouse() != null ? product.getWarehouse().getId() : null)
                .warehouseName(product.getWarehouse() != null ? product.getWarehouse().getName() : null)
                .warehouseCode(product.getWarehouse() != null ? product.getWarehouse().getCode() : null)
                .currentQuantity(product.getQuantity())
                .minimumStock(product.getMinimumStock())
                .isLowStock(isLowStock)
                .unit(product.getUnit())
                .lastMovementAt(lastMovementAt)
                .build();
    }

    public StockMovementResponse toStockMovementResponse(StockMovement movement) {
        if (movement == null) {
            return null;
        }

        return StockMovementResponse.builder()
                .id(movement.getId())
                .movementType(movement.getMovementType() != null ? movement.getMovementType().name() : null)
                .quantity(movement.getQuantity())
                .quantityBefore(movement.getQuantityBefore())
                .quantityAfter(movement.getQuantityAfter())
                .remarks(movement.getRemarks())
                .movementDate(movement.getMovementDate())
                .productId(movement.getProduct() != null ? movement.getProduct().getId() : null)
                .productSku(movement.getProduct() != null ? movement.getProduct().getSku() : null)
                .productName(movement.getProduct() != null ? movement.getProduct().getName() : null)
                .warehouseId(movement.getWarehouse() != null ? movement.getWarehouse().getId() : null)
                .warehouseName(movement.getWarehouse() != null ? movement.getWarehouse().getName() : null)
                .warehouseCode(movement.getWarehouse() != null ? movement.getWarehouse().getCode() : null)
                .performedBy(movement.getUser() != null ? movement.getUser().getUsername() : null)
                .createdAt(movement.getCreatedAt())
                .build();
    }

    public List<StockMovementResponse> toStockMovementResponseList(List<StockMovement> movements) {
        if (movements == null) {
            return List.of();
        }
        return movements.stream()
                .map(this::toStockMovementResponse)
                .toList();
    }

    public Page<StockMovementResponse> toStockMovementResponsePage(Page<StockMovement> page) {
        if (page == null) {
            return Page.empty();
        }
        return page.map(this::toStockMovementResponse);
    }
}
