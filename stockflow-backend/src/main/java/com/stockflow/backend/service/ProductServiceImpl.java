package com.stockflow.backend.service;

import com.stockflow.backend.dto.product.ProductRequest;
import com.stockflow.backend.dto.product.ProductResponse;
import com.stockflow.backend.entity.*;
import com.stockflow.backend.exception.*;
import com.stockflow.backend.mapper.ProductMapper;
import com.stockflow.backend.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * ProductServiceImpl — Core implementation of ProductService.
 *
 * Key responsibilities:
 *  1. Auto-generate SKU in format: PRD-YYYYMMDD-XXXXX (random alphanumeric suffix)
 *  2. Validate sellingPrice > buyingPrice at business logic level
 *  3. Resolve FK entities (Category, Brand, Supplier, Warehouse) by ID before saving
 *  4. Enforce barcode uniqueness
 *  5. Soft-delete (active = false)
 *  6. Support multi-field search, filter, and low-stock queries
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final SupplierRepository supplierRepository;
    private final WarehouseRepository warehouseRepository;

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int SKU_SUFFIX_LENGTH = 6;

    // ── SKU Generation ────────────────────────────────────────────────────────

    /**
     * Generates a unique SKU in format: PRD-YYYYMMDD-XXXXXX
     * Retries up to 10 times to guarantee uniqueness.
     */
    private String generateUniqueSku() {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        for (int attempt = 0; attempt < 10; attempt++) {
            StringBuilder suffix = new StringBuilder();
            for (int i = 0; i < SKU_SUFFIX_LENGTH; i++) {
                suffix.append(CHARACTERS.charAt(ThreadLocalRandom.current().nextInt(CHARACTERS.length())));
            }
            String sku = "PRD-" + date + "-" + suffix;
            if (!productRepository.existsBySkuIgnoreCase(sku)) {
                return sku;
            }
        }
        throw new IllegalStateException("Failed to generate unique SKU after 10 attempts. Please retry.");
    }

    // ── Entity Resolvers ──────────────────────────────────────────────────────

    private Category resolveCategory(Long id) {
        return categoryRepository.findById(id)
                .filter(Category::getActive)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found or inactive with id: " + id));
    }

    private Brand resolveBrand(Long id) {
        return brandRepository.findById(id)
                .filter(Brand::getActive)
                .orElseThrow(() -> new BrandNotFoundException("Brand not found or inactive with id: " + id));
    }

    private Supplier resolveSupplier(Long id) {
        return supplierRepository.findById(id)
                .filter(Supplier::getActive)
                .orElseThrow(() -> new SupplierNotFoundException("Supplier not found or inactive with id: " + id));
    }

    private Warehouse resolveWarehouse(Long id) {
        return warehouseRepository.findById(id)
                .filter(Warehouse::getActive)
                .orElseThrow(() -> new WarehouseNotFoundException("Warehouse not found or inactive with id: " + id));
    }

    // ── Business Validation ───────────────────────────────────────────────────

    private void validatePrices(ProductRequest request) {
        if (request.getSellingPrice().compareTo(request.getBuyingPrice()) <= 0) {
            throw new IllegalArgumentException(
                    "Selling price (" + request.getSellingPrice() +
                    ") must be greater than buying price (" + request.getBuyingPrice() + ")");
        }
    }

    // ── CREATE ────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        log.info("Creating product with name: '{}'", request.getName());

        // Business rule: selling price > buying price
        validatePrices(request);

        // Barcode uniqueness check
        if (request.getBarcode() != null && !request.getBarcode().isBlank()) {
            if (productRepository.existsByBarcodeIgnoreCase(request.getBarcode().trim())) {
                throw new DuplicateResourceException(
                        "Product with barcode '" + request.getBarcode().trim() + "' already exists");
            }
        }

        // Resolve FK entities
        Category category = resolveCategory(request.getCategoryId());
        Brand brand = resolveBrand(request.getBrandId());
        Supplier supplier = resolveSupplier(request.getSupplierId());
        Warehouse warehouse = resolveWarehouse(request.getWarehouseId());

        // Auto-generate SKU
        String sku = generateUniqueSku();

        Product product = productMapper.toEntity(request, category, brand, supplier, warehouse, sku);
        Product saved = productRepository.save(product);

        log.info("Product created — id: {}, SKU: '{}', name: '{}'", saved.getId(), saved.getSku(), saved.getName());
        return productMapper.toResponse(saved);
    }

    // ── READ (single) ─────────────────────────────────────────────────────────

    @Override
    public ProductResponse getProductById(Long id) {
        log.debug("Fetching product by id: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        return productMapper.toResponse(product);
    }

    // ── READ (all) ────────────────────────────────────────────────────────────

    @Override
    public List<ProductResponse> getAllProducts() {
        log.debug("Fetching all active products");
        return productMapper.toResponseList(productRepository.findAllByActiveTrue());
    }

    @Override
    public Page<ProductResponse> getAllProductsPaginated(Pageable pageable) {
        log.debug("Fetching products — page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());
        return productMapper.toResponsePage(productRepository.findAllByActiveTrue(pageable));
    }

    // ── UPDATE ────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        log.info("Updating product id: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        // Business rule: selling price > buying price
        validatePrices(request);

        // Barcode uniqueness excluding self
        if (request.getBarcode() != null && !request.getBarcode().isBlank()) {
            if (productRepository.existsByBarcodeIgnoreCaseAndIdNot(request.getBarcode().trim(), id)) {
                throw new DuplicateResourceException(
                        "Product with barcode '" + request.getBarcode().trim() + "' already exists");
            }
        }

        // Resolve FK entities
        Category category = resolveCategory(request.getCategoryId());
        Brand brand = resolveBrand(request.getBrandId());
        Supplier supplier = resolveSupplier(request.getSupplierId());
        Warehouse warehouse = resolveWarehouse(request.getWarehouseId());

        productMapper.updateEntity(request, product, category, brand, supplier, warehouse);
        Product updated = productRepository.save(product);

        log.info("Product updated — id: {}, SKU: '{}'", updated.getId(), updated.getSku());
        return productMapper.toResponse(updated);
    }

    // ── DELETE (soft) ─────────────────────────────────────────────────────────

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        log.info("Soft-deleting product id: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        if (!product.getActive()) {
            log.warn("Product id: {} is already inactive", id);
            return;
        }

        product.setActive(false);
        productRepository.save(product);
        log.info("Product id: {} soft-deleted", id);
    }

    // ── SEARCH ────────────────────────────────────────────────────────────────

    @Override
    public Page<ProductResponse> searchProducts(String keyword, Pageable pageable) {
        log.debug("Searching products with keyword: '{}'", keyword);
        if (keyword == null || keyword.isBlank()) {
            return getAllProductsPaginated(pageable);
        }
        return productMapper.toResponsePage(
                productRepository.searchByKeyword(keyword.trim(), pageable));
    }

    // ── FILTER ────────────────────────────────────────────────────────────────

    @Override
    public Page<ProductResponse> filterProducts(Long categoryId, Long brandId, Long supplierId,
                                                Long warehouseId, Boolean active, Pageable pageable) {
        log.debug("Filtering products — cat:{}, brand:{}, supplier:{}, warehouse:{}, active:{}",
                categoryId, brandId, supplierId, warehouseId, active);
        return productMapper.toResponsePage(
                productRepository.filterProducts(categoryId, brandId, supplierId, warehouseId, active, pageable));
    }

    // ── LOW STOCK ─────────────────────────────────────────────────────────────

    @Override
    public Page<ProductResponse> getLowStockProducts(Pageable pageable) {
        log.debug("Fetching low-stock products");
        return productMapper.toResponsePage(
                productRepository.findLowStockProducts(pageable));
    }
}
