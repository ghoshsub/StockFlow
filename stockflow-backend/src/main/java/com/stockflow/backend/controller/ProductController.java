package com.stockflow.backend.controller;

import com.stockflow.backend.dto.product.ProductRequest;
import com.stockflow.backend.dto.product.ProductResponse;
import com.stockflow.backend.service.ProductService;
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
 * ProductController — REST controller for the Product module.
 *
 * Base path: /api/products
 *
 * Security:
 *  ADMIN   → Create, Update, Delete
 *  STAFF   → View Only, Create, Update (based on business rules)
 */
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@Tag(name = "Products", description = "Product catalog management — CRUD, search, filter, low-stock, pagination")
@SecurityRequirement(name = "Bearer Authentication")
public class ProductController {

    private final ProductService productService;

    // ── POST /api/products ────────────────────────────────────────────────────

    @Operation(
        summary = "Create a new product",
        description = "SKU is auto-generated. Selling price must be > buying price. Requires ADMIN or STAFF."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Product created",
            content = @Content(schema = @Schema(implementation = ProductResponse.class))),
        @ApiResponse(responseCode = "400", description = "Validation failed / selling price <= buying price"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "404", description = "Category / Brand / Supplier / Warehouse not found"),
        @ApiResponse(responseCode = "409", description = "Barcode already exists")
    })
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest request) {
        ProductResponse response = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ── GET /api/products (flat list) ─────────────────────────────────────────

    @Operation(summary = "Get all active products (flat list)", description = "Returns all active products. Suitable for dropdowns.")
    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    // ── GET /api/products (paginated) ─────────────────────────────────────────

    @Operation(
        summary = "Get all active products (paginated)",
        description = "Supports pagination and sorting. Sort fields: name, sellingPrice, quantity, createdAt."
    )
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<Page<ProductResponse>> getAllProductsPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sort,
            @RequestParam(defaultValue = "ASC") String direction) {
        Pageable pageable = buildPageable(page, size, sort, direction);
        return ResponseEntity.ok(productService.getAllProductsPaginated(pageable));
    }

    // ── GET /api/products/{id} ────────────────────────────────────────────────

    @Operation(summary = "Get product by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Product found",
            content = @Content(schema = @Schema(implementation = ProductResponse.class))),
        @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    // ── PUT /api/products/{id} ────────────────────────────────────────────────

    @Operation(summary = "Update a product", description = "Requires ADMIN or STAFF.")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request) {
        return ResponseEntity.ok(productService.updateProduct(id, request));
    }

    // ── DELETE /api/products/{id} ─────────────────────────────────────────────

    @Operation(summary = "Soft-delete a product", description = "Requires ADMIN only.")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(Map.of("message", "Product with id " + id + " has been deactivated successfully"));
    }

    // ── GET /api/products/search ──────────────────────────────────────────────

    @Operation(
        summary = "Search products",
        description = "Searches by Name, SKU, Barcode, Category Name, Brand Name, Supplier Name, or Warehouse Name."
    )
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<Page<ProductResponse>> searchProducts(
            @Parameter(description = "Search keyword", example = "logitech") @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sort,
            @RequestParam(defaultValue = "ASC") String direction) {
        Pageable pageable = buildPageable(page, size, sort, direction);
        return ResponseEntity.ok(productService.searchProducts(keyword, pageable));
    }

    // ── GET /api/products/filter ──────────────────────────────────────────────

    @Operation(
        summary = "Filter products",
        description = "Filter by Category, Brand, Supplier, Warehouse, and Active status. All params are optional."
    )
    @GetMapping("/filter")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<Page<ProductResponse>> filterProducts(
            @Parameter(description = "Category ID")  @RequestParam(required = false) Long categoryId,
            @Parameter(description = "Brand ID")     @RequestParam(required = false) Long brandId,
            @Parameter(description = "Supplier ID")  @RequestParam(required = false) Long supplierId,
            @Parameter(description = "Warehouse ID") @RequestParam(required = false) Long warehouseId,
            @Parameter(description = "Active status")@RequestParam(required = false) Boolean active,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sort,
            @RequestParam(defaultValue = "ASC") String direction) {
        Pageable pageable = buildPageable(page, size, sort, direction);
        return ResponseEntity.ok(
                productService.filterProducts(categoryId, brandId, supplierId, warehouseId, active, pageable));
    }

    // ── GET /api/products/low-stock ───────────────────────────────────────────

    @Operation(
        summary = "Get low-stock products",
        description = "Returns active products where quantity ≤ minimumStock threshold."
    )
    @GetMapping("/low-stock")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<Page<ProductResponse>> getLowStockProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "quantity") String sort,
            @RequestParam(defaultValue = "ASC") String direction) {
        Pageable pageable = buildPageable(page, size, sort, direction);
        return ResponseEntity.ok(productService.getLowStockProducts(pageable));
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    private Pageable buildPageable(int page, int size, String sort, String direction) {
        Sort sortObj = direction.equalsIgnoreCase("DESC")
                ? Sort.by(sort).descending()
                : Sort.by(sort).ascending();
        return PageRequest.of(page, size, sortObj);
    }
}
