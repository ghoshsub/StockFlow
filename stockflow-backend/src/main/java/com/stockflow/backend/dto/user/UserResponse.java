package com.stockflow.backend.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response object representing user profile details")
public class UserResponse {

    @Schema(description = "User ID", example = "1")
    private Long id;

    @Schema(description = "Unique username", example = "john_doe")
    private String username;

    @Schema(description = "User email address", example = "john@stockflow.com")
    private String email;

    @Schema(description = "User first name", example = "John")
    private String firstName;

    @Schema(description = "User last name", example = "Doe")
    private String lastName;

    @Schema(description = "Assigned user role (ADMIN, STAFF)", example = "STAFF")
    private String role;

    @Schema(description = "Account active status", example = "true")
    private Boolean active;

    @Schema(description = "Account creation timestamp")
    private LocalDateTime createdAt;
}
