package com.stockflow.backend.controller;

import com.stockflow.backend.dto.brand.BrandRequest;
import com.stockflow.backend.dto.brand.BrandResponse;
import com.stockflow.backend.service.BrandService;
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
 * BrandController — REST controller for the Brand module.
 *
 * Base path: /api/brands
 */
@RestController
@RequestMapping("/brands")
@RequiredArgsConstructor
@Tag(name = "Brands", description = "Manage product brands — CRUD, search, pagination, soft-delete")
@SecurityRequirement(name = "Bearer Authentication")
public class BrandController {

    private final BrandService brandService;

    // ── POST /api/brands ──────────────────────────────────────────────────────

    @Operation(summary = "Create a new brand", description = "Requires ADMIN role. Name must be unique.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Brand created successfully",
            content = @Content(schema = @Schema(implementation = BrandResponse.class))),
        @ApiResponse(responseCode = "400", description = "Validation failed"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden — ADMIN required"),
        @ApiResponse(responseCode = "409", description = "Brand name already exists")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BrandResponse> createBrand(@Valid @RequestBody BrandRequest request) {
        BrandResponse response = brandService.createBrand(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ── GET /api/brands/{id} ──────────────────────────────────────────────────

    @Operation(summary = "Get brand by ID", description = "All authenticated users can access.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Brand found",
            content = @Content(schema = @Schema(implementation = BrandResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "Brand not found")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<BrandResponse> getBrandById(
            @Parameter(description = "Brand ID", example = "1")
            @PathVariable Long id) {
        BrandResponse response = brandService.getBrandById(id);
        return ResponseEntity.ok(response);
    }

    // ── GET /api/brands (list all active brands) ─────────────────────────────

    @Operation(summary = "Get all active brands", description = "Returns active brands as a list.")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<List<BrandResponse>> getAllBrands() {
        List<BrandResponse> brands = brandService.getAllBrands();
        return ResponseEntity.ok(brands);
    }

    // ── GET /api/brands/page (paginated) ─────────────────────────────────────

    @Operation(summary = "Get paginated brands", description = "Returns a paginated and sorted list of active brands.")
    @GetMapping("/page")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<Page<BrandResponse>> getBrandsPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sort,
            @RequestParam(defaultValue = "ASC") String direction) {

        Sort sortObj = direction.equalsIgnoreCase("DESC")
                ? Sort.by(sort).descending()
                : Sort.by(sort).ascending();

        Pageable pageable = PageRequest.of(page, size, sortObj);
        Page<BrandResponse> response = brandService.getAllBrandsPaginated(pageable);
        return ResponseEntity.ok(response);
    }

    // ── PUT /api/brands/{id} ──────────────────────────────────────────────────

    @Operation(summary = "Update a brand", description = "Requires ADMIN role.")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BrandResponse> updateBrand(
            @PathVariable Long id,
            @Valid @RequestBody BrandRequest request) {
        BrandResponse response = brandService.updateBrand(id, request);
        return ResponseEntity.ok(response);
    }

    // ── DELETE /api/brands/{id} ───────────────────────────────────────────────

    @Operation(summary = "Soft-delete a brand", description = "Requires ADMIN role.")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> deleteBrand(@PathVariable Long id) {
        brandService.deleteBrand(id);
        return ResponseEntity.ok(Map.of(
                "message", "Brand with id " + id + " has been deactivated successfully"
        ));
    }

    // ── GET /api/brands/search?keyword= ──────────────────────────────────────

    @Operation(summary = "Search brands by keyword", description = "Partial match on name or description.")
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<Page<BrandResponse>> searchBrands(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sort,
            @RequestParam(defaultValue = "ASC") String direction) {

        Sort sortObj = direction.equalsIgnoreCase("DESC")
                ? Sort.by(sort).descending()
                : Sort.by(sort).ascending();

        Pageable pageable = PageRequest.of(page, size, sortObj);
        Page<BrandResponse> response = brandService.searchBrands(keyword, pageable);
        return ResponseEntity.ok(response);
    }
}
