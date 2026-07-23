package com.stockflow.backend.service;

import com.stockflow.backend.dto.brand.BrandRequest;
import com.stockflow.backend.dto.brand.BrandResponse;
import com.stockflow.backend.entity.Brand;
import com.stockflow.backend.exception.BrandNotFoundException;
import com.stockflow.backend.exception.DuplicateResourceException;
import com.stockflow.backend.mapper.BrandMapper;
import com.stockflow.backend.repository.BrandRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * BrandServiceImpl — Implementation of BrandService.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BrandServiceImpl implements BrandService {

    private final BrandRepository brandRepository;
    private final BrandMapper brandMapper;

    // ── CREATE ────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public BrandResponse createBrand(BrandRequest request) {
        log.info("Creating brand with name: '{}'", request.getName());

        if (brandRepository.existsByNameIgnoreCase(request.getName().trim())) {
            throw new DuplicateResourceException(
                    "Brand with name '" + request.getName().trim() + "' already exists");
        }

        Brand brand = brandMapper.toEntity(request);
        Brand saved = brandRepository.save(brand);

        log.info("Brand created successfully — id: {}, name: '{}'", saved.getId(), saved.getName());
        return brandMapper.toResponse(saved);
    }

    // ── READ (single) ─────────────────────────────────────────────────────────

    @Override
    public BrandResponse getBrandById(Long id) {
        log.debug("Fetching brand by id: {}", id);
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new BrandNotFoundException(id));
        return brandMapper.toResponse(brand);
    }

    // ── READ (all) ────────────────────────────────────────────────────────────

    @Override
    public List<BrandResponse> getAllBrands() {
        log.debug("Fetching all active brands");
        List<Brand> brands = brandRepository.findAllByActiveTrue();
        return brandMapper.toResponseList(brands);
    }

    @Override
    public Page<BrandResponse> getAllBrandsPaginated(Pageable pageable) {
        log.debug("Fetching brands — page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());
        Page<Brand> page = brandRepository.findAllByActiveTrue(pageable);
        return brandMapper.toResponsePage(page);
    }

    // ── UPDATE ────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public BrandResponse updateBrand(Long id, BrandRequest request) {
        log.info("Updating brand id: {}", id);

        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new BrandNotFoundException(id));

        if (brandRepository.existsByNameIgnoreCaseAndIdNot(request.getName().trim(), id)) {
            throw new DuplicateResourceException(
                    "Brand with name '" + request.getName().trim() + "' already exists");
        }

        brandMapper.updateEntity(request, brand);
        Brand updated = brandRepository.save(brand);

        log.info("Brand updated — id: {}, name: '{}'", updated.getId(), updated.getName());
        return brandMapper.toResponse(updated);
    }

    // ── DELETE (soft) ─────────────────────────────────────────────────────────

    @Override
    @Transactional
    public void deleteBrand(Long id) {
        log.info("Soft-deleting brand id: {}", id);

        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new BrandNotFoundException(id));

        if (!brand.getActive()) {
            log.warn("Brand id: {} is already inactive", id);
            return;
        }

        brand.setActive(false);
        brandRepository.save(brand);

        log.info("Brand id: {} soft-deleted successfully", id);
    }

    // ── SEARCH ────────────────────────────────────────────────────────────────

    @Override
    public Page<BrandResponse> searchBrands(String keyword, Pageable pageable) {
        log.debug("Searching brands with keyword: '{}'", keyword);

        if (keyword == null || keyword.isBlank()) {
            return getAllBrandsPaginated(pageable);
        }

        Page<Brand> results = brandRepository.searchByKeyword(keyword.trim(), pageable);
        return brandMapper.toResponsePage(results);
    }
}
