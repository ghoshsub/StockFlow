import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import toast from 'react-hot-toast';
import { saleService } from '../services/saleService';

const STALE_TIME = 2 * 60 * 1000;

export function useSales(params = {}) {
  const { page = 0, size = 10, sortBy = 'saleDate', direction = 'DESC', searchParams = {} } = params;
  const hasSearch = Object.values(searchParams).some((v) => v !== undefined && v !== '');

  return useQuery({
    queryKey: ['sales', { page, size, sortBy, direction, searchParams }],
    queryFn: () => {
      if (hasSearch) {
        return saleService.search(searchParams, page, size, sortBy, direction);
      }
      return saleService.getPaginated(page, size, sortBy, direction);
    },
    staleTime: STALE_TIME,
    keepPreviousData: true,
  });
}

export function useSale(id) {
  return useQuery({
    queryKey: ['sales', id],
    queryFn: () => saleService.getById(id),
    enabled: !!id,
    staleTime: STALE_TIME,
  });
}

export function useCreateSale() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: saleService.create,
    onSuccess: () => {
      toast.success('Sale Order completed successfully!');
      // Invalidate relevant caches to trigger automatic UI updates
      queryClient.invalidateQueries({ queryKey: ['sales'] });
      queryClient.invalidateQueries({ queryKey: ['products'] });
      queryClient.invalidateQueries({ queryKey: ['inventory'] });
      queryClient.invalidateQueries({ queryKey: ['dashboard'] });
    },
    onError: (err) => {
      toast.error(err.userMessage || 'Failed to complete sales transaction.');
    },
  });
}
