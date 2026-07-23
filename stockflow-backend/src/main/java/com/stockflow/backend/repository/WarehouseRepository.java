package com.stockflow.backend.repository;

import com.stockflow.backend.entity.Warehouse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * WarehouseRepository — Data access layer for Warehouse entity.
 */
@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {

    // ── Name Uniqueness ───────────────────────────────────────────────────────
    boolean existsByNameIgnoreCase(String name);

    @Query("SELECT COUNT(w) > 0 FROM Warehouse w WHERE LOWER(w.name) = LOWER(:name) AND w.id <> :id")
    boolean existsByNameIgnoreCaseAndIdNot(@Param("name") String name, @Param("id") Long id);

    // ── Code Uniqueness ───────────────────────────────────────────────────────
    boolean existsByCodeIgnoreCase(String code);

    @Query("SELECT COUNT(w) > 0 FROM Warehouse w WHERE LOWER(w.code) = LOWER(:code) AND w.id <> :id")
    boolean existsByCodeIgnoreCaseAndIdNot(@Param("code") String code, @Param("id") Long id);

    // ── Active-Only Queries ───────────────────────────────────────────────────
    List<Warehouse> findAllByActiveTrue();

    Page<Warehouse> findAllByActiveTrue(Pageable pageable);

    /**
     * Search active warehouses by name, code, city, or manager name.
     */
    @Query("""
            SELECT w FROM Warehouse w
            WHERE w.active = true
            AND (LOWER(w.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
                 OR LOWER(w.code) LIKE LOWER(CONCAT('%', :keyword, '%'))
                 OR LOWER(w.city) LIKE LOWER(CONCAT('%', :keyword, '%'))
                 OR LOWER(w.managerName) LIKE LOWER(CONCAT('%', :keyword, '%')))
            """)
    Page<Warehouse> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    Optional<Warehouse> findByCodeIgnoreCaseAndActiveTrue(String code);
}
