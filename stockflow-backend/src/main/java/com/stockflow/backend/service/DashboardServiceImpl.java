package com.stockflow.backend.service;

import com.stockflow.backend.dto.dashboard.*;
import com.stockflow.backend.mapper.ProductMapper;
import com.stockflow.backend.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

/**
 * DashboardServiceImpl — Implementation of DashboardService.
 * Employs direct database aggregate queries (COUNT, SUM, GROUP BY) to deliver lightning-fast responses without entity overhead.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final SupplierRepository supplierRepository;
    private final WarehouseRepository warehouseRepository;
    private final PurchaseRepository purchaseRepository;
    private final SaleRepository saleRepository;
    private final ProductMapper productMapper;

    @Override
    public DashboardSummaryResponse getSummary() {
        log.debug("Fetching executive dashboard summary KPIs");

        return DashboardSummaryResponse.builder()
                .totalProducts(productRepository.countByActiveTrue())
                .totalCategories(categoryRepository.count())
                .totalBrands(brandRepository.count())
                .totalSuppliers(supplierRepository.count())
                .totalWarehouses(warehouseRepository.count())
                .totalPurchases(purchaseRepository.count())
                .totalSales(saleRepository.count())
                .totalRevenue(saleRepository.calculateTotalRevenue())
                .totalPurchaseCost(purchaseRepository.calculateTotalPurchaseCost())
                .currentInventoryValue(productRepository.calculateTotalInventoryValue())
                .build();
    }

    @Override
    public InventoryAnalyticsResponse getInventoryAnalytics() {
        log.debug("Fetching inventory analytics");

        List<ChartDataResponse> categoryBreakdown = mapChartData(productRepository.getInventoryValueByCategory());
        List<ChartDataResponse> warehouseBreakdown = mapChartData(productRepository.getInventoryValueByWarehouse());

        return InventoryAnalyticsResponse.builder()
                .lowStockCount(productRepository.countLowStockProducts())
                .outOfStockCount(productRepository.countOutOfStockProducts())
                .lowStockProducts(productMapper.toResponseList(productRepository.findLowStockProductsList()))
                .outOfStockProducts(productMapper.toResponseList(productRepository.findOutOfStockProductsList()))
                .top10HighestStockProducts(productMapper.toResponseList(productRepository.findTop10ByActiveTrueOrderByQuantityDesc()))
                .top10LowestStockProducts(productMapper.toResponseList(productRepository.findTop10ByActiveTrueOrderByQuantityAsc()))
                .inventoryValueByCategory(categoryBreakdown)
                .inventoryValueByWarehouse(warehouseBreakdown)
                .build();
    }

    @Override
    public SalesAnalyticsResponse getSalesAnalytics() {
        log.debug("Fetching sales analytics");

        LocalDate today = LocalDate.now();
        LocalDateTime startOfToday = today.atStartOfDay();
        LocalDateTime endOfToday = today.atTime(LocalTime.MAX);

        LocalDateTime startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).atStartOfDay();
        LocalDateTime startOfMonth = today.with(TemporalAdjusters.firstDayOfMonth()).atStartOfDay();
        LocalDateTime startOfYear = today.with(TemporalAdjusters.firstDayOfYear()).atStartOfDay();

        BigDecimal todaysSales = saleRepository.calculateSalesBetween(startOfToday, endOfToday);
        BigDecimal thisWeeksSales = saleRepository.calculateSalesBetween(startOfWeek, endOfToday);
        BigDecimal thisMonthsSales = saleRepository.calculateSalesBetween(startOfMonth, endOfToday);
        BigDecimal yearlySales = saleRepository.calculateSalesBetween(startOfYear, endOfToday);

        List<ChartDataResponse> salesByMonth = mapMonthlyTrend(saleRepository.getMonthlySalesTrend());
        List<ChartDataResponse> topSellingProducts = mapChartData(saleRepository.getTopSellingProducts());
        List<ChartDataResponse> topCustomers = mapChartData(saleRepository.getTopCustomers());

        return SalesAnalyticsResponse.builder()
                .todaysSales(todaysSales)
                .thisWeeksSales(thisWeeksSales)
                .thisMonthsSales(thisMonthsSales)
                .yearlySales(yearlySales)
                .salesByMonth(salesByMonth)
                .topSellingProducts(topSellingProducts)
                .topCustomers(topCustomers)
                .build();
    }

    @Override
    public PurchaseAnalyticsResponse getPurchaseAnalytics() {
        log.debug("Fetching purchase analytics");

        LocalDate today = LocalDate.now();
        LocalDateTime startOfToday = today.atStartOfDay();
        LocalDateTime endOfToday = today.atTime(LocalTime.MAX);
        LocalDateTime startOfMonth = today.with(TemporalAdjusters.firstDayOfMonth()).atStartOfDay();

        BigDecimal todaysPurchases = purchaseRepository.calculatePurchasesBetween(startOfToday, endOfToday);
        BigDecimal monthlyPurchases = purchaseRepository.calculatePurchasesBetween(startOfMonth, endOfToday);

        List<ChartDataResponse> topSuppliers = mapChartData(purchaseRepository.getTopSuppliers());
        List<ChartDataResponse> purchaseTrend = mapMonthlyTrend(purchaseRepository.getMonthlyPurchaseTrend());

        return PurchaseAnalyticsResponse.builder()
                .todaysPurchases(todaysPurchases)
                .monthlyPurchases(monthlyPurchases)
                .topSuppliers(topSuppliers)
                .purchaseTrend(purchaseTrend)
                .build();
    }

    @Override
    public List<ChartDataResponse> getMonthlyRevenueChart() {
        return mapMonthlyTrend(saleRepository.getMonthlySalesTrend());
    }

    @Override
    public List<ChartDataResponse> getMonthlyPurchasesChart() {
        return mapMonthlyTrend(purchaseRepository.getMonthlyPurchaseTrend());
    }

    @Override
    public List<ChartDataResponse> getCategoryDistributionChart() {
        return mapChartData(productRepository.getInventoryValueByCategory());
    }

    @Override
    public List<ChartDataResponse> getWarehouseDistributionChart() {
        return mapChartData(productRepository.getInventoryValueByWarehouse());
    }

    // ── Private Helper Mappers for Object[] results ───────────────────────────

    private List<ChartDataResponse> mapChartData(List<Object[]> queryResults) {
        List<ChartDataResponse> chartData = new ArrayList<>();
        if (queryResults == null) return chartData;

        for (Object[] row : queryResults) {
            String label = row[0] != null ? row[0].toString() : "Unknown";
            BigDecimal value = row[1] != null ? new BigDecimal(row[1].toString()) : BigDecimal.ZERO;
            BigDecimal secondaryValue = (row.length > 2 && row[2] != null) ? new BigDecimal(row[2].toString()) : BigDecimal.ZERO;

            chartData.add(ChartDataResponse.builder()
                    .label(label)
                    .value(value)
                    .secondaryValue(secondaryValue)
                    .build());
        }
        return chartData;
    }

    private List<ChartDataResponse> mapMonthlyTrend(List<Object[]> queryResults) {
        List<ChartDataResponse> chartData = new ArrayList<>();
        if (queryResults == null) return chartData;

        for (Object[] row : queryResults) {
            String year = row[0] != null ? row[0].toString() : "";
            String month = row[1] != null ? String.format("%02d", Integer.parseInt(row[1].toString())) : "";
            String label = year + "-" + month;

            BigDecimal value = row[2] != null ? new BigDecimal(row[2].toString()) : BigDecimal.ZERO;
            BigDecimal secondaryValue = (row.length > 3 && row[3] != null) ? new BigDecimal(row[3].toString()) : BigDecimal.ZERO;

            chartData.add(ChartDataResponse.builder()
                    .label(label)
                    .value(value)
                    .secondaryValue(secondaryValue)
                    .build());
        }
        return chartData;
    }
}
