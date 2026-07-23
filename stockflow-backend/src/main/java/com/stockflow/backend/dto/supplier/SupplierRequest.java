package com.stockflow.backend.dto.supplier;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * SupplierRequest — Inbound DTO for creating and updating a Supplier.
 */
@Data
@Schema(description = "Request payload for creating or updating a supplier")
public class SupplierRequest {

    @Schema(description = "Unique supplier business name", example = "TechDistributors Pvt Ltd")
    @NotBlank(message = "Supplier name is required")
    @Size(min = 2, max = 100, message = "Supplier name must be between 2 and 100 characters")
    private String name;

    @Schema(description = "Primary contact person's name", example = "Rajesh Sharma")
    @Size(max = 100, message = "Contact person name cannot exceed 100 characters")
    private String contactPerson;

    @Schema(description = "Unique official email address", example = "contact@techdistributors.com")
    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    @Size(max = 100, message = "Email cannot exceed 100 characters")
    private String email;

    @Schema(description = "Supplier phone number", example = "+91-9876543210")
    @Size(max = 20, message = "Phone number cannot exceed 20 characters")
    @Pattern(
        regexp = "^[0-9+\\-\\s()]*$",
        message = "Phone number contains invalid characters"
    )
    private String phone;

    @Schema(description = "Goods and Services Tax Number (GSTIN)", example = "27AABCU9603R1ZN")
    @Size(max = 20, message = "GST number cannot exceed 20 characters")
    private String gstNumber;

    @Schema(description = "Street address", example = "123 Industrial Estate, Andheri East")
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

    @Schema(description = "Postal code", example = "400069")
    @Size(max = 20, message = "Postal code cannot exceed 20 characters")
    private String postalCode;

    @Schema(description = "Official website URL", example = "https://www.techdistributors.com")
    @Size(max = 100, message = "Website URL cannot exceed 100 characters")
    private String website;

    @Schema(description = "Active status (true = active, false = soft-deleted)", example = "true", defaultValue = "true")
    private Boolean active;
}
