package com.stockflow.backend.controller;

import com.stockflow.backend.dto.dashboard.*;
import com.stockflow.backend.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * DashboardController — REST controller for Dashboard & Analytics.
 */
@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "Executive Dashboard & Analytics — Executive summary, stock analytics, sales performance, procurement trends, and chart structures")
@SecurityRequirement(name = "Bearer Authentication")
public class DashboardController {

    private final DashboardService dashboardService;

    @Operation(summary = "Get Executive Dashboard Summary", description = "Returns high-level KPI counts and monetary totals (products, sales, revenue, cost, inventory value). Accessible by ADMIN and STAFF.")
    @GetMapping("/summary")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<DashboardSummaryResponse> getSummary() {
        return ResponseEntity.ok(dashboardService.getSummary());
    }

    @Operation(summary = "Get Inventory Analytics", description = "Returns low stock, out of stock, top 10 products, and category/warehouse valuations.")
    @GetMapping("/inventory")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<InventoryAnalyticsResponse> getInventoryAnalytics() {
        return ResponseEntity.ok(dashboardService.getInventoryAnalytics());
    }

    @Operation(summary = "Get Sales Analytics", description = "Returns today's, weekly, monthly, yearly sales, top products, and top customers.")
    @GetMapping("/sales")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<SalesAnalyticsResponse> getSalesAnalytics() {
        return ResponseEntity.ok(dashboardService.getSalesAnalytics());
    }

    @Operation(summary = "Get Purchase Analytics", description = "Returns today's & monthly purchase totals, top suppliers, and monthly purchase trends.")
    @GetMapping("/purchases")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<PurchaseAnalyticsResponse> getPurchaseAnalytics() {
        return ResponseEntity.ok(dashboardService.getPurchaseAnalytics());
    }

    @Operation(summary = "Get Monthly Revenue Chart Data", description = "Returns label-value JSON structure for monthly revenue charts.")
    @GetMapping("/charts/revenue-monthly")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<List<ChartDataResponse>> getMonthlyRevenueChart() {
        return ResponseEntity.ok(dashboardService.getMonthlyRevenueChart());
    }

    @Operation(summary = "Get Monthly Purchases Chart Data", description = "Returns label-value JSON structure for monthly purchase charts.")
    @GetMapping("/charts/purchases-monthly")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<List<ChartDataResponse>> getMonthlyPurchasesChart() {
        return ResponseEntity.ok(dashboardService.getMonthlyPurchasesChart());
    }

    @Operation(summary = "Get Category Distribution Chart Data", description = "Returns label-value JSON structure for category valuation distribution.")
    @GetMapping("/charts/category-distribution")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<List<ChartDataResponse>> getCategoryDistributionChart() {
        return ResponseEntity.ok(dashboardService.getCategoryDistributionChart());
    }

    @Operation(summary = "Get Warehouse Distribution Chart Data", description = "Returns label-value JSON structure for warehouse valuation distribution.")
    @GetMapping("/charts/warehouse-distribution")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<List<ChartDataResponse>> getWarehouseDistributionChart() {
        return ResponseEntity.ok(dashboardService.getWarehouseDistributionChart());
    }
}
