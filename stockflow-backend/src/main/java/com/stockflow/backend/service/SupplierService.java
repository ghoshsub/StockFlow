package com.stockflow.backend.service;

import com.stockflow.backend.dto.supplier.SupplierRequest;
import com.stockflow.backend.dto.supplier.SupplierResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * SupplierService — Service interface defining the Supplier module contract.
 */
public interface SupplierService {

    SupplierResponse createSupplier(SupplierRequest request);

    SupplierResponse getSupplierById(Long id);

    List<SupplierResponse> getAllSuppliers();

    Page<SupplierResponse> getAllSuppliersPaginated(Pageable pageable);

    SupplierResponse updateSupplier(Long id, SupplierRequest request);

    void deleteSupplier(Long id);

    Page<SupplierResponse> searchSuppliers(String keyword, Pageable pageable);
}
