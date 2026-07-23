package com.stockflow.backend.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * LoginResponse — Outbound DTO returned after a successful login or registration.
 *
 * Contains the JWT token plus the authenticated user's profile summary.
 * Never exposes the raw User entity or password to the client.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response body returned after successful authentication")
public class LoginResponse {

    @Schema(description = "Bearer JWT access token", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String token;

    @Schema(description = "Token type, always Bearer", example = "Bearer")
    private String tokenType;

    @Schema(description = "Internal user ID", example = "1")
    private Long id;

    @Schema(description = "Unique username", example = "admin")
    private String username;

    @Schema(description = "User email address", example = "admin@stockflow.com")
    private String email;

    @Schema(description = "User's first name", example = "Admin")
    private String firstName;

    @Schema(description = "User's last name", example = "User")
    private String lastName;

    @Schema(description = "Assigned role (ADMIN / STAFF)", example = "ADMIN")
    private String role;
}
