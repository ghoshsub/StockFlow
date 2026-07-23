package com.stockflow.backend.controller;

import com.stockflow.backend.dto.purchase.PurchaseRequest;
import com.stockflow.backend.dto.purchase.PurchaseResponse;
import com.stockflow.backend.service.PurchaseService;
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
import java.util.List;

/**
 * PurchaseController — REST controller for Purchase order management.
 */
@RestController
@RequestMapping("/purchases")
@RequiredArgsConstructor
@Tag(name = "Purchases", description = "Purchase Order management — Create purchases, track inventory integration, search & filter")
@SecurityRequirement(name = "Bearer Authentication")
public class PurchaseController {

    private final PurchaseService purchaseService;

    @Operation(summary = "Create a new purchase order", description = "Auto-generates PO Number, calculates totals, and automatically triggers Stock In for inventory. Requires ADMIN or STAFF.")
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<PurchaseResponse> createPurchase(
            @Valid @RequestBody PurchaseRequest request,
            Authentication authentication) {
        PurchaseResponse response = purchaseService.createPurchase(request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Get all purchases (flat list)", description = "Returns all purchases. Accessible by ADMIN and STAFF.")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<List<PurchaseResponse>> getAllPurchases() {
        return ResponseEntity.ok(purchaseService.getAllPurchases());
    }

    @Operation(summary = "Get purchase by ID", description = "Returns full details of a specific purchase order.")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<PurchaseResponse> getPurchaseById(@PathVariable Long id) {
        return ResponseEntity.ok(purchaseService.getPurchaseById(id));
    }

    @Operation(summary = "Get purchases (paginated)", description = "Supports pagination and sorting. Sort fields: purchaseDate, totalAmount, purchaseNumber.")
    @GetMapping("/page")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<Page<PurchaseResponse>> getAllPurchasesPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "purchaseDate") String sort,
            @RequestParam(defaultValue = "DESC") String direction) {
        Pageable pageable = buildPageable(page, size, sort, direction);
        return ResponseEntity.ok(purchaseService.getAllPurchasesPaginated(pageable));
    }

    @Operation(summary = "Search purchases", description = "Search by Purchase Number, Supplier Name, Invoice Number, Product Name/SKU, or Date Range.")
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<Page<PurchaseResponse>> searchPurchases(
            @RequestParam(required = false) String purchaseNumber,
            @RequestParam(required = false) String supplierName,
            @RequestParam(required = false) String invoiceNumber,
            @RequestParam(required = false) String productKeyword,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "purchaseDate") String sort,
            @RequestParam(defaultValue = "DESC") String direction) {
        Pageable pageable = buildPageable(page, size, sort, direction);
        return ResponseEntity.ok(purchaseService.searchPurchases(
                purchaseNumber, supplierName, invoiceNumber, productKeyword, dateFrom, dateTo, pageable));
    }

    @Operation(summary = "Filter purchases", description = "Filter by Supplier ID, Warehouse ID, Payment Status, or Date Range.")
    @GetMapping("/filter")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<Page<PurchaseResponse>> filterPurchases(
            @RequestParam(required = false) Long supplierId,
            @RequestParam(required = false) Long warehouseId,
            @RequestParam(required = false) String paymentStatus,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "purchaseDate") String sort,
            @RequestParam(defaultValue = "DESC") String direction) {
        Pageable pageable = buildPageable(page, size, sort, direction);
        return ResponseEntity.ok(purchaseService.filterPurchases(
                supplierId, warehouseId, paymentStatus, dateFrom, dateTo, pageable));
    }

    private Pageable buildPageable(int page, int size, String sort, String direction) {
        Sort sortObj = direction.equalsIgnoreCase("DESC")
                ? Sort.by(sort).descending()
                : Sort.by(sort).ascending();
        return PageRequest.of(page, size, sortObj);
    }
}
