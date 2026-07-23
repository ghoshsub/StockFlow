import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import toast from 'react-hot-toast';
import { purchaseService } from '../services/purchaseService';

const STALE_TIME = 2 * 60 * 1000;

export function usePurchases(params = {}) {
  const { page = 0, size = 10, sortBy = 'purchaseDate', direction = 'DESC', searchParams = {} } = params;
  const hasSearch = Object.values(searchParams).some((v) => v !== undefined && v !== '');

  return useQuery({
    queryKey: ['purchases', { page, size, sortBy, direction, searchParams }],
    queryFn: () => {
      if (hasSearch) {
        return purchaseService.search(searchParams, page, size, sortBy, direction);
      }
      return purchaseService.getPaginated(page, size, sortBy, direction);
    },
    staleTime: STALE_TIME,
    keepPreviousData: true,
  });
}

export function usePurchase(id) {
  return useQuery({
    queryKey: ['purchases', id],
    queryFn: () => purchaseService.getById(id),
    enabled: !!id,
    staleTime: STALE_TIME,
  });
}

export function useCreatePurchase() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: purchaseService.create,
    onSuccess: () => {
      toast.success('Purchase Order created successfully!');
      // Invalidate relevant caches to trigger automatic UI updates
      queryClient.invalidateQueries({ queryKey: ['purchases'] });
      queryClient.invalidateQueries({ queryKey: ['products'] });
      queryClient.invalidateQueries({ queryKey: ['inventory'] });
      queryClient.invalidateQueries({ queryKey: ['dashboard'] });
    },
    onError: (err) => {
      toast.error(err.userMessage || 'Failed to create purchase order.');
    },
  });
}
