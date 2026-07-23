package com.stockflow.backend.dto.warehouse;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * WarehouseRequest — Inbound DTO for creating and updating a Warehouse.
 */
@Data
@Schema(description = "Request payload for creating or updating a warehouse")
public class WarehouseRequest {

    @Schema(description = "Unique warehouse name", example = "Central Logistics Hub")
    @NotBlank(message = "Warehouse name is required")
    @Size(min = 2, max = 100, message = "Warehouse name must be between 2 and 100 characters")
    private String name;

    @Schema(description = "Unique warehouse code", example = "WH-MUM-01")
    @NotBlank(message = "Warehouse code is required")
    @Size(min = 2, max = 20, message = "Warehouse code must be between 2 and 20 characters")
    @Pattern(regexp = "^[A-Z0-9\\-_]+$", message = "Warehouse code must contain uppercase letters, numbers, hyphens or underscores")
    private String code;

    @Schema(description = "Physical street address", example = "Plot 45, MIDC Industrial Area")
    @Size(max = 255, message = "Address cannot exceed 255 characters")
    private String address;

    @Schema(description = "City", example = "Mumbai")
    @Size(max = 50, message = "City cannot exceed 50 characters")
    private String city;

    @Schema(description = "State", example = "Maharashtra")
    @Size(max = 50, message = "State cannot exceed 50 characters")
    private String state;

    @Schema(description = "Country", example = "India")
    @Size(max = 50, message = "Country cannot exceed 50 characters")
    private String country;

    @Schema(description = "Postal code", example = "400093")
    @Size(max = 20, message = "Postal code cannot exceed 20 characters")
    private String postalCode;

    @Schema(description = "Storage capacity (non-negative integer)", example = "50000")
    @Min(value = 0, message = "Capacity cannot be negative")
    private Integer capacity;

    @Schema(description = "Manager's full name", example = "Anil Kapoor")
    @Size(max = 100, message = "Manager name cannot exceed 100 characters")
    private String managerName;

    @Schema(description = "Manager's email address", example = "anil.kapoor@stockflow.com")
    @Email(message = "Please provide a valid manager email address")
    @Size(max = 100, message = "Manager email cannot exceed 100 characters")
    private String managerEmail;

    @Schema(description = "Manager's phone number", example = "+91-9876500011")
    @Size(max = 20, message = "Manager phone cannot exceed 20 characters")
    private String managerPhone;

    @Schema(description = "Active status", example = "true", defaultValue = "true")
    private Boolean active;
}
