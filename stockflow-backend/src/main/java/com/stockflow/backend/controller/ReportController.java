package com.stockflow.backend.controller;

import com.stockflow.backend.dto.report.FinancialSummaryReportResponse;
import com.stockflow.backend.dto.report.InventoryReportResponse;
import com.stockflow.backend.dto.report.PurchaseReportResponse;
import com.stockflow.backend.dto.report.SalesReportResponse;
import com.stockflow.backend.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * ReportController — REST controller for Reports & Exports (JSON, PDF, Excel, CSV).
 */
@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
@Tag(name = "Reports & Exports", description = "Report generation and downloadable file exports (PDF, Excel .xlsx, CSV) for Inventory, Purchases, Sales, and Financial Summaries")
@SecurityRequirement(name = "Bearer Authentication")
public class ReportController {

    private final ReportService reportService;

    // ── JSON Endpoints ────────────────────────────────────────────────────────

    @Operation(summary = "Get Inventory Report (JSON)", description = "Returns active inventory items, low stock list, out of stock list, and total valuation.")
    @GetMapping("/inventory")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<InventoryReportResponse> getInventoryReport() {
        return ResponseEntity.ok(reportService.getInventoryReport());
    }

    @Operation(summary = "Get Purchase Report (JSON)", description = "Returns purchase orders filtered by date range, supplier, and warehouse.")
    @GetMapping("/purchases")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<PurchaseReportResponse> getPurchaseReport(
            @RequestParam(required = false) Long supplierId,
            @RequestParam(required = false) Long warehouseId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTo) {
        return ResponseEntity.ok(reportService.getPurchaseReport(supplierId, warehouseId, dateFrom, dateTo));
    }

    @Operation(summary = "Get Sales Report (JSON)", description = "Returns sales orders filtered by date range, customer name, and product.")
    @GetMapping("/sales")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<SalesReportResponse> getSalesReport(
            @RequestParam(required = false) String customerName,
            @RequestParam(required = false) String productKeyword,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTo) {
        return ResponseEntity.ok(reportService.getSalesReport(customerName, productKeyword, dateFrom, dateTo));
    }

    @Operation(summary = "Get Financial Summary Report (JSON)", description = "Returns high-level financial summary (Total Revenue, Purchase Cost, Gross Profit, Inventory Valuation).")
    @GetMapping("/financial")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<FinancialSummaryReportResponse> getFinancialSummaryReport() {
        return ResponseEntity.ok(reportService.getFinancialSummaryReport());
    }

    // ── Export Endpoints ──────────────────────────────────────────────────────

    @Operation(summary = "Export Inventory Report", description = "Downloads Inventory Report as pdf, xlsx, or csv. ADMIN only.")
    @GetMapping("/inventory/export")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<byte[]> exportInventoryReport(@RequestParam(defaultValue = "pdf") String format) {
        byte[] fileBytes = reportService.exportInventoryReport(format);
        return buildFileResponse(fileBytes, "inventory_report", format);
    }

    @Operation(summary = "Export Purchase Report", description = "Downloads Purchase Report as pdf, xlsx, or csv. ADMIN only.")
    @GetMapping("/purchases/export")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<byte[]> exportPurchaseReport(
            @RequestParam(required = false) Long supplierId,
            @RequestParam(required = false) Long warehouseId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTo,
            @RequestParam(defaultValue = "pdf") String format) {
        byte[] fileBytes = reportService.exportPurchaseReport(supplierId, warehouseId, dateFrom, dateTo, format);
        return buildFileResponse(fileBytes, "purchase_report", format);
    }

    @Operation(summary = "Export Sales Report", description = "Downloads Sales Report as pdf, xlsx, or csv. ADMIN only.")
    @GetMapping("/sales/export")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<byte[]> exportSalesReport(
            @RequestParam(required = false) String customerName,
            @RequestParam(required = false) String productKeyword,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTo,
            @RequestParam(defaultValue = "pdf") String format) {
        byte[] fileBytes = reportService.exportSalesReport(customerName, productKeyword, dateFrom, dateTo, format);
        return buildFileResponse(fileBytes, "sales_report", format);
    }

    @Operation(summary = "Export Financial Summary Report", description = "Downloads Financial Summary Report as pdf, xlsx, or csv. ADMIN only.")
    @GetMapping("/financial/export")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<byte[]> exportFinancialSummaryReport(@RequestParam(defaultValue = "pdf") String format) {
        byte[] fileBytes = reportService.exportFinancialSummaryReport(format);
        return buildFileResponse(fileBytes, "financial_summary_report", format);
    }

    // ── File Response Helper ──────────────────────────────────────────────────

    private ResponseEntity<byte[]> buildFileResponse(byte[] fileBytes, String baseFileName, String format) {
        String ext = format.toLowerCase();
        MediaType mediaType;
        String fileName;

        switch (ext) {
            case "pdf" -> {
                mediaType = MediaType.APPLICATION_PDF;
                fileName = baseFileName + ".pdf";
            }
            case "xlsx", "excel" -> {
                mediaType = MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                fileName = baseFileName + ".xlsx";
            }
            case "csv" -> {
                mediaType = MediaType.parseMediaType("text/csv");
                fileName = baseFileName + ".csv";
            }
            default -> throw new IllegalArgumentException("Unsupported format: " + format);
        }

        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .body(fileBytes);
    }
}
