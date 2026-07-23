package com.stockflow.backend.service;

import com.stockflow.backend.dto.dashboard.*;

import java.util.List;

/**
 * DashboardService — Core interface for executive KPI metrics and visual chart analytics.
 */
public interface DashboardService {

    /** Executive summary KPIs */
    DashboardSummaryResponse getSummary();

    /** Detailed Inventory Stock & Valuation Analytics */
    InventoryAnalyticsResponse getInventoryAnalytics();

    /** Detailed Sales Revenue & Periodic Performance Analytics */
    SalesAnalyticsResponse getSalesAnalytics();

    /** Detailed Purchase Procurement Analytics */
    PurchaseAnalyticsResponse getPurchaseAnalytics();

    /** Monthly Revenue trend data for React charts */
    List<ChartDataResponse> getMonthlyRevenueChart();

    /** Monthly Purchase trend data for React charts */
    List<ChartDataResponse> getMonthlyPurchasesChart();

    /** Category inventory distribution data for React charts */
    List<ChartDataResponse> getCategoryDistributionChart();

    /** Warehouse inventory distribution data for React charts */
    List<ChartDataResponse> getWarehouseDistributionChart();
}
