package com.stockflow.backend.controller;

import com.stockflow.backend.dto.category.CategoryRequest;
import com.stockflow.backend.dto.category.CategoryResponse;
import com.stockflow.backend.service.CategoryService;
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
 * CategoryController — REST controller for the Category module.
 *
 * Base path (context-path /api already set in application.yml): /api/categories
 *
 * Role-based access control via @PreAuthorize:
 *  - POST, PUT, DELETE → ADMIN only
 *  - GET, Search       → ADMIN, STAFF
 *
 * The controller is intentionally thin:
 *  - Validates @Valid request bodies.
 *  - Builds Pageable from raw query parameters (avoids PageableDefault coupling).
 *  - Delegates all logic to CategoryService.
 *  - Returns ResponseEntity with the appropriate HTTP status.
 */
@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@Tag(name = "Categories", description = "Manage product categories — CRUD, search, pagination, soft-delete")
@SecurityRequirement(name = "Bearer Authentication")
public class CategoryController {

    private final CategoryService categoryService;

    // ── POST /api/categories ──────────────────────────────────────────────────

    @Operation(
        summary = "Create a new category",
        description = "Creates a new product category. Requires ADMIN role. Name must be unique."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Category created successfully",
            content = @Content(schema = @Schema(implementation = CategoryResponse.class))),
        @ApiResponse(responseCode = "400", description = "Validation failed"),
        @ApiResponse(responseCode = "401", description = "Unauthorized — missing or invalid token"),
        @ApiResponse(responseCode = "403", description = "Forbidden — ADMIN role required"),
        @ApiResponse(responseCode = "409", description = "Category name already exists")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryResponse> createCategory(
            @Valid @RequestBody CategoryRequest request) {
        CategoryResponse response = categoryService.createCategory(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ── GET /api/categories/{id} ──────────────────────────────────────────────

    @Operation(
        summary = "Get category by ID",
        description = "Retrieves a single category by its ID. All authenticated users can access."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Category found",
            content = @Content(schema = @Schema(implementation = CategoryResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "Category not found")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<CategoryResponse> getCategoryById(
            @Parameter(description = "Category ID", example = "1")
            @PathVariable Long id) {
        CategoryResponse response = categoryService.getCategoryById(id);
        return ResponseEntity.ok(response);
    }

    // ── GET /api/categories (non-paginated) ───────────────────────────────────

    @Operation(
        summary = "Get all categories (list)",
        description = "Returns all active categories as a flat list. Ideal for dropdown menus. All authenticated users can access."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Categories retrieved"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<List<CategoryResponse>> getAllCategoriesList() {
        List<CategoryResponse> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    // ── GET /api/categories (paginated) ──────────────────────────────────────

    @Operation(
        summary = "Get all categories (paginated)",
        description = "Returns a paginated, sortable list of active categories. " +
                "Default: page=0, size=10, sort=name, direction=ASC."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Page of categories returned"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<Page<CategoryResponse>> getAllCategoriesPaginated(
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Number of records per page", example = "10")
            @RequestParam(defaultValue = "10") int size,

            @Parameter(description = "Sort field", example = "name")
            @RequestParam(defaultValue = "name") String sortBy,

            @Parameter(description = "Sort direction: ASC or DESC", example = "ASC")
            @RequestParam(defaultValue = "ASC") String direction) {

        Sort sort = direction.equalsIgnoreCase("DESC")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<CategoryResponse> response = categoryService.getAllCategoriesPaginated(pageable);
        return ResponseEntity.ok(response);
    }

    // ── PUT /api/categories/{id} ──────────────────────────────────────────────

    @Operation(
        summary = "Update a category",
        description = "Updates an existing category by ID. Requires ADMIN role. " +
                "Can also reactivate a soft-deleted category by setting active=true."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Category updated",
            content = @Content(schema = @Schema(implementation = CategoryResponse.class))),
        @ApiResponse(responseCode = "400", description = "Validation failed"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden — ADMIN only"),
        @ApiResponse(responseCode = "404", description = "Category not found"),
        @ApiResponse(responseCode = "409", description = "Name conflict with another category")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryResponse> updateCategory(
            @Parameter(description = "Category ID to update", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequest request) {
        CategoryResponse response = categoryService.updateCategory(id, request);
        return ResponseEntity.ok(response);
    }

    // ── DELETE /api/categories/{id} ───────────────────────────────────────────

    @Operation(
        summary = "Soft-delete a category",
        description = "Sets the category's active flag to false. " +
                "The record is NOT removed from the database. Requires ADMIN role."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Category soft-deleted"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden — ADMIN only"),
        @ApiResponse(responseCode = "404", description = "Category not found")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> deleteCategory(
            @Parameter(description = "Category ID to soft-delete", example = "1")
            @PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(Map.of(
                "message", "Category with id " + id + " has been deactivated successfully"
        ));
    }

    // ── GET /api/categories/search?keyword=... ────────────────────────────────

    @Operation(
        summary = "Search categories by keyword",
        description = "Searches active categories by partial name or description match (case-insensitive). " +
                "Supports pagination and sorting. All authenticated users can access."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Search results returned"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<Page<CategoryResponse>> searchCategories(
            @Parameter(description = "Search keyword (partial match on name or description)", example = "elec")
            @RequestParam String keyword,

            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Records per page", example = "10")
            @RequestParam(defaultValue = "10") int size,

            @Parameter(description = "Sort field", example = "name")
            @RequestParam(defaultValue = "name") String sortBy,

            @Parameter(description = "Sort direction: ASC or DESC", example = "ASC")
            @RequestParam(defaultValue = "ASC") String direction) {

        Sort sort = direction.equalsIgnoreCase("DESC")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<CategoryResponse> response = categoryService.searchCategories(keyword, pageable);
        return ResponseEntity.ok(response);
    }
}
