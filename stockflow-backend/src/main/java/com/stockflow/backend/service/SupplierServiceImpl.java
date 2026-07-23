package com.stockflow.backend.service;

import com.stockflow.backend.dto.supplier.SupplierRequest;
import com.stockflow.backend.dto.supplier.SupplierResponse;
import com.stockflow.backend.entity.Supplier;
import com.stockflow.backend.exception.DuplicateResourceException;
import com.stockflow.backend.exception.SupplierNotFoundException;
import com.stockflow.backend.mapper.SupplierMapper;
import com.stockflow.backend.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * SupplierServiceImpl — Implementation of SupplierService with complete business logic.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SupplierServiceImpl implements SupplierService {

    private final SupplierRepository supplierRepository;
    private final SupplierMapper supplierMapper;

    // ── CREATE ────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public SupplierResponse createSupplier(SupplierRequest request) {
        log.info("Creating supplier with name: '{}', email: '{}'", request.getName(), request.getEmail());

        String trimmedName = request.getName().trim();
        String trimmedEmail = request.getEmail().trim();
        String trimmedGst = request.getGstNumber() != null ? request.getGstNumber().trim() : null;

        // Uniqueness checks
        if (supplierRepository.existsByNameIgnoreCase(trimmedName)) {
            throw new DuplicateResourceException("Supplier with name '" + trimmedName + "' already exists");
        }
        if (supplierRepository.existsByEmailIgnoreCase(trimmedEmail)) {
            throw new DuplicateResourceException("Supplier with email '" + trimmedEmail + "' already exists");
        }
        if (trimmedGst != null && !trimmedGst.isBlank() && supplierRepository.existsByGstNumberIgnoreCase(trimmedGst)) {
            throw new DuplicateResourceException("Supplier with GST number '" + trimmedGst + "' already exists");
        }

        Supplier supplier = supplierMapper.toEntity(request);
        Supplier saved = supplierRepository.save(supplier);

        log.info("Supplier created successfully — id: {}, name: '{}'", saved.getId(), saved.getName());
        return supplierMapper.toResponse(saved);
    }

    // ── READ (single) ─────────────────────────────────────────────────────────

    @Override
    public SupplierResponse getSupplierById(Long id) {
        log.debug("Fetching supplier by id: {}", id);
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new SupplierNotFoundException(id));
        return supplierMapper.toResponse(supplier);
    }

    // ── READ (all) ────────────────────────────────────────────────────────────

    @Override
    public List<SupplierResponse> getAllSuppliers() {
        log.debug("Fetching all active suppliers");
        List<Supplier> suppliers = supplierRepository.findAllByActiveTrue();
        return supplierMapper.toResponseList(suppliers);
    }

    @Override
    public Page<SupplierResponse> getAllSuppliersPaginated(Pageable pageable) {
        log.debug("Fetching suppliers page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());
        Page<Supplier> page = supplierRepository.findAllByActiveTrue(pageable);
        return supplierMapper.toResponsePage(page);
    }

    // ── UPDATE ────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public SupplierResponse updateSupplier(Long id, SupplierRequest request) {
        log.info("Updating supplier id: {}", id);

        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new SupplierNotFoundException(id));

        String trimmedName = request.getName().trim();
        String trimmedEmail = request.getEmail().trim();
        String trimmedGst = request.getGstNumber() != null ? request.getGstNumber().trim() : null;

        // Uniqueness checks excluding current ID
        if (supplierRepository.existsByNameIgnoreCaseAndIdNot(trimmedName, id)) {
            throw new DuplicateResourceException("Supplier with name '" + trimmedName + "' already exists");
        }
        if (supplierRepository.existsByEmailIgnoreCaseAndIdNot(trimmedEmail, id)) {
            throw new DuplicateResourceException("Supplier with email '" + trimmedEmail + "' already exists");
        }
        if (trimmedGst != null && !trimmedGst.isBlank() && supplierRepository.existsByGstNumberIgnoreCaseAndIdNot(trimmedGst, id)) {
            throw new DuplicateResourceException("Supplier with GST number '" + trimmedGst + "' already exists");
        }

        supplierMapper.updateEntity(request, supplier);
        Supplier updated = supplierRepository.save(supplier);

        log.info("Supplier updated — id: {}, name: '{}'", updated.getId(), updated.getName());
        return supplierMapper.toResponse(updated);
    }

    // ── DELETE (soft) ─────────────────────────────────────────────────────────

    @Override
    @Transactional
    public void deleteSupplier(Long id) {
        log.info("Soft-deleting supplier id: {}", id);

        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new SupplierNotFoundException(id));

        if (!supplier.getActive()) {
            log.warn("Supplier id: {} is already inactive", id);
            return;
        }

        supplier.setActive(false);
        supplierRepository.save(supplier);

        log.info("Supplier id: {} soft-deleted successfully", id);
    }

    // ── SEARCH ────────────────────────────────────────────────────────────────

    @Override
    public Page<SupplierResponse> searchSuppliers(String keyword, Pageable pageable) {
        log.debug("Searching suppliers with keyword: '{}'", keyword);

        if (keyword == null || keyword.isBlank()) {
            return getAllSuppliersPaginated(pageable);
        }

        Page<Supplier> results = supplierRepository.searchByKeyword(keyword.trim(), pageable);
        return supplierMapper.toResponsePage(results);
    }
}
