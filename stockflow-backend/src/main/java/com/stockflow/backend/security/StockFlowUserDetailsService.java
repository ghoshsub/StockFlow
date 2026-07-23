package com.stockflow.backend.security;

import com.stockflow.backend.entity.User;
import com.stockflow.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * StockFlowUserDetailsService — Spring Security's UserDetailsService implementation.
 *
 * Spring Security calls loadUserByUsername() during the authentication process
 * to retrieve user information from the database.
 *
 * Flow:
 *  1. AuthenticationManager receives credentials (from LoginRequest).
 *  2. It delegates to DaoAuthenticationProvider.
 *  3. DaoAuthenticationProvider calls loadUserByUsername(username).
 *  4. This class queries the DB and returns a UserDetails object.
 *  5. DaoAuthenticationProvider compares the incoming password with the stored hash.
 *
 * We also accept email as the username to support login by email.
 */
@Service
@RequiredArgsConstructor
public class StockFlowUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Load user by username OR email (supports both login strategies).
     *
     * @param usernameOrEmail username or email string provided by the client
     * @return UserDetails containing authorities and password hash
     * @throws UsernameNotFoundException if no matching user is found
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        // Try username first, then fall back to email lookup
        User user = userRepository.findByUsername(usernameOrEmail)
                .orElseGet(() -> userRepository.findByEmail(usernameOrEmail)
                        .orElseThrow(() -> new UsernameNotFoundException(
                                "User not found with username or email: " + usernameOrEmail)));

        // Convert our Role entity into a Spring Security GrantedAuthority.
        // Prefix "ROLE_" is required by Spring Security's hasRole() checks.
        String roleName = "ROLE_" + user.getRole().getName().name();

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())          // already BCrypt-encoded
                .authorities(List.of(new SimpleGrantedAuthority(roleName)))
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }
}
