package com.stockflow.backend.repository;

import com.stockflow.backend.entity.Supplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * SupplierRepository — Data access layer for Supplier entity.
 */
@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {

    // ── Name Uniqueness ───────────────────────────────────────────────────────
    boolean existsByNameIgnoreCase(String name);

    @Query("SELECT COUNT(s) > 0 FROM Supplier s WHERE LOWER(s.name) = LOWER(:name) AND s.id <> :id")
    boolean existsByNameIgnoreCaseAndIdNot(@Param("name") String name, @Param("id") Long id);

    // ── Email Uniqueness ──────────────────────────────────────────────────────
    boolean existsByEmailIgnoreCase(String email);

    @Query("SELECT COUNT(s) > 0 FROM Supplier s WHERE LOWER(s.email) = LOWER(:email) AND s.id <> :id")
    boolean existsByEmailIgnoreCaseAndIdNot(@Param("email") String email, @Param("id") Long id);

    // ── GST Uniqueness ────────────────────────────────────────────────────────
    boolean existsByGstNumberIgnoreCase(String gstNumber);

    @Query("SELECT COUNT(s) > 0 FROM Supplier s WHERE LOWER(s.gstNumber) = LOWER(:gstNumber) AND s.id <> :id")
    boolean existsByGstNumberIgnoreCaseAndIdNot(@Param("gstNumber") String gstNumber, @Param("id") Long id);

    // ── Active-Only Queries ───────────────────────────────────────────────────
    List<Supplier> findAllByActiveTrue();

    Page<Supplier> findAllByActiveTrue(Pageable pageable);

    /**
     * Search active suppliers by name, contact person, email, phone, or city.
     */
    @Query("""
            SELECT s FROM Supplier s
            WHERE s.active = true
            AND (LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
                 OR LOWER(s.contactPerson) LIKE LOWER(CONCAT('%', :keyword, '%'))
                 OR LOWER(s.email) LIKE LOWER(CONCAT('%', :keyword, '%'))
                 OR LOWER(s.phone) LIKE LOWER(CONCAT('%', :keyword, '%'))
                 OR LOWER(s.city) LIKE LOWER(CONCAT('%', :keyword, '%')))
            """)
    Page<Supplier> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    Optional<Supplier> findByNameIgnoreCaseAndActiveTrue(String name);
}
