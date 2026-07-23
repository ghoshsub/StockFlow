package com.stockflow.backend.controller;

import com.stockflow.backend.dto.supplier.SupplierRequest;
import com.stockflow.backend.dto.supplier.SupplierResponse;
import com.stockflow.backend.service.SupplierService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * SupplierController — REST controller for Supplier module.
 * Base path: /api/suppliers
 */
@RestController
@RequestMapping("/suppliers")
@RequiredArgsConstructor
@Tag(name = "Suppliers", description = "Manage product suppliers — CRUD, search, pagination, soft-delete")
@SecurityRequirement(name = "Bearer Authentication")
public class SupplierController {

    private final SupplierService supplierService;

    // ── POST /api/suppliers ───────────────────────────────────────────────────

    @Operation(summary = "Create a new supplier", description = "Requires ADMIN role. Name, email & GST must be unique.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Supplier created successfully",
            content = @Content(schema = @Schema(implementation = SupplierResponse.class))),
        @ApiResponse(responseCode = "400", description = "Validation failed"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden — ADMIN required"),
        @ApiResponse(responseCode = "409", description = "Name, Email or GST already exists")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SupplierResponse> createSupplier(@Valid @RequestBody SupplierRequest request) {
        SupplierResponse response = supplierService.createSupplier(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ── GET /api/suppliers/{id} ───────────────────────────────────────────────

    @Operation(summary = "Get supplier by ID", description = "Accessible by ADMIN and STAFF.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Supplier found",
            content = @Content(schema = @Schema(implementation = SupplierResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "Supplier not found")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<SupplierResponse> getSupplierById(@PathVariable Long id) {
        SupplierResponse response = supplierService.getSupplierById(id);
        return ResponseEntity.ok(response);
    }

    // ── GET /api/suppliers (list all active suppliers) ───────────────────────

    @Operation(summary = "Get all active suppliers", description = "Returns active suppliers as a list.")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<List<SupplierResponse>> getAllSuppliers() {
        List<SupplierResponse> suppliers = supplierService.getAllSuppliers();
        return ResponseEntity.ok(suppliers);
    }

    // ── GET /api/suppliers/page (paginated) ───────────────────────────────────

    @Operation(summary = "Get paginated suppliers", description = "Returns a paginated and sorted list of active suppliers.")
    @GetMapping("/page")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<Page<SupplierResponse>> getSuppliersPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sort,
            @RequestParam(defaultValue = "ASC") String direction) {

        Sort sortObj = direction.equalsIgnoreCase("DESC")
                ? Sort.by(sort).descending()
                : Sort.by(sort).ascending();

        Pageable pageable = PageRequest.of(page, size, sortObj);
        Page<SupplierResponse> response = supplierService.getAllSuppliersPaginated(pageable);
        return ResponseEntity.ok(response);
    }

    // ── PUT /api/suppliers/{id} ───────────────────────────────────────────────

    @Operation(summary = "Update a supplier", description = "Requires ADMIN role.")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SupplierResponse> updateSupplier(
            @PathVariable Long id,
            @Valid @RequestBody SupplierRequest request) {
        SupplierResponse response = supplierService.updateSupplier(id, request);
        return ResponseEntity.ok(response);
    }

    // ── DELETE /api/suppliers/{id} ────────────────────────────────────────────

    @Operation(summary = "Soft-delete a supplier", description = "Requires ADMIN role.")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> deleteSupplier(@PathVariable Long id) {
        supplierService.deleteSupplier(id);
        return ResponseEntity.ok(Map.of(
                "message", "Supplier with id " + id + " has been deactivated successfully"
        ));
    }

    // ── GET /api/suppliers/search?keyword= ───────────────────────────────────

    @Operation(summary = "Search suppliers", description = "Partial match on name, contact person, email, phone, or city.")
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<Page<SupplierResponse>> searchSuppliers(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sort,
            @RequestParam(defaultValue = "ASC") String direction) {

        Sort sortObj = direction.equalsIgnoreCase("DESC")
                ? Sort.by(sort).descending()
                : Sort.by(sort).ascending();

        Pageable pageable = PageRequest.of(page, size, sortObj);
        Page<SupplierResponse> response = supplierService.searchSuppliers(keyword, pageable);
        return ResponseEntity.ok(response);
    }
}
