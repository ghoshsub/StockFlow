package com.stockflow.backend.controller;

import com.stockflow.backend.dto.auth.LoginRequest;
import com.stockflow.backend.dto.auth.LoginResponse;
import com.stockflow.backend.dto.auth.RegisterRequest;
import com.stockflow.backend.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * AuthController — REST controller exposing authentication endpoints.
 *
 * Base path (from application.yml context-path /api): /api/auth
 *
 * Endpoints:
 *  POST /api/auth/register → Register a new user
 *  POST /api/auth/login    → Login and receive a JWT
 *  GET  /api/auth/me       → Get the current user's profile (requires JWT)
 *
 * The controller is thin — it only:
 *  1. Validates the request body (@Valid).
 *  2. Delegates to AuthService.
 *  3. Returns a ResponseEntity with the appropriate HTTP status.
 *
 * No business logic lives here.
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Register, login, and retrieve current user profile")
public class AuthController {

    private final AuthService authService;

    // ── POST /api/auth/register ───────────────────────────────────────────────

    @Operation(
        summary = "Register a new user",
        description = "Creates a new user account. Returns a JWT token and user profile on success."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "User registered successfully",
            content = @Content(schema = @Schema(implementation = LoginResponse.class))),
        @ApiResponse(responseCode = "400", description = "Validation failed"),
        @ApiResponse(responseCode = "409", description = "Username or email already exists")
    })
    @PostMapping("/register")
    public ResponseEntity<LoginResponse> register(@Valid @RequestBody RegisterRequest request) {
        LoginResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ── POST /api/auth/login ──────────────────────────────────────────────────

    @Operation(
        summary = "Login user",
        description = "Authenticates user credentials and returns a signed JWT Bearer token."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Login successful",
            content = @Content(schema = @Schema(implementation = LoginResponse.class))),
        @ApiResponse(responseCode = "400", description = "Validation failed"),
        @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    // ── GET /api/auth/me ──────────────────────────────────────────────────────

    @Operation(
        summary = "Get current user profile",
        description = "Returns the profile of the currently authenticated user. Requires a valid Bearer token.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Profile retrieved successfully",
            content = @Content(schema = @Schema(implementation = LoginResponse.class))),
        @ApiResponse(responseCode = "401", description = "No or invalid token provided"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/me")
    public ResponseEntity<LoginResponse> getCurrentUser() {
        // Spring Security has already validated the JWT and set the Authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();  // == JWT "sub" claim
        LoginResponse response = authService.getMe(username);
        return ResponseEntity.ok(response);
    }

    // ── GET /api/auth/ping (health shortcut, no auth required) ───────────────

    @Operation(summary = "Auth health ping", description = "Quick ping to confirm the auth routes are up.")
    @GetMapping("/ping")
    public ResponseEntity<Map<String, String>> ping() {
        return ResponseEntity.ok(Map.of("status", "Auth service is running"));
    }
}
