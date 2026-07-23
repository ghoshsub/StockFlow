package com.stockflow.backend.dto.category;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * CategoryRequest — Inbound DTO for creating and updating a Category.
 *
 * Used for both:
 *  - POST /api/categories        (create)
 *  - PUT  /api/categories/{id}   (update)
 *
 * Validation is enforced by @Valid in the controller before
 * the request reaches CategoryServiceImpl.
 */
@Data
@Schema(description = "Request payload for creating or updating a category")
public class CategoryRequest {

    /**
     * Category name — required, 2-100 characters.
     * Pattern: allows letters, digits, spaces, hyphens, ampersands, and apostrophes.
     */
    @Schema(description = "Unique category name (2–100 characters)", example = "Electronics")
    @NotBlank(message = "Category name is required")
    @Size(min = 2, max = 100, message = "Category name must be between 2 and 100 characters")
    @Pattern(
        regexp = "^[a-zA-Z0-9 &'\\-]+$",
        message = "Category name can only contain letters, digits, spaces, hyphens, ampersands, and apostrophes"
    )
    private String name;

    /** Description is fully optional — no @NotBlank. */
    @Schema(description = "Optional description of the category", example = "Electronic devices and accessories")
    @Size(max = 255, message = "Description cannot exceed 255 characters")
    private String description;

    /**
     * Active flag — used in updates to reactivate a soft-deleted category.
     * For create requests this is ignored; service always defaults to true.
     */
    @Schema(description = "Active status (true = visible, false = soft-deleted)", example = "true", defaultValue = "true")
    private Boolean active;
}
