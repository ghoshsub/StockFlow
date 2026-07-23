package com.stockflow.backend.service;

import com.stockflow.backend.dto.purchase.PurchaseRequest;
import com.stockflow.backend.dto.purchase.PurchaseResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

/**
 * PurchaseService — Business operations interface for Purchase module.
 */
public interface PurchaseService {

    /** Create a new purchase order and automatically update inventory (STOCK_IN) */
    PurchaseResponse createPurchase(PurchaseRequest request, String username);

    /** Get purchase by ID */
    PurchaseResponse getPurchaseById(Long id);

    /** Get all purchases (flat list) */
    List<PurchaseResponse> getAllPurchases();

    /** Get paginated purchases */
    Page<PurchaseResponse> getAllPurchasesPaginated(Pageable pageable);

    /** Search purchases */
    Page<PurchaseResponse> searchPurchases(
            String purchaseNumber,
            String supplierName,
            String invoiceNumber,
            String productKeyword,
            LocalDateTime dateFrom,
            LocalDateTime dateTo,
            Pageable pageable);

    /** Filter purchases */
    Page<PurchaseResponse> filterPurchases(
            Long supplierId,
            Long warehouseId,
            String paymentStatus,
            LocalDateTime dateFrom,
            LocalDateTime dateTo,
            Pageable pageable);
}
