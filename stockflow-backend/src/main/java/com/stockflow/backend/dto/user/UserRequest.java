package com.stockflow.backend.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request object for creating or updating a user")
public class UserRequest {

    @Schema(description = "Unique username", example = "john_doe")
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    @Schema(description = "User email address", example = "john@stockflow.com")
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be a valid email address")
    private String email;

    @Schema(description = "Password (required for create, optional for update)", example = "Password@123")
    private String password;

    @Schema(description = "User first name", example = "John")
    @NotBlank(message = "First name is required")
    private String firstName;

    @Schema(description = "User last name", example = "Doe")
    @NotBlank(message = "Last name is required")
    private String lastName;

    @Builder.Default
    @Schema(description = "Role to assign: ADMIN or STAFF", example = "STAFF")
    private String role = "STAFF";

    public String getRole() {
        if (this.role == null || this.role.trim().isEmpty()) {
            return "STAFF";
        }
        return this.role;
    }

    public void setRole(String role) {
        if (role == null || role.trim().isEmpty()) {
            this.role = "STAFF";
        } else {
            this.role = role;
        }
    }
}
