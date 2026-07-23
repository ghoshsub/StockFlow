package com.stockflow.backend.service;

import com.stockflow.backend.dto.warehouse.WarehouseRequest;
import com.stockflow.backend.dto.warehouse.WarehouseResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * WarehouseService — Service interface defining the Warehouse module contract.
 */
public interface WarehouseService {

    WarehouseResponse createWarehouse(WarehouseRequest request);

    WarehouseResponse getWarehouseById(Long id);

    List<WarehouseResponse> getAllWarehouses();

    Page<WarehouseResponse> getAllWarehousesPaginated(Pageable pageable);

    WarehouseResponse updateWarehouse(Long id, WarehouseRequest request);

    void deleteWarehouse(Long id);

    Page<WarehouseResponse> searchWarehouses(String keyword, Pageable pageable);
}
