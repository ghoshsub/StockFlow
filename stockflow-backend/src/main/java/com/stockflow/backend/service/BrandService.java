package com.stockflow.backend.service;

import com.stockflow.backend.dto.brand.BrandRequest;
import com.stockflow.backend.dto.brand.BrandResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * BrandService — Service interface defining the Brand module contract.
 */
public interface BrandService {

    /**
     * Create a new brand.
     * Throws DuplicateResourceException if the name already exists.
     */
    BrandResponse createBrand(BrandRequest request);

    /**
     * Retrieve a brand by its ID.
     * Throws BrandNotFoundException if not found.
     */
    BrandResponse getBrandById(Long id);

    /**
     * Retrieve all active brands (non-paginated).
     */
    List<BrandResponse> getAllBrands();

    /**
     * Retrieve a paginated and sorted list of all active brands.
     */
    Page<BrandResponse> getAllBrandsPaginated(Pageable pageable);

    /**
     * Update an existing brand by ID.
     */
    BrandResponse updateBrand(Long id, BrandRequest request);

    /**
     * Soft-delete a brand by setting active = false.
     */
    void deleteBrand(Long id);

    /**
     * Search active brands by keyword.
     */
    Page<BrandResponse> searchBrands(String keyword, Pageable pageable);
}
