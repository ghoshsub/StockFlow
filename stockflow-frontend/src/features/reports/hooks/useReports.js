import { useQuery, useMutation } from '@tanstack/react-query';
import toast from 'react-hot-toast';
import { reportService } from '../services/reportService';

const STALE_TIME = 3 * 60 * 1000;

export function useInventoryReport() {
  return useQuery({
    queryKey: ['reports', 'inventory'],
    queryFn: reportService.getInventoryReport,
    staleTime: STALE_TIME,
  });
}

export function usePurchaseReport(params = {}) {
  return useQuery({
    queryKey: ['reports', 'purchases', params],
    queryFn: () => reportService.getPurchaseReport(params),
    staleTime: STALE_TIME,
  });
}

export function useSalesReport(params = {}) {
  return useQuery({
    queryKey: ['reports', 'sales', params],
    queryFn: () => reportService.getSalesReport(params),
    staleTime: STALE_TIME,
  });
}

export function useFinancialReport() {
  return useQuery({
    queryKey: ['reports', 'financial'],
    queryFn: reportService.getFinancialReport,
    staleTime: STALE_TIME,
  });
}

export function useExportReport() {
  return useMutation({
    mutationFn: ({ endpoint, params, filename }) =>
      reportService.exportReport(endpoint, params, filename),
    onSuccess: (_, variables) => {
      toast.success(`Export downloaded: ${variables.filename}.${variables.params?.format || 'pdf'}`);
    },
    onError: (err) => {
      toast.error(err.userMessage || 'Failed to download report export.');
    },
  });
}
