import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import toast from 'react-hot-toast';
import { inventoryService } from '../services/inventoryService';

const STALE_TIME = 2 * 60 * 1000;

export function useInventory(productId) {
  return useQuery({
    queryKey: ['inventory', 'product', productId],
    queryFn: () => inventoryService.getProductInventory(productId),
    enabled: !!productId,
    staleTime: STALE_TIME,
  });
}

export function useStockMovements(filters = {}) {
  const { productId, movementType, dateFrom, dateTo, username, page = 0, size = 10, sortBy = 'movementDate', direction = 'DESC' } = filters;
  return useQuery({
    queryKey: ['inventory', 'movements', { productId, movementType, dateFrom, dateTo, username, page, size, sortBy, direction }],
    queryFn: () => inventoryService.searchMovements(filters, page, size, sortBy, direction),
    staleTime: STALE_TIME,
    keepPreviousData: true,
  });
}

export function useStockIn() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: inventoryService.stockIn,
    onSuccess: () => {
      toast.success('Stock added successfully!');
      queryClient.invalidateQueries({ queryKey: ['products'] });
      queryClient.invalidateQueries({ queryKey: ['inventory'] });
      queryClient.invalidateQueries({ queryKey: ['dashboard'] });
    },
    onError: (err) => {
      toast.error(err.userMessage || 'Failed to add stock.');
    },
  });
}

export function useStockOut() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: inventoryService.stockOut,
    onSuccess: () => {
      toast.success('Stock removed successfully!');
      queryClient.invalidateQueries({ queryKey: ['products'] });
      queryClient.invalidateQueries({ queryKey: ['inventory'] });
      queryClient.invalidateQueries({ queryKey: ['dashboard'] });
    },
    onError: (err) => {
      toast.error(err.userMessage || 'Failed to remove stock.');
    },
  });
}

export function useStockAdjust() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: inventoryService.adjustStock,
    onSuccess: () => {
      toast.success('Stock quantity adjusted!');
      queryClient.invalidateQueries({ queryKey: ['products'] });
      queryClient.invalidateQueries({ queryKey: ['inventory'] });
      queryClient.invalidateQueries({ queryKey: ['dashboard'] });
    },
    onError: (err) => {
      toast.error(err.userMessage || 'Failed to adjust stock.');
    },
  });
}
