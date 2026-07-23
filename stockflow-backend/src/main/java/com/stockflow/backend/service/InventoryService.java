package com.stockflow.backend.service;

import com.stockflow.backend.dto.inventory.*;
import com.stockflow.backend.dto.product.ProductResponse;
import com.stockflow.backend.entity.MovementType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

/**
 * InventoryService — Core service interface for Inventory & Stock Management.
 */
public interface InventoryService {

    /**
     * Stock In — Increases product stock quantity and logs STOCK_IN movement.
     */
    StockMovementResponse stockIn(StockInRequest request, String username);

    /**
     * Stock Out — Decreases product stock quantity and logs STOCK_OUT movement.
     * Throws InsufficientStockException if requested > available.
     */
    StockMovementResponse stockOut(StockOutRequest request, String username);

    /**
     * Stock Adjustment — Sets product stock quantity to target absolute value and logs ADJUSTMENT movement.
     */
    StockMovementResponse adjustStock(StockAdjustmentRequest request, String username);

    /**
     * Get current inventory state for a product.
     */
    InventoryResponse getProductInventory(Long productId);

    /**
     * Get paginated stock movement history for a product.
     */
    Page<StockMovementResponse> getProductHistory(Long productId, Pageable pageable);

    /**
     * Get low stock products (quantity <= minimumStock).
     */
    Page<ProductResponse> getLowStockProducts(Pageable pageable);

    /**
     * Advanced search for stock movements.
     */
    Page<StockMovementResponse> searchMovements(
            Long productId,
            MovementType movementType,
            LocalDateTime dateFrom,
            LocalDateTime dateTo,
            String username,
            Pageable pageable);
}
