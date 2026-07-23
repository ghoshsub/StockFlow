package com.stockflow.backend.dto.warehouse;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * WarehouseResponse — Outbound DTO for warehouse API responses.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Warehouse data returned in API responses")
public class WarehouseResponse {

    @Schema(description = "Auto-generated warehouse ID", example = "1")
    private Long id;

    @Schema(description = "Warehouse name", example = "Central Logistics Hub")
    private String name;

    @Schema(description = "Warehouse code", example = "WH-MUM-01")
    private String code;

    @Schema(description = "Street address", example = "Plot 45, MIDC Industrial Area")
    private String address;

    @Schema(description = "City", example = "Mumbai")
    private String city;

    @Schema(description = "State", example = "Maharashtra")
    private String state;

    @Schema(description = "Country", example = "India")
    private String country;

    @Schema(description = "Postal code", example = "400093")
    private String postalCode;

    @Schema(description = "Storage capacity", example = "50000")
    private Integer capacity;

    @Schema(description = "Manager name", example = "Anil Kapoor")
    private String managerName;

    @Schema(description = "Manager email", example = "anil.kapoor@stockflow.com")
    private String managerEmail;

    @Schema(description = "Manager phone", example = "+91-9876500011")
    private String managerPhone;

    @Schema(description = "Active status", example = "true")
    private Boolean active;

    @Schema(description = "Creation timestamp", example = "2026-07-22T10:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @Schema(description = "Last update timestamp", example = "2026-07-22T12:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
}
