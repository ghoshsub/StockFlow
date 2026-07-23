package com.stockflow.backend.dto.supplier;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * SupplierResponse — Outbound DTO returned for all supplier API responses.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Supplier details returned in API responses")
public class SupplierResponse {

    @Schema(description = "Auto-generated supplier ID", example = "1")
    private Long id;

    @Schema(description = "Supplier name", example = "TechDistributors Pvt Ltd")
    private String name;

    @Schema(description = "Contact person", example = "Rajesh Sharma")
    private String contactPerson;

    @Schema(description = "Email address", example = "contact@techdistributors.com")
    private String email;

    @Schema(description = "Phone number", example = "+91-9876543210")
    private String phone;

    @Schema(description = "GST Identification Number", example = "27AABCU9603R1ZN")
    private String gstNumber;

    @Schema(description = "Street address", example = "123 Industrial Estate, Andheri East")
    private String address;

    @Schema(description = "City", example = "Mumbai")
    private String city;

    @Schema(description = "State", example = "Maharashtra")
    private String state;

    @Schema(description = "Country", example = "India")
    private String country;

    @Schema(description = "Postal code", example = "400069")
    private String postalCode;

    @Schema(description = "Website URL", example = "https://www.techdistributors.com")
    private String website;

    @Schema(description = "Active status", example = "true")
    private Boolean active;

    @Schema(description = "Record creation timestamp", example = "2026-07-22T10:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @Schema(description = "Record update timestamp", example = "2026-07-22T12:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
}
