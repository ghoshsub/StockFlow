package com.stockflow.backend.repository;

import com.stockflow.backend.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * CategoryRepository — Data access layer for the Category entity.
 *
 * Extends JpaRepository to inherit all standard CRUD + pagination operations.
 *
 * Query strategy:
 *  - Simple lookups: Spring Data JPA method-name parsing (findBy...).
 *  - Search / filter: JPQL @Query for fine-grained control.
 *  - All "normal" queries filter on active = true (soft-delete aware).
 *  - Admin-only queries (e.g. findById) do NOT filter on active,
 *    allowing admins to view and restore soft-deleted categories.
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    // ── Existence Checks ─────────────────────────────────────────────────────

    /**
     * Check if a category with the given name already exists (case-insensitive).
     * Used during create to enforce uniqueness.
     */
    boolean existsByNameIgnoreCase(String name);

    /**
     * Check if another category has the same name, excluding the current one by ID.
     * Used during update to prevent renaming to an already-taken name.
     */
    @Query("SELECT COUNT(c) > 0 FROM Category c WHERE LOWER(c.name) = LOWER(:name) AND c.id <> :id")
    boolean existsByNameIgnoreCaseAndIdNot(@Param("name") String name, @Param("id") Long id);

    // ── Active-Only Queries ───────────────────────────────────────────────────

    /**
     * Fetch all active categories (active = true) — for STAFF views.
     */
    List<Category> findAllByActiveTrue();

    /**
     * Paginated list of all active categories, with sorting support via Pageable.
     *
     * @param pageable includes page number, page size, and sort direction
     */
    Page<Category> findAllByActiveTrue(Pageable pageable);

    /**
     * Keyword search across name and description (case-insensitive LIKE).
     * Only returns active categories.
     *
     * Example JPQL:
     *   SELECT c FROM Category c
     *   WHERE c.active = true
     *   AND (LOWER(c.name) LIKE LOWER(:kw) OR LOWER(c.description) LIKE LOWER(:kw))
     *
     * @param keyword  the search term (partial match, e.g. "elec" matches "Electronics")
     * @param pageable pagination + sorting
     */
    @Query("""
            SELECT c FROM Category c
            WHERE c.active = true
            AND (LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
                 OR LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%')))
            """)
    Page<Category> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    /**
     * Find an active category by exact name (used for duplicate-name validation on update).
     */
    Optional<Category> findByNameIgnoreCaseAndActiveTrue(String name);

    /**
     * Find a category by ID — does NOT filter on active.
     * Used when admin needs to view / restore a soft-deleted category.
     * JpaRepository.findById() already covers this — listed here for documentation.
     */
    // findById() inherited from JpaRepository
}
