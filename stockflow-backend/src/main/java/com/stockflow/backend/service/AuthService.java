package com.stockflow.backend.service;

import com.stockflow.backend.dto.auth.LoginRequest;
import com.stockflow.backend.dto.auth.LoginResponse;
import com.stockflow.backend.dto.auth.RegisterRequest;
import com.stockflow.backend.entity.Role;
import com.stockflow.backend.entity.User;
import com.stockflow.backend.entity.UserRole;
import com.stockflow.backend.exception.DuplicateResourceException;
import com.stockflow.backend.exception.ResourceNotFoundException;
import com.stockflow.backend.repository.RoleRepository;
import com.stockflow.backend.repository.UserRepository;
import com.stockflow.backend.security.JwtService;
import com.stockflow.backend.security.StockFlowUserDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * AuthService — The business logic layer for authentication operations.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final StockFlowUserDetailsService userDetailsService;

    // ── Register ─────────────────────────────────────────────────────────────

    /**
     * Register a new user account.
     * Defaults role to STAFF if null or empty.
     */
    @Transactional
    public LoginResponse register(RegisterRequest request) {
        // Uniqueness validation
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException(
                    "Username '" + request.getUsername() + "' is already taken");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException(
                    "Email '" + request.getEmail() + "' is already registered");
        }

        // Default role handling: If role is null or blank, assign default role STAFF
        String rawRole = request.getRole();
        if (rawRole == null || rawRole.trim().isEmpty()) {
            rawRole = "STAFF";
        }

        String roleStr = rawRole.trim().toUpperCase();
        if (roleStr.startsWith("ROLE_")) {
            roleStr = roleStr.substring(5);
        }

        UserRole userRole;
        try {
            userRole = UserRole.valueOf(roleStr);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Invalid role: '" + rawRole + "'. Must be ADMIN or STAFF");
        }

        Role role = roleRepository.findByName(userRole)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Role '" + userRole + "' not found. Ensure DataInitializer has run."));

        String firstName = request.getFirstName() != null ? request.getFirstName() : request.getUsername();
        String lastName = request.getLastName() != null ? request.getLastName() : "";

        // Build and persist the User entity
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(firstName)
                .lastName(lastName)
                .role(role)
                .build();

        User savedUser = userRepository.save(user);
        log.info("New user registered: {} (role: {})", savedUser.getUsername(), userRole);

        // Generate JWT with role embedded in claims
        String token = buildTokenForUser(savedUser);
        return buildLoginResponse(token, savedUser);
    }

    // ── Login ─────────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsernameOrEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByUsername(request.getUsernameOrEmail())
                .or(() -> userRepository.findByEmail(request.getUsernameOrEmail()))
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        log.info("User logged in: {}", user.getUsername());

        String token = buildTokenForUser(user);
        return buildLoginResponse(token, user);
    }

    // ── Get Current User ──────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public LoginResponse getMe(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with username: " + username));

        String token = buildTokenForUser(user);
        return buildLoginResponse(token, user);
    }

    // ── Private Helpers ───────────────────────────────────────────────────────

    private String buildTokenForUser(User user) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("role", user.getRole().getName().name());
        extraClaims.put("userId", user.getId());
        extraClaims.put("email", user.getEmail());

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        return jwtService.generateToken(extraClaims, userDetails);
    }

    private LoginResponse buildLoginResponse(String token, User user) {
        return LoginResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole().getName().name())
                .build();
    }
}
