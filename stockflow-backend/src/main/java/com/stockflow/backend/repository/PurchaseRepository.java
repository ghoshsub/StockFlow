package com.stockflow.backend.repository;

import com.stockflow.backend.entity.Purchase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * PurchaseRepository — Data access for Purchase entity with optimized JPQL aggregations.
 */
@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

    boolean existsByPurchaseNumberIgnoreCase(String purchaseNumber);

    boolean existsByInvoiceNumberIgnoreCase(String invoiceNumber);

    @Query("SELECT COALESCE(SUM(p.totalAmount), 0.0) FROM Purchase p")
    BigDecimal calculateTotalPurchaseCost();

    @Query("SELECT COALESCE(SUM(p.totalAmount), 0.0) FROM Purchase p WHERE p.purchaseDate >= :startDate AND p.purchaseDate <= :endDate")
    BigDecimal calculatePurchasesBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("""
            SELECT p.supplier.name AS supplierName, COALESCE(SUM(p.totalAmount), 0.0) AS totalSpent, COUNT(p) AS orderCount
            FROM Purchase p
            GROUP BY p.supplier.name
            ORDER BY SUM(p.totalAmount) DESC
            """)
    List<Object[]> getTopSuppliers();

    @Query("""
            SELECT FUNCTION('YEAR', p.purchaseDate) AS yr, FUNCTION('MONTH', p.purchaseDate) AS mth, COALESCE(SUM(p.totalAmount), 0.0) AS totalCost, COUNT(p) AS totalCount
            FROM Purchase p
            GROUP BY FUNCTION('YEAR', p.purchaseDate), FUNCTION('MONTH', p.purchaseDate)
            ORDER BY FUNCTION('YEAR', p.purchaseDate) DESC, FUNCTION('MONTH', p.purchaseDate) DESC
            """)
    List<Object[]> getMonthlyPurchaseTrend();

    @Query("""
            SELECT DISTINCT p FROM Purchase p
            LEFT JOIN p.supplier s
            LEFT JOIN p.purchaseItems item
            LEFT JOIN item.product prd
            WHERE (:purchaseNumber IS NULL OR LOWER(p.purchaseNumber) LIKE LOWER(CONCAT('%', :purchaseNumber, '%')))
            AND   (:supplierName   IS NULL OR LOWER(s.name)           LIKE LOWER(CONCAT('%', :supplierName, '%')))
            AND   (:invoiceNumber  IS NULL OR LOWER(p.invoiceNumber)  LIKE LOWER(CONCAT('%', :invoiceNumber, '%')))
            AND   (:productKeyword IS NULL OR LOWER(prd.name)         LIKE LOWER(CONCAT('%', :productKeyword, '%'))
                                          OR LOWER(prd.sku)          LIKE LOWER(CONCAT('%', :productKeyword, '%')))
            AND   (:dateFrom       IS NULL OR p.purchaseDate          >= :dateFrom)
            AND   (:dateTo         IS NULL OR p.purchaseDate          <= :dateTo)
            ORDER BY p.purchaseDate DESC
            """)
    Page<Purchase> searchPurchases(
            @Param("purchaseNumber") String purchaseNumber,
            @Param("supplierName")   String supplierName,
            @Param("invoiceNumber")  String invoiceNumber,
            @Param("productKeyword") String productKeyword,
            @Param("dateFrom")       LocalDateTime dateFrom,
            @Param("dateTo")         LocalDateTime dateTo,
            Pageable pageable);

    @Query("""
            SELECT p FROM Purchase p
            WHERE (:supplierId    IS NULL OR p.supplier.id     = :supplierId)
            AND   (:warehouseId   IS NULL OR p.warehouse.id    = :warehouseId)
            AND   (:paymentStatus IS NULL OR LOWER(p.paymentStatus) = LOWER(:paymentStatus))
            AND   (:dateFrom      IS NULL OR p.purchaseDate    >= :dateFrom)
            AND   (:dateTo        IS NULL OR p.purchaseDate    <= :dateTo)
            ORDER BY p.purchaseDate DESC
            """)
    Page<Purchase> filterPurchases(
            @Param("supplierId")    Long supplierId,
            @Param("warehouseId")   Long warehouseId,
            @Param("paymentStatus") String paymentStatus,
            @Param("dateFrom")      LocalDateTime dateFrom,
            @Param("dateTo")        LocalDateTime dateTo,
            Pageable pageable);
}
