package com.stockflow.backend.dto.brand;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * BrandRequest — Inbound DTO for creating and updating a Brand.
 *
 * Used for both:
 *  - POST /api/brands        (create)
 *  - PUT  /api/brands/{id}   (update)
 *
 * Validation is enforced by @Valid in the controller before
 * the request reaches BrandServiceImpl.
 */
@Data
@Schema(description = "Request payload for creating or updating a brand")
public class BrandRequest {

    /**
     * Brand name — required, 2-100 characters.
     * Pattern: allows letters, digits, spaces, hyphens, ampersands, and apostrophes.
     */
    @Schema(description = "Unique brand name (2–100 characters)", example = "Logitech")
    @NotBlank(message = "Brand name is required")
    @Size(min = 2, max = 100, message = "Brand name must be between 2 and 100 characters")
    @Pattern(
        regexp = "^[a-zA-Z0-9 &'\\-]+$",
        message = "Brand name can only contain letters, digits, spaces, hyphens, ampersands, and apostrophes"
    )
    private String name;

    /** Description is optional — max 255 characters. */
    @Schema(description = "Optional description of the brand", example = "Computer peripherals and software manufacturer")
    @Size(max = 255, message = "Description cannot exceed 255 characters")
    private String description;

    /**
     * Active flag — used in updates to reactivate a soft-deleted brand.
     * For create requests this is ignored; service always defaults to true.
     */
    @Schema(description = "Active status (true = visible, false = soft-deleted)", example = "true", defaultValue = "true")
    private Boolean active;
}
