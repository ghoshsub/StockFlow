package com.stockflow.backend.dto.brand;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * BrandResponse — Outbound DTO returned for all brand API responses.
 *
 * Key decisions:
 *  - Uses @Builder so BrandMapper can construct it fluently.
 *  - Dates are formatted as ISO-8601 strings.
 *  - Entity is NEVER directly returned to the controller layer.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Brand data returned in API responses")
public class BrandResponse {

    @Schema(description = "Auto-generated brand ID", example = "1")
    private Long id;

    @Schema(description = "Brand name", example = "Logitech")
    private String name;

    @Schema(description = "Brand description", example = "Computer peripherals and software manufacturer")
    private String description;

    @Schema(description = "Whether the brand is active", example = "true")
    private Boolean active;

    @Schema(description = "Timestamp when the brand was created", example = "2026-07-22T10:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @Schema(description = "Timestamp of the last update", example = "2026-07-22T12:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
}
