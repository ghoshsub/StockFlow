package com.stockflow.backend.config;

import com.stockflow.backend.security.JwtAuthenticationEntryPoint;
import com.stockflow.backend.security.JwtAuthenticationFilter;
import com.stockflow.backend.security.StockFlowUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * SecurityConfig — The master Spring Security configuration.
 *
 * Key decisions:
 *  - CSRF disabled     → stateless JWT API, no browser sessions.
 *  - CORS enabled      → handled by WebConfig (already exists).
 *  - Session STATELESS → no HttpSession is created; state lives in the JWT.
 *  - JwtAuthenticationFilter is inserted BEFORE Spring's built-in
 *    UsernamePasswordAuthenticationFilter so JWT validation happens first.
 *
 * @EnableMethodSecurity enables:
 *  - @PreAuthorize("hasRole('ADMIN')")  on controller methods
 *  - @Secured("ROLE_ADMIN")
 *  - @RolesAllowed("ADMIN")
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final StockFlowUserDetailsService userDetailsService;
    private final JwtAuthenticationEntryPoint authenticationEntryPoint;

    /** Endpoints that do NOT require a JWT token. */
    private static final String[] PUBLIC_URLS = {
            "/auth/register",
            "/auth/login",
            "/health",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/v3/api-docs/**",
            "/api-docs/**"
    };

    /**
     * SecurityFilterChain — Defines the HTTP security rules and filter order.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 1. Disable CSRF (not needed for stateless JWT APIs)
            .csrf(AbstractHttpConfigurer::disable)

            // 2. CORS — config lives in WebConfig (CorsConfigurationSource bean)
            .cors(Customizer.withDefaults())

            // 3. Exception handling — use our JSON entry point for 401 errors
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(authenticationEntryPoint)
            )

            // 4. Session management — STATELESS (no HttpSession)
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // 5. URL authorization rules
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(PUBLIC_URLS).permitAll()
                .anyRequest().authenticated()
            )

            // 6. Wire our DaoAuthenticationProvider
            .authenticationProvider(authenticationProvider())

            // 7. Insert JWT filter before Spring's username/password filter
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * PasswordEncoder — BCrypt with strength 12 (default is 10).
     * Higher rounds = slower hashing = harder to brute-force.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    /**
     * AuthenticationProvider — DaoAuthenticationProvider wires together:
     *  - UserDetailsService (loads user from DB)
     *  - PasswordEncoder   (verifies BCrypt hashes)
     *
     * Spring Security calls this during login to verify credentials.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    /**
     * AuthenticationManager — The entry point for programmatic authentication.
     * Injected into AuthService to trigger authentication during login.
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}
