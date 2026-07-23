package com.stockflow.backend.repository;

import com.stockflow.backend.entity.Sale;
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
 * SaleRepository — Data access layer for Sale entity with JPQL aggregations.
 */
@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {

    boolean existsBySaleNumberIgnoreCase(String saleNumber);

    @Query("SELECT COALESCE(SUM(s.totalAmount), 0.0) FROM Sale s")
    BigDecimal calculateTotalRevenue();

    @Query("SELECT COALESCE(SUM(s.totalAmount), 0.0) FROM Sale s WHERE s.saleDate >= :startDate AND s.saleDate <= :endDate")
    BigDecimal calculateSalesBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("""
            SELECT FUNCTION('YEAR', s.saleDate) AS yr, FUNCTION('MONTH', s.saleDate) AS mth, COALESCE(SUM(s.totalAmount), 0.0) AS totalRev, COUNT(s) AS saleCount
            FROM Sale s
            GROUP BY FUNCTION('YEAR', s.saleDate), FUNCTION('MONTH', s.saleDate)
            ORDER BY FUNCTION('YEAR', s.saleDate) DESC, FUNCTION('MONTH', s.saleDate) DESC
            """)
    List<Object[]> getMonthlySalesTrend();

    @Query("""
            SELECT item.product.name AS productName, COALESCE(SUM(item.subtotal), 0.0) AS totalRevenue, COALESCE(SUM(item.quantity), 0) AS totalQuantity
            FROM SaleItem item
            GROUP BY item.product.name
            ORDER BY SUM(item.subtotal) DESC
            """)
    List<Object[]> getTopSellingProducts();

    @Query("""
            SELECT s.customerName AS customerName, COALESCE(SUM(s.totalAmount), 0.0) AS totalSpent, COUNT(s) AS orderCount
            FROM Sale s
            WHERE s.customerName IS NOT NULL AND s.customerName <> ''
            GROUP BY s.customerName
            ORDER BY SUM(s.totalAmount) DESC
            """)
    List<Object[]> getTopCustomers();

    @Query("""
            SELECT DISTINCT s FROM Sale s
            LEFT JOIN s.saleItems item
            LEFT JOIN item.product prd
            WHERE (:saleNumber    IS NULL OR LOWER(s.saleNumber)    LIKE LOWER(CONCAT('%', :saleNumber, '%')))
            AND   (:customerName  IS NULL OR LOWER(s.customerName)  LIKE LOWER(CONCAT('%', :customerName, '%')))
            AND   (:customerEmail IS NULL OR LOWER(s.customerEmail) LIKE LOWER(CONCAT('%', :customerEmail, '%')))
            AND   (:productKeyword IS NULL OR LOWER(prd.name)       LIKE LOWER(CONCAT('%', :productKeyword, '%'))
                                          OR LOWER(prd.sku)        LIKE LOWER(CONCAT('%', :productKeyword, '%')))
            AND   (:dateFrom      IS NULL OR s.saleDate             >= :dateFrom)
            AND   (:dateTo        IS NULL OR s.saleDate             <= :dateTo)
            ORDER BY s.saleDate DESC
            """)
    Page<Sale> searchSales(
            @Param("saleNumber")     String saleNumber,
            @Param("customerName")   String customerName,
            @Param("customerEmail")  String customerEmail,
            @Param("productKeyword") String productKeyword,
            @Param("dateFrom")       LocalDateTime dateFrom,
            @Param("dateTo")         LocalDateTime dateTo,
            Pageable pageable);

    @Query("""
            SELECT s FROM Sale s
            WHERE (:paymentStatus IS NULL OR LOWER(s.paymentStatus) = LOWER(:paymentStatus))
            AND   (:paymentMethod IS NULL OR LOWER(s.paymentMethod) = LOWER(:paymentMethod))
            AND   (:customerName  IS NULL OR LOWER(s.customerName)  LIKE LOWER(CONCAT('%', :customerName, '%')))
            AND   (:dateFrom      IS NULL OR s.saleDate             >= :dateFrom)
            AND   (:dateTo        IS NULL OR s.saleDate             <= :dateTo)
            ORDER BY s.saleDate DESC
            """)
    Page<Sale> filterSales(
            @Param("paymentStatus") String paymentStatus,
            @Param("paymentMethod") String paymentMethod,
            @Param("customerName")  String customerName,
            @Param("dateFrom")      LocalDateTime dateFrom,
            @Param("dateTo")        LocalDateTime dateTo,
            Pageable pageable);
}
