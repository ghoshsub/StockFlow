package com.stockflow.backend.repository;

import com.stockflow.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * UserRepository — Data access layer for the User entity.
 *
 * Extends JpaRepository to inherit all standard CRUD operations.
 * Custom query methods leverage Spring Data JPA method-name parsing.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find a user by their unique username.
     * Used by UserDetailsService to load a user during authentication.
     */
    Optional<User> findByUsername(String username);

    /**
     * Find a user by their email address.
     * Used during registration to check for duplicate emails.
     */
    Optional<User> findByEmail(String email);

    /**
     * Check if any user exists in the database.
     * Used by DataInitializer to decide whether to seed a default ADMIN.
     */
    @Query("SELECT COUNT(u) > 0 FROM User u")
    boolean existsAnyUser();

    /**
     * Check if a username is already taken.
     */
    boolean existsByUsername(String username);

    /**
     * Check if an email is already registered.
     */
    boolean existsByEmail(String email);

    /**
     * Search users by keyword (username, email, firstName, or lastName).
     */
    @Query("SELECT u FROM User u WHERE " +
           "LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    org.springframework.data.domain.Page<User> searchUsers(String keyword, org.springframework.data.domain.Pageable pageable);
}

