package com.stockflow.backend.dto.category;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * CategoryResponse — Outbound DTO returned for all category API responses.
 *
 * Key decisions:
 *  - Uses @Builder so CategoryMapper can construct it fluently.
 *  - Dates are formatted as ISO-8601 strings (Jackson serialisation).
 *  - Entity is NEVER directly returned to the controller layer.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Category data returned in API responses")
public class CategoryResponse {

    @Schema(description = "Auto-generated category ID", example = "1")
    private Long id;

    @Schema(description = "Category name", example = "Electronics")
    private String name;

    @Schema(description = "Category description", example = "Electronic devices and accessories")
    private String description;

    @Schema(description = "Whether the category is active", example = "true")
    private Boolean active;

    @Schema(description = "Timestamp when the category was created", example = "2026-07-22T10:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @Schema(description = "Timestamp of the last update", example = "2026-07-22T12:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
}
