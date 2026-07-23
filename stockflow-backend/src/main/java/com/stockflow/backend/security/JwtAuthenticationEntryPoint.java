package com.stockflow.backend.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * JwtAuthenticationEntryPoint — Handles unauthorized access attempts.
 *
 * Implements AuthenticationEntryPoint, which Spring Security calls
 * when an unauthenticated request tries to reach a protected resource.
 *
 * Without this: Spring returns a default HTML 401 page (ugly, unusable for APIs).
 * With this: We return a clean, structured JSON 401 response.
 *
 * Triggered when:
 *  - No Authorization header is present on a protected route.
 *  - The JWT token is invalid, expired, or tampered with.
 *  - The filter chain did NOT set an authentication in the SecurityContext.
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);       // 401
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", 401);
        body.put("error", "Unauthorized");
        body.put("message", "Full authentication is required to access this resource. " +
                "Please provide a valid Bearer token.");
        body.put("path", request.getServletPath());

        objectMapper.writeValue(response.getOutputStream(), body);
    }
}
