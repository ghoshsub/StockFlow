import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import toast from 'react-hot-toast';
import { productService } from '../services/productService';

const STALE_TIME = 2 * 60 * 1000;

export function useProducts(params = {}) {
  const { page = 0, size = 10, sortBy = 'name', direction = 'ASC', keyword = '', filters = {} } = params;

  const hasFilters = Boolean(
    filters.categoryId ||
    filters.brandId ||
    filters.supplierId ||
    filters.warehouseId ||
    filters.lowStockOnly ||
    (filters.active !== undefined && filters.active !== '')
  );

  return useQuery({
    queryKey: ['products', { page, size, sortBy, direction, keyword, filters }],
    queryFn: () => {
      if (keyword && keyword.trim() !== '') {
        return productService.search(keyword, page, size, sortBy, direction);
      }
      if (filters.lowStockOnly) {
        return productService.getLowStock(page, size, sortBy, direction);
      }
      if (hasFilters) {
        return productService.filter(filters, page, size, sortBy, direction);
      }
      return productService.getPaginated(page, size, sortBy, direction);
    },
    staleTime: STALE_TIME,
    keepPreviousData: true,
  });
}

export function useProduct(id) {
  return useQuery({
    queryKey: ['products', id],
    queryFn: () => productService.getById(id),
    enabled: !!id,
    staleTime: STALE_TIME,
  });
}

export function useCreateProduct() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: productService.create,
    onSuccess: () => {
      toast.success('Product created successfully!');
      queryClient.invalidateQueries({ queryKey: ['products'] });
      queryClient.invalidateQueries({ queryKey: ['dashboard'] });
    },
    onError: (err) => {
      toast.error(err.userMessage || 'Failed to create product.');
    },
  });
}

export function useUpdateProduct() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ id, data }) => productService.update(id, data),
    onSuccess: (_, variables) => {
      toast.success('Product updated successfully!');
      queryClient.invalidateQueries({ queryKey: ['products'] });
      queryClient.invalidateQueries({ queryKey: ['products', variables.id] });
      queryClient.invalidateQueries({ queryKey: ['dashboard'] });
    },
    onError: (err) => {
      toast.error(err.userMessage || 'Failed to update product.');
    },
  });
}

export function useDeleteProduct() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: productService.delete,
    onSuccess: () => {
      toast.success('Product soft-deleted successfully!');
      queryClient.invalidateQueries({ queryKey: ['products'] });
      queryClient.invalidateQueries({ queryKey: ['dashboard'] });
    },
    onError: (err) => {
      toast.error(err.userMessage || 'Failed to delete product.');
    },
  });
}
