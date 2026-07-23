package com.stockflow.backend.service;

import com.stockflow.backend.dto.inventory.*;
import com.stockflow.backend.dto.product.ProductResponse;
import com.stockflow.backend.entity.*;
import com.stockflow.backend.exception.*;
import com.stockflow.backend.mapper.InventoryMapper;
import com.stockflow.backend.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * InventoryServiceImpl — Business logic for stock management.
 * Enforces transactional safety and updates Product quantity ONLY via stock movements.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InventoryServiceImpl implements InventoryService {

    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;
    private final UserRepository userRepository;
    private final StockMovementRepository stockMovementRepository;
    private final InventoryMapper inventoryMapper;
    private final ProductService productService;

    @Override
    @Transactional
    public StockMovementResponse stockIn(StockInRequest request, String username) {
        log.info("Processing Stock In: productId={}, qty={}, user={}", request.getProductId(), request.getQuantity(), username);

        Product product = getActiveProduct(request.getProductId());
        Warehouse warehouse = getActiveWarehouse(request.getWarehouseId());
        User user = getUserByUsername(username);

        int qtyBefore = product.getQuantity();
        int qtyAfter = qtyBefore + request.getQuantity();

        product.setQuantity(qtyAfter);
        productRepository.save(product);

        StockMovement movement = StockMovement.builder()
                .product(product)
                .warehouse(warehouse)
                .user(user)
                .quantity(request.getQuantity())
                .quantityBefore(qtyBefore)
                .quantityAfter(qtyAfter)
                .movementType(MovementType.STOCK_IN)
                .movementDate(LocalDateTime.now())
                .remarks(request.getRemarks())
                .build();

        StockMovement saved = stockMovementRepository.save(movement);
        return inventoryMapper.toStockMovementResponse(saved);
    }

    @Override
    @Transactional
    public StockMovementResponse stockOut(StockOutRequest request, String username) {
        log.info("Processing Stock Out: productId={}, qty={}, user={}", request.getProductId(), request.getQuantity(), username);

        Product product = getActiveProduct(request.getProductId());
        Warehouse warehouse = getActiveWarehouse(request.getWarehouseId());
        User user = getUserByUsername(username);

        int qtyBefore = product.getQuantity();
        if (qtyBefore < request.getQuantity()) {
            throw new InsufficientStockException(product.getName(), qtyBefore, request.getQuantity());
        }

        int qtyAfter = qtyBefore - request.getQuantity();
        product.setQuantity(qtyAfter);
        productRepository.save(product);

        StockMovement movement = StockMovement.builder()
                .product(product)
                .warehouse(warehouse)
                .user(user)
                .quantity(request.getQuantity())
                .quantityBefore(qtyBefore)
                .quantityAfter(qtyAfter)
                .movementType(MovementType.STOCK_OUT)
                .movementDate(LocalDateTime.now())
                .remarks(request.getRemarks())
                .build();

        StockMovement saved = stockMovementRepository.save(movement);
        return inventoryMapper.toStockMovementResponse(saved);
    }

    @Override
    @Transactional
    public StockMovementResponse adjustStock(StockAdjustmentRequest request, String username) {
        log.info("Processing Stock Adjustment: productId={}, targetQty={}, user={}", request.getProductId(), request.getAdjustedQuantity(), username);

        Product product = getActiveProduct(request.getProductId());
        Warehouse warehouse = getActiveWarehouse(request.getWarehouseId());
        User user = getUserByUsername(username);

        int qtyBefore = product.getQuantity();
        int qtyAfter = request.getAdjustedQuantity();

        product.setQuantity(qtyAfter);
        productRepository.save(product);

        StockMovement movement = StockMovement.builder()
                .product(product)
                .warehouse(warehouse)
                .user(user)
                .quantity(qtyAfter - qtyBefore)
                .quantityBefore(qtyBefore)
                .quantityAfter(qtyAfter)
                .movementType(MovementType.ADJUSTMENT)
                .movementDate(LocalDateTime.now())
                .remarks(request.getRemarks())
                .build();

        StockMovement saved = stockMovementRepository.save(movement);
        return inventoryMapper.toStockMovementResponse(saved);
    }

    @Override
    public InventoryResponse getProductInventory(Long productId) {
        Product product = getActiveProduct(productId);
        LocalDateTime lastMovementAt = stockMovementRepository
                .findTopByProductIdOrderByMovementDateDesc(productId)
                .map(StockMovement::getMovementDate)
                .orElse(null);

        return inventoryMapper.toInventoryResponse(product, lastMovementAt);
    }

    @Override
    public Page<StockMovementResponse> getProductHistory(Long productId, Pageable pageable) {
        getActiveProduct(productId); // Ensures product exists
        Page<StockMovement> page = stockMovementRepository.findByProductIdOrderByMovementDateDesc(productId, pageable);
        return inventoryMapper.toStockMovementResponsePage(page);
    }

    @Override
    public Page<ProductResponse> getLowStockProducts(Pageable pageable) {
        return productService.getLowStockProducts(pageable);
    }

    @Override
    public Page<StockMovementResponse> searchMovements(
            Long productId,
            MovementType movementType,
            LocalDateTime dateFrom,
            LocalDateTime dateTo,
            String username,
            Pageable pageable) {
        Page<StockMovement> page = stockMovementRepository.searchMovements(
                productId, movementType, dateFrom, dateTo, username, pageable);
        return inventoryMapper.toStockMovementResponsePage(page);
    }

    // Helper methods
    private Product getActiveProduct(Long id) {
        return productRepository.findById(id)
                .filter(Product::getActive)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    private Warehouse getActiveWarehouse(Long id) {
        return warehouseRepository.findById(id)
                .filter(Warehouse::getActive)
                .orElseThrow(() -> new WarehouseNotFoundException("Warehouse not found or inactive with id: " + id));
    }

    private User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
    }
}
