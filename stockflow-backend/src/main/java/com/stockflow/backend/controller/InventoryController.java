package com.stockflow.backend.controller;

import com.stockflow.backend.dto.inventory.*;
import com.stockflow.backend.dto.product.ProductResponse;
import com.stockflow.backend.entity.MovementType;
import com.stockflow.backend.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * InventoryController — REST API endpoints for Inventory & Stock Management.
 */
@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
@Tag(name = "Inventory", description = "Inventory & Stock Management — Stock In, Stock Out, Adjustment, History & Low Stock")
@SecurityRequirement(name = "Bearer Authentication")
public class InventoryController {

    private final InventoryService inventoryService;

    @Operation(summary = "Stock In (Add stock)", description = "Increases product stock. Requires ADMIN only.")
    @PostMapping("/stock-in")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StockMovementResponse> stockIn(
            @Valid @RequestBody StockInRequest request,
            Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(inventoryService.stockIn(request, authentication.getName()));
    }

    @Operation(summary = "Stock Out (Reduce stock)", description = "Decreases product stock. Requires ADMIN only.")
    @PostMapping("/stock-out")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StockMovementResponse> stockOut(
            @Valid @RequestBody StockOutRequest request,
            Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(inventoryService.stockOut(request, authentication.getName()));
    }

    @Operation(summary = "Stock Adjustment", description = "Adjusts stock to target quantity. Requires ADMIN only.")
    @PostMapping("/adjust")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StockMovementResponse> adjustStock(
            @Valid @RequestBody StockAdjustmentRequest request,
            Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(inventoryService.adjustStock(request, authentication.getName()));
    }

    @Operation(summary = "Get product inventory snapshot", description = "Get current quantity and low stock state. Accessible by ADMIN and STAFF.")
    @GetMapping("/product/{productId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<InventoryResponse> getProductInventory(@PathVariable Long productId) {
        return ResponseEntity.ok(inventoryService.getProductInventory(productId));
    }

    @Operation(summary = "Get product stock history", description = "Get paginated movement history for a product. Accessible by ADMIN and STAFF.")
    @GetMapping("/history/{productId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<Page<StockMovementResponse>> getProductHistory(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "movementDate") String sort,
            @RequestParam(defaultValue = "DESC") String direction) {
        Pageable pageable = buildPageable(page, size, sort, direction);
        return ResponseEntity.ok(inventoryService.getProductHistory(productId, pageable));
    }

    @Operation(summary = "Get low stock products", description = "Get products where quantity <= minimumStock. Accessible by ADMIN and STAFF.")
    @GetMapping("/low-stock")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<Page<ProductResponse>> getLowStockProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "quantity") String sort,
            @RequestParam(defaultValue = "ASC") String direction) {
        Pageable pageable = buildPageable(page, size, sort, direction);
        return ResponseEntity.ok(inventoryService.getLowStockProducts(pageable));
    }

    @Operation(summary = "Search stock movements", description = "Filter movements by product, movement type, date range, user. Accessible by ADMIN and STAFF.")
    @GetMapping("/movements/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<Page<StockMovementResponse>> searchMovements(
            @RequestParam(required = false) Long productId,
            @RequestParam(required = false) MovementType movementType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTo,
            @RequestParam(required = false) String username,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "movementDate") String sort,
            @RequestParam(defaultValue = "DESC") String direction) {
        Pageable pageable = buildPageable(page, size, sort, direction);
        return ResponseEntity.ok(inventoryService.searchMovements(
                productId, movementType, dateFrom, dateTo, username, pageable));
    }

    private Pageable buildPageable(int page, int size, String sort, String direction) {
        Sort sortObj = direction.equalsIgnoreCase("DESC")
                ? Sort.by(sort).descending()
                : Sort.by(sort).ascending();
        return PageRequest.of(page, size, sortObj);
    }
}
