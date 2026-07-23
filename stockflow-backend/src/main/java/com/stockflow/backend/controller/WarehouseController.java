package com.stockflow.backend.controller;

import com.stockflow.backend.dto.warehouse.WarehouseRequest;
import com.stockflow.backend.dto.warehouse.WarehouseResponse;
import com.stockflow.backend.service.WarehouseService;
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
 * WarehouseController — REST controller for Warehouse module.
 * Base path: /api/warehouses
 */
@RestController
@RequestMapping("/warehouses")
@RequiredArgsConstructor
@Tag(name = "Warehouses", description = "Manage storage facilities — CRUD, search, pagination, soft-delete")
@SecurityRequirement(name = "Bearer Authentication")
public class WarehouseController {

    private final WarehouseService warehouseService;

    // ── POST /api/warehouses ──────────────────────────────────────────────────

    @Operation(summary = "Create a new warehouse", description = "Requires ADMIN role. Name and Code must be unique.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Warehouse created successfully",
            content = @Content(schema = @Schema(implementation = WarehouseResponse.class))),
        @ApiResponse(responseCode = "400", description = "Validation failed"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden — ADMIN required"),
        @ApiResponse(responseCode = "409", description = "Name or Code already exists")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<WarehouseResponse> createWarehouse(@Valid @RequestBody WarehouseRequest request) {
        WarehouseResponse response = warehouseService.createWarehouse(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ── GET /api/warehouses/{id} ──────────────────────────────────────────────

    @Operation(summary = "Get warehouse by ID", description = "Accessible by ADMIN and STAFF.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Warehouse found",
            content = @Content(schema = @Schema(implementation = WarehouseResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "Warehouse not found")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<WarehouseResponse> getWarehouseById(@PathVariable Long id) {
        WarehouseResponse response = warehouseService.getWarehouseById(id);
        return ResponseEntity.ok(response);
    }

    // ── GET /api/warehouses (list all active warehouses) ────────────────────

    @Operation(summary = "Get all active warehouses", description = "Returns active warehouses as a list.")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<List<WarehouseResponse>> getAllWarehouses() {
        List<WarehouseResponse> warehouses = warehouseService.getAllWarehouses();
        return ResponseEntity.ok(warehouses);
    }

    // ── GET /api/warehouses/page (paginated) ──────────────────────────────────

    @Operation(summary = "Get paginated warehouses", description = "Returns a paginated and sorted list of active warehouses.")
    @GetMapping("/page")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<Page<WarehouseResponse>> getWarehousesPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sort,
            @RequestParam(defaultValue = "ASC") String direction) {

        Sort sortObj = direction.equalsIgnoreCase("DESC")
                ? Sort.by(sort).descending()
                : Sort.by(sort).ascending();

        Pageable pageable = PageRequest.of(page, size, sortObj);
        Page<WarehouseResponse> response = warehouseService.getAllWarehousesPaginated(pageable);
        return ResponseEntity.ok(response);
    }

    // ── PUT /api/warehouses/{id} ──────────────────────────────────────────────

    @Operation(summary = "Update a warehouse", description = "Requires ADMIN role.")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<WarehouseResponse> updateWarehouse(
            @PathVariable Long id,
            @Valid @RequestBody WarehouseRequest request) {
        WarehouseResponse response = warehouseService.updateWarehouse(id, request);
        return ResponseEntity.ok(response);
    }

    // ── DELETE /api/warehouses/{id} ───────────────────────────────────────────

    @Operation(summary = "Soft-delete a warehouse", description = "Requires ADMIN role.")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> deleteWarehouse(@PathVariable Long id) {
        warehouseService.deleteWarehouse(id);
        return ResponseEntity.ok(Map.of(
                "message", "Warehouse with id " + id + " has been deactivated successfully"
        ));
    }

    // ── GET /api/warehouses/search?keyword= ──────────────────────────────────

    @Operation(summary = "Search warehouses", description = "Partial match on name, code, city, or manager name.")
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<Page<WarehouseResponse>> searchWarehouses(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sort,
            @RequestParam(defaultValue = "ASC") String direction) {

        Sort sortObj = direction.equalsIgnoreCase("DESC")
                ? Sort.by(sort).descending()
                : Sort.by(sort).ascending();

        Pageable pageable = PageRequest.of(page, size, sortObj);
        Page<WarehouseResponse> response = warehouseService.searchWarehouses(keyword, pageable);
        return ResponseEntity.ok(response);
    }
}
