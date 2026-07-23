import { useQuery } from '@tanstack/react-query';
import { dashboardService } from '../services/dashboardService';

const STALE_TIME = 5 * 60 * 1000; // 5 minutes

export function useDashboardSummary() {
  return useQuery({
    queryKey: ['dashboard', 'summary'],
    queryFn: dashboardService.getSummary,
    staleTime: STALE_TIME,
    retry: 1,
    refetchOnWindowFocus: false,
  });
}

export function useInventoryAnalytics() {
  return useQuery({
    queryKey: ['dashboard', 'inventory'],
    queryFn: dashboardService.getInventoryAnalytics,
    staleTime: STALE_TIME,
    retry: 1,
    refetchOnWindowFocus: false,
  });
}

export function useSalesAnalytics() {
  return useQuery({
    queryKey: ['dashboard', 'sales'],
    queryFn: dashboardService.getSalesAnalytics,
    staleTime: STALE_TIME,
    retry: 1,
    refetchOnWindowFocus: false,
  });
}

export function usePurchaseAnalytics() {
  return useQuery({
    queryKey: ['dashboard', 'purchases'],
    queryFn: dashboardService.getPurchaseAnalytics,
    staleTime: STALE_TIME,
    retry: 1,
    refetchOnWindowFocus: false,
  });
}

export function useRevenueChart() {
  return useQuery({
    queryKey: ['dashboard', 'charts', 'revenue'],
    queryFn: dashboardService.getRevenueChart,
    staleTime: STALE_TIME,
    retry: 1,
    refetchOnWindowFocus: false,
  });
}

export function usePurchasesChart() {
  return useQuery({
    queryKey: ['dashboard', 'charts', 'purchases'],
    queryFn: dashboardService.getPurchasesChart,
    staleTime: STALE_TIME,
    retry: 1,
    refetchOnWindowFocus: false,
  });
}

export function useCategoryDistribution() {
  return useQuery({
    queryKey: ['dashboard', 'charts', 'category'],
    queryFn: dashboardService.getCategoryDistribution,
    staleTime: STALE_TIME,
    retry: 1,
    refetchOnWindowFocus: false,
  });
}

export function useWarehouseDistribution() {
  return useQuery({
    queryKey: ['dashboard', 'charts', 'warehouse'],
    queryFn: dashboardService.getWarehouseDistribution,
    staleTime: STALE_TIME,
    retry: 1,
    refetchOnWindowFocus: false,
  });
}
