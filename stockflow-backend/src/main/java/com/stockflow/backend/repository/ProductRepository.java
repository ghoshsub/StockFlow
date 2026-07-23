package com.stockflow.backend.repository;

import com.stockflow.backend.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * ProductRepository — Data access layer for Product entity with optimized JPQL queries.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // ── Uniqueness Checks ─────────────────────────────────────────────────────
    boolean existsBySkuIgnoreCase(String sku);

    boolean existsByBarcodeIgnoreCase(String barcode);

    @Query("SELECT COUNT(p) > 0 FROM Product p WHERE LOWER(p.barcode) = LOWER(:barcode) AND p.id <> :id")
    boolean existsByBarcodeIgnoreCaseAndIdNot(@Param("barcode") String barcode, @Param("id") Long id);

    // ── Active Lookups ────────────────────────────────────────────────────────
    Optional<Product> findBySkuIgnoreCaseAndActiveTrue(String sku);

    Optional<Product> findByBarcodeIgnoreCaseAndActiveTrue(String barcode);

    Page<Product> findAllByActiveTrue(Pageable pageable);

    List<Product> findAllByActiveTrue();

    long countByActiveTrue();

    @Query("SELECT COUNT(p) FROM Product p WHERE p.active = true AND p.quantity = 0")
    long countOutOfStockProducts();

    @Query("SELECT COUNT(p) FROM Product p WHERE p.active = true AND p.quantity <= p.minimumStock")
    long countLowStockProducts();

    @Query("SELECT COALESCE(SUM(p.quantity * p.buyingPrice), 0.0) FROM Product p WHERE p.active = true")
    BigDecimal calculateTotalInventoryValue();

    @Query("SELECT p FROM Product p WHERE p.active = true AND p.quantity = 0")
    List<Product> findOutOfStockProductsList();

    @Query("SELECT p FROM Product p WHERE p.active = true AND p.quantity <= p.minimumStock")
    List<Product> findLowStockProductsList();

    List<Product> findTop10ByActiveTrueOrderByQuantityDesc();

    List<Product> findTop10ByActiveTrueOrderByQuantityAsc();

    @Query("""
            SELECT p.category.name AS categoryName, COALESCE(SUM(p.quantity * p.buyingPrice), 0.0) AS totalValuation, COUNT(p) AS itemCount
            FROM Product p WHERE p.active = true
            GROUP BY p.category.name
            """)
    List<Object[]> getInventoryValueByCategory();

    @Query("""
            SELECT p.warehouse.name AS warehouseName, COALESCE(SUM(p.quantity * p.buyingPrice), 0.0) AS totalValuation, COUNT(p) AS itemCount
            FROM Product p WHERE p.active = true
            GROUP BY p.warehouse.name
            """)
    List<Object[]> getInventoryValueByWarehouse();

    // ── Search Query ──────────────────────────────────────────────────────────
    @Query("""
            SELECT p FROM Product p
            WHERE p.active = true
            AND (LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
                 OR LOWER(p.sku) LIKE LOWER(CONCAT('%', :keyword, '%'))
                 OR LOWER(p.barcode) LIKE LOWER(CONCAT('%', :keyword, '%'))
                 OR LOWER(p.category.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
                 OR LOWER(p.brand.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
                 OR LOWER(p.supplier.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
                 OR LOWER(p.warehouse.name) LIKE LOWER(CONCAT('%', :keyword, '%')))
            """)
    Page<Product> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    // ── Filter Query ──────────────────────────────────────────────────────────
    @Query("""
            SELECT p FROM Product p
            WHERE ((:active IS NULL AND p.active = true) OR p.active = :active)
            AND (:categoryId IS NULL OR p.category.id = :categoryId)
            AND (:brandId IS NULL OR p.brand.id = :brandId)
            AND (:supplierId IS NULL OR p.supplier.id = :supplierId)
            AND (:warehouseId IS NULL OR p.warehouse.id = :warehouseId)
            """)
    Page<Product> filterProducts(
            @Param("categoryId") Long categoryId,
            @Param("brandId") Long brandId,
            @Param("supplierId") Long supplierId,
            @Param("warehouseId") Long warehouseId,
            @Param("active") Boolean active,
            Pageable pageable
    );

    // ── Low Stock Query ───────────────────────────────────────────────────────
    @Query("SELECT p FROM Product p WHERE p.active = true AND p.quantity <= p.minimumStock")
    Page<Product> findLowStockProducts(Pageable pageable);
}
