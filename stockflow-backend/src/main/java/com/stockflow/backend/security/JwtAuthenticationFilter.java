package com.stockflow.backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JwtAuthenticationFilter — Intercepts every HTTP request ONCE and validates the JWT.
 *
 * Extends OncePerRequestFilter → guaranteed single execution per request,
 * even in servlet containers that dispatch the filter multiple times.
 *
 * Processing steps (per request):
 *  1. Extract the "Authorization" header.
 *  2. If absent or doesn't start with "Bearer ", skip (chain.doFilter).
 *  3. Extract the token string (strip "Bearer " prefix).
 *  4. Extract username from the token via JwtService.
 *  5. If username is found AND SecurityContext has no authentication yet:
 *       a. Load UserDetails from DB via StockFlowUserDetailsService.
 *       b. Validate token (signature + expiry + username match).
 *       c. Build UsernamePasswordAuthenticationToken with authorities.
 *       d. Set it in the SecurityContext → request is now authenticated.
 *  6. Continue down the filter chain.
 *
 * Why check SecurityContextHolder.getContext().getAuthentication() == null?
 *  → Prevents overwriting an already-authenticated context (e.g. test environments).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final StockFlowUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        // ── Step 1: No or invalid Authorization header → skip ────────────────
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // ── Step 2: Extract JWT (everything after "Bearer ") ─────────────────
        final String jwt = authHeader.substring(7);
        String username = null;

        try {
            username = jwtService.extractUsername(jwt);
        } catch (Exception e) {
            log.warn("JWT extraction failed for request [{}]: {}", request.getRequestURI(), e.getMessage());
            filterChain.doFilter(request, response);
            return;
        }

        // ── Step 3: Authenticate if username found and not yet authenticated ──
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (jwtService.isTokenValid(jwt, userDetails)) {
                // Build authentication token with granted authorities
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,                           // credentials null (stateless)
                                userDetails.getAuthorities()
                        );

                // Attach request details (IP, session ID) for audit purposes
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Place authenticated token in the SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authToken);
                log.debug("JWT authentication successful for user: {}", username);
            } else {
                log.warn("JWT token validation failed for user: {}", username);
            }
        }

        filterChain.doFilter(request, response);
    }
}
