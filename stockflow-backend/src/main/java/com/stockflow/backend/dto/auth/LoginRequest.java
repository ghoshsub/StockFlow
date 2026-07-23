package com.stockflow.backend.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * LoginRequest — Inbound DTO for the POST /api/auth/login endpoint.
 *
 * Accepts either username or email as the identifier,
 * paired with a plaintext password that will be matched
 * against the BCrypt-encoded value stored in the database.
 */
@Data
@Schema(description = "Request body for user login")
public class LoginRequest {

    @Schema(description = "Username or email address", example = "admin")
    @NotBlank(message = "Username or email is required")
    private String usernameOrEmail;

    @Schema(description = "Account password", example = "Admin@1234")
    @NotBlank(message = "Password is required")
    private String password;
}
