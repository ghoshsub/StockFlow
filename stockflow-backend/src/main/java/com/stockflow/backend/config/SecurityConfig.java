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

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final StockFlowUserDetailsService userDetailsService;
    private final JwtAuthenticationEntryPoint authenticationEntryPoint;


    /**
     * Public endpoints that do not require JWT token
     */
    private static final String[] PUBLIC_URLS = {

            // Authentication APIs
            "/auth/**",

            // Health check
            "/health",

            // Swagger
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/v3/api-docs/**",
            "/api-docs/**"
    };


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http

            // Disable CSRF for JWT based authentication
            .csrf(AbstractHttpConfigurer::disable)


            // Enable CORS
            .cors(Customizer.withDefaults())


            // Handle unauthorized requests
            .exceptionHandling(ex -> ex
                    .authenticationEntryPoint(authenticationEntryPoint)
            )


            // Stateless session
            .sessionManagement(session -> session
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )


            // Authorization rules
            .authorizeHttpRequests(auth -> auth

                    // Public APIs
                    .requestMatchers(PUBLIC_URLS).permitAll()

                    // Everything else needs JWT
                    .anyRequest().authenticated()
            )


            // Authentication provider
            .authenticationProvider(authenticationProvider())


            // JWT filter
            .addFilterBefore(
                    jwtAuthenticationFilter,
                    UsernamePasswordAuthenticationFilter.class
            );


        return http.build();
    }



    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder(12);

    }



    @Bean
    public AuthenticationProvider authenticationProvider() {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider();

        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());

        return provider;
    }



    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authConfig
    ) throws Exception {

        return authConfig.getAuthenticationManager();

    }

}