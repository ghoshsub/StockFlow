package com.stockflow.backend.repository;

import com.stockflow.backend.entity.MovementType;
import com.stockflow.backend.entity.StockMovement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * StockMovementRepository — Data access layer for StockMovement entity.
 *
 * Provides:
 *  1. History by product (paginated)
 *  2. Latest movement per product (for InventoryResponse.lastMovementAt)
 *  3. Advanced search by product / type / date range / user
 */
@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {

    /**
     * Full movement history for a product ordered by most-recent-first.
     * Used by GET /api/inventory/history/{productId}.
     */
    Page<StockMovement> findByProductIdOrderByMovementDateDesc(Long productId, Pageable pageable);

    /**
     * Latest movement for a product — used to populate lastMovementAt in InventoryResponse.
     */
    Optional<StockMovement> findTopByProductIdOrderByMovementDateDesc(Long productId);

    /**
     * Advanced search with optional filters.
     * All parameters are optional — null values are ignored via conditional JPQL.
     *
     * Searchable by:
     *  - productId       (exact FK match)
     *  - movementType    (enum)
     *  - dateFrom/dateTo (inclusive date range)
     *  - username        (case-insensitive partial match on User.username)
     */
    @Query("""
            SELECT sm FROM StockMovement sm
            WHERE (:productId    IS NULL OR sm.product.id             = :productId)
            AND   (:movementType IS NULL OR sm.movementType           = :movementType)
            AND   (:dateFrom     IS NULL OR sm.movementDate          >= :dateFrom)
            AND   (:dateTo       IS NULL OR sm.movementDate          <= :dateTo)
            AND   (:username     IS NULL OR LOWER(sm.user.username)  LIKE LOWER(CONCAT('%', :username, '%')))
            ORDER BY sm.movementDate DESC
            """)
    Page<StockMovement> searchMovements(
            @Param("productId")    Long productId,
            @Param("movementType") MovementType movementType,
            @Param("dateFrom")     LocalDateTime dateFrom,
            @Param("dateTo")       LocalDateTime dateTo,
            @Param("username")     String username,
            Pageable pageable);

    /**
     * Count total movements for a product — useful for summary stats.
     */
    long countByProductId(Long productId);
}
