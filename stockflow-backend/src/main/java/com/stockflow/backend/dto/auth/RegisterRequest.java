package com.stockflow.backend.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * RegisterRequest — Inbound DTO for the POST /api/auth/register endpoint.
 *
 * Validated with Jakarta Bean Validation before reaching AuthService.
 * Using @Data (Lombok) generates: getters, setters, equals, hashCode, toString.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request body for user registration")
public class RegisterRequest {

    @Schema(description = "Unique username (3-50 characters)", example = "john_doe")
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    @Schema(description = "Valid email address", example = "john@stockflow.com")
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be a valid email address")
    private String email;

    @Schema(description = "Password (min 6 characters)", example = "Password@123")
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @Schema(description = "User's first name", example = "John")
    private String firstName;

    @Schema(description = "User's last name", example = "Doe")
    private String lastName;

    @Builder.Default
    @Schema(description = "Role to assign: ADMIN or STAFF. Defaults to STAFF if omitted.", example = "STAFF")
    private String role = "STAFF";

    /**
     * Custom getter guaranteeing non-null role return value.
     */
    public String getRole() {
        if (this.role == null || this.role.trim().isEmpty()) {
            return "STAFF";
        }
        return this.role;
    }

    /**
     * Custom setter handling null/blank values gracefully.
     */
    public void setRole(String role) {
        if (role == null || role.trim().isEmpty()) {
            this.role = "STAFF";
        } else {
            this.role = role;
        }
    }
}
