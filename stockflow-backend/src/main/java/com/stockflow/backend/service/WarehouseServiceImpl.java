package com.stockflow.backend.service;

import com.stockflow.backend.dto.warehouse.WarehouseRequest;
import com.stockflow.backend.dto.warehouse.WarehouseResponse;
import com.stockflow.backend.entity.Warehouse;
import com.stockflow.backend.exception.DuplicateResourceException;
import com.stockflow.backend.exception.WarehouseNotFoundException;
import com.stockflow.backend.mapper.WarehouseMapper;
import com.stockflow.backend.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * WarehouseServiceImpl — Core implementation of WarehouseService.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WarehouseServiceImpl implements WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final WarehouseMapper warehouseMapper;

    // ── CREATE ────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public WarehouseResponse createWarehouse(WarehouseRequest request) {
        log.info("Creating warehouse with name: '{}', code: '{}'", request.getName(), request.getCode());

        String trimmedName = request.getName().trim();
        String trimmedCode = request.getCode().trim().toUpperCase();

        if (warehouseRepository.existsByNameIgnoreCase(trimmedName)) {
            throw new DuplicateResourceException("Warehouse with name '" + trimmedName + "' already exists");
        }
        if (warehouseRepository.existsByCodeIgnoreCase(trimmedCode)) {
            throw new DuplicateResourceException("Warehouse with code '" + trimmedCode + "' already exists");
        }

        Warehouse warehouse = warehouseMapper.toEntity(request);
        Warehouse saved = warehouseRepository.save(warehouse);

        log.info("Warehouse created successfully — id: {}, code: '{}'", saved.getId(), saved.getCode());
        return warehouseMapper.toResponse(saved);
    }

    // ── READ (single) ─────────────────────────────────────────────────────────

    @Override
    public WarehouseResponse getWarehouseById(Long id) {
        log.debug("Fetching warehouse by id: {}", id);
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new WarehouseNotFoundException(id));
        return warehouseMapper.toResponse(warehouse);
    }

    // ── READ (all) ────────────────────────────────────────────────────────────

    @Override
    public List<WarehouseResponse> getAllWarehouses() {
        log.debug("Fetching all active warehouses");
        List<Warehouse> warehouses = warehouseRepository.findAllByActiveTrue();
        return warehouseMapper.toResponseList(warehouses);
    }

    @Override
    public Page<WarehouseResponse> getAllWarehousesPaginated(Pageable pageable) {
        log.debug("Fetching warehouses page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());
        Page<Warehouse> page = warehouseRepository.findAllByActiveTrue(pageable);
        return warehouseMapper.toResponsePage(page);
    }

    // ── UPDATE ────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public WarehouseResponse updateWarehouse(Long id, WarehouseRequest request) {
        log.info("Updating warehouse id: {}", id);

        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new WarehouseNotFoundException(id));

        String trimmedName = request.getName().trim();
        String trimmedCode = request.getCode().trim().toUpperCase();

        if (warehouseRepository.existsByNameIgnoreCaseAndIdNot(trimmedName, id)) {
            throw new DuplicateResourceException("Warehouse with name '" + trimmedName + "' already exists");
        }
        if (warehouseRepository.existsByCodeIgnoreCaseAndIdNot(trimmedCode, id)) {
            throw new DuplicateResourceException("Warehouse with code '" + trimmedCode + "' already exists");
        }

        warehouseMapper.updateEntity(request, warehouse);
        Warehouse updated = warehouseRepository.save(warehouse);

        log.info("Warehouse updated — id: {}, code: '{}'", updated.getId(), updated.getCode());
        return warehouseMapper.toResponse(updated);
    }

    // ── DELETE (soft) ─────────────────────────────────────────────────────────

    @Override
    @Transactional
    public void deleteWarehouse(Long id) {
        log.info("Soft-deleting warehouse id: {}", id);

        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new WarehouseNotFoundException(id));

        if (!warehouse.getActive()) {
            log.warn("Warehouse id: {} is already inactive", id);
            return;
        }

        warehouse.setActive(false);
        warehouseRepository.save(warehouse);

        log.info("Warehouse id: {} soft-deleted successfully", id);
    }

    // ── SEARCH ────────────────────────────────────────────────────────────────

    @Override
    public Page<WarehouseResponse> searchWarehouses(String keyword, Pageable pageable) {
        log.debug("Searching warehouses with keyword: '{}'", keyword);

        if (keyword == null || keyword.isBlank()) {
            return getAllWarehousesPaginated(pageable);
        }

        Page<Warehouse> results = warehouseRepository.searchByKeyword(keyword.trim(), pageable);
        return warehouseMapper.toResponsePage(results);
    }
}
