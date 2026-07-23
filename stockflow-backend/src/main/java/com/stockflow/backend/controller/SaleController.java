package com.stockflow.backend.controller;

import com.stockflow.backend.dto.sale.SaleRequest;
import com.stockflow.backend.dto.sale.SaleResponse;
import com.stockflow.backend.service.SaleService;
import io.swagger.v3.oas.annotations.Operation;
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
 * SaleController — REST controller for Sales order management.
 */
@RestController
@RequestMapping("/sales")
@RequiredArgsConstructor
@Tag(name = "Sales", description = "Sales Order management — Process sales transactions, automatic stock-out integration, search & filter")
@SecurityRequirement(name = "Bearer Authentication")
public class SaleController {

    private final SaleService saleService;

    @Operation(summary = "Create a new sale order", description = "Auto-generates SO Number, validates stock availability, calculates total, and triggers Stock Out. Requires ADMIN or STAFF.")
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<SaleResponse> createSale(
            @Valid @RequestBody SaleRequest request,
            Authentication authentication) {
        SaleResponse response = saleService.createSale(request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Get all sales (flat list)", description = "Returns all sales transactions. Accessible by ADMIN and STAFF.")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<List<SaleResponse>> getAllSales() {
        return ResponseEntity.ok(saleService.getAllSales());
    }

    @Operation(summary = "Get sale by ID", description = "Returns full details of a specific sale transaction.")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<SaleResponse> getSaleById(@PathVariable Long id) {
        return ResponseEntity.ok(saleService.getSaleById(id));
    }

    @Operation(summary = "Get sales (paginated)", description = "Supports pagination and sorting. Sort fields: saleDate, totalAmount, saleNumber.")
    @GetMapping("/page")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<Page<SaleResponse>> getAllSalesPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "saleDate") String sort,
            @RequestParam(defaultValue = "DESC") String direction) {
        Pageable pageable = buildPageable(page, size, sort, direction);
        return ResponseEntity.ok(saleService.getAllSalesPaginated(pageable));
    }

    @Operation(summary = "Search sales", description = "Search by Sale Number, Customer Name, Customer Email, Product Name/SKU, or Date Range.")
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<Page<SaleResponse>> searchSales(
            @RequestParam(required = false) String saleNumber,
            @RequestParam(required = false) String customerName,
            @RequestParam(required = false) String customerEmail,
            @RequestParam(required = false) String productKeyword,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "saleDate") String sort,
            @RequestParam(defaultValue = "DESC") String direction) {
        Pageable pageable = buildPageable(page, size, sort, direction);
        return ResponseEntity.ok(saleService.searchSales(
                saleNumber, customerName, customerEmail, productKeyword, dateFrom, dateTo, pageable));
    }

    @Operation(summary = "Filter sales", description = "Filter by Payment Status, Payment Method, Customer Name, or Date Range.")
    @GetMapping("/filter")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<Page<SaleResponse>> filterSales(
            @RequestParam(required = false) String paymentStatus,
            @RequestParam(required = false) String paymentMethod,
            @RequestParam(required = false) String customerName,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "saleDate") String sort,
            @RequestParam(defaultValue = "DESC") String direction) {
        Pageable pageable = buildPageable(page, size, sort, direction);
        return ResponseEntity.ok(saleService.filterSales(
                paymentStatus, paymentMethod, customerName, dateFrom, dateTo, pageable));
    }

    private Pageable buildPageable(int page, int size, String sort, String direction) {
        Sort sortObj = direction.equalsIgnoreCase("DESC")
                ? Sort.by(sort).descending()
                : Sort.by(sort).ascending();
        return PageRequest.of(page, size, sortObj);
    }
}
