package com.stockflow.backend.service;

import com.stockflow.backend.dto.sale.SaleRequest;
import com.stockflow.backend.dto.sale.SaleResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

/**
 * SaleService — Business operations interface for Sales module.
 */
public interface SaleService {

    /** Create a sales transaction and automatically reduce inventory (STOCK_OUT) */
    SaleResponse createSale(SaleRequest request, String username);

    /** Get sale by ID */
    SaleResponse getSaleById(Long id);

    /** Get all sales (flat list) */
    List<SaleResponse> getAllSales();

    /** Get paginated sales */
    Page<SaleResponse> getAllSalesPaginated(Pageable pageable);

    /** Search sales */
    Page<SaleResponse> searchSales(
            String saleNumber,
            String customerName,
            String customerEmail,
            String productKeyword,
            LocalDateTime dateFrom,
            LocalDateTime dateTo,
            Pageable pageable);

    /** Filter sales */
    Page<SaleResponse> filterSales(
            String paymentStatus,
            String paymentMethod,
            String customerName,
            LocalDateTime dateFrom,
            LocalDateTime dateTo,
            Pageable pageable);
}
