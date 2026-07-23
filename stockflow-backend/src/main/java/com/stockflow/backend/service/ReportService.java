package com.stockflow.backend.service;

import com.stockflow.backend.dto.report.FinancialSummaryReportResponse;
import com.stockflow.backend.dto.report.InventoryReportResponse;
import com.stockflow.backend.dto.report.PurchaseReportResponse;
import com.stockflow.backend.dto.report.SalesReportResponse;

import java.time.LocalDateTime;

/**
 * ReportService — Service interface for generating JSON reports and binary file exports (PDF, Excel, CSV).
 */
public interface ReportService {

    InventoryReportResponse getInventoryReport();

    PurchaseReportResponse getPurchaseReport(Long supplierId, Long warehouseId, LocalDateTime dateFrom, LocalDateTime dateTo);

    SalesReportResponse getSalesReport(String customerName, String productKeyword, LocalDateTime dateFrom, LocalDateTime dateTo);

    FinancialSummaryReportResponse getFinancialSummaryReport();

    byte[] exportInventoryReport(String format);

    byte[] exportPurchaseReport(Long supplierId, Long warehouseId, LocalDateTime dateFrom, LocalDateTime dateTo, String format);

    byte[] exportSalesReport(String customerName, String productKeyword, LocalDateTime dateFrom, LocalDateTime dateTo, String format);

    byte[] exportFinancialSummaryReport(String format);
}
