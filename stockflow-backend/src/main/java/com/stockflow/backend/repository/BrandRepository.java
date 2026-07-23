package com.stockflow.backend.repository;

import com.stockflow.backend.entity.Brand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * BrandRepository — Data access layer for the Brand entity.
 *
 * Extends JpaRepository to inherit all standard CRUD + pagination operations.
 */
@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {

    // ── Existence Checks ─────────────────────────────────────────────────────

    /**
     * Check if a brand with the given name already exists (case-insensitive).
     */
    boolean existsByNameIgnoreCase(String name);

    /**
     * Check if another brand has the same name, excluding the current one by ID.
     */
    @Query("SELECT COUNT(b) > 0 FROM Brand b WHERE LOWER(b.name) = LOWER(:name) AND b.id <> :id")
    boolean existsByNameIgnoreCaseAndIdNot(@Param("name") String name, @Param("id") Long id);

    // ── Active-Only Queries ───────────────────────────────────────────────────

    /**
     * Fetch all active brands (active = true).
     */
    List<Brand> findAllByActiveTrue();

    /**
     * Paginated list of all active brands.
     */
    Page<Brand> findAllByActiveTrue(Pageable pageable);

    /**
     * Keyword search across name and description (case-insensitive LIKE).
     * Only returns active brands.
     */
    @Query("""
            SELECT b FROM Brand b
            WHERE b.active = true
            AND (LOWER(b.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
                 OR LOWER(b.description) LIKE LOWER(CONCAT('%', :keyword, '%')))
            """)
    Page<Brand> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    /**
     * Find an active brand by exact name.
     */
    Optional<Brand> findByNameIgnoreCaseAndActiveTrue(String name);
}
