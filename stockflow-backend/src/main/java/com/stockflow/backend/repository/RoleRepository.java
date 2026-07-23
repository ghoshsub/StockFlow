package com.stockflow.backend.repository;

import com.stockflow.backend.entity.Role;
import com.stockflow.backend.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * RoleRepository — Data access layer for the Role entity.
 *
 * Provides lookup by the UserRole enum (ADMIN, STAFF).
 * Used by AuthService to assign roles during registration,
 * and by DataInitializer to seed default roles on startup.
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * Find a Role by its enum name.
     *
     * @param name the UserRole enum value (e.g. UserRole.ADMIN)
     * @return Optional<Role> — empty if the role hasn't been seeded yet
     */
    Optional<Role> findByName(UserRole name);
}
