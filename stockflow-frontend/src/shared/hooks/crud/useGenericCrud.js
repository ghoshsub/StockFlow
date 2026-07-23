import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import toast from 'react-hot-toast';

/**
 * useGenericCrud — Generic React Query hook for CRUD operations.
 *
 * @param {string} queryKey      - Unique cache key (e.g. 'categories')
 * @param {object} service       - Service object with getPaginated, search, create, update, delete
 * @param {number} page          - Current page (0-indexed)
 * @param {number} size          - Page size
 * @param {string} sortBy        - Sort field name (frontend state)
 * @param {string} direction     - 'ASC' | 'DESC'
 * @param {string} keyword       - Search keyword (empty = use getPaginated)
 */
export function useGenericCrud({
  queryKey,
  service,
  page = 0,
  size = 10,
  sortBy = 'name',
  direction = 'ASC',
  keyword = '',
}) {
  const queryClient = useQueryClient();

  // ── Paginated / Searched Query ─────────────────────────────────────────────
  const query = useQuery({
    queryKey: [queryKey, { page, size, sortBy, direction, keyword }],
    queryFn: () => {
      if (keyword && keyword.trim() !== '') {
        return service.search(keyword, page, size, sortBy, direction);
      }
      return service.getPaginated(page, size, sortBy, direction);
    },
    staleTime: 2 * 60 * 1000, // 2 min
    keepPreviousData: true,   // Prevents flash when paginating
  });

  // ── Create ─────────────────────────────────────────────────────────────────
  const createMutation = useMutation({
    mutationFn: service.create,
    onSuccess: () => {
      toast.success('Created successfully!');
      queryClient.invalidateQueries({ queryKey: [queryKey] });
    },
    onError: (err) => {
      toast.error(err.userMessage || 'Failed to create. Please try again.');
    },
  });

  // ── Update ─────────────────────────────────────────────────────────────────
  const updateMutation = useMutation({
    mutationFn: ({ id, data }) => service.update(id, data),
    onSuccess: () => {
      toast.success('Updated successfully!');
      queryClient.invalidateQueries({ queryKey: [queryKey] });
    },
    onError: (err) => {
      toast.error(err.userMessage || 'Failed to update. Please try again.');
    },
  });

  // ── Delete ─────────────────────────────────────────────────────────────────
  const deleteMutation = useMutation({
    mutationFn: service.delete,
    onSuccess: () => {
      toast.success('Deleted successfully!');
      queryClient.invalidateQueries({ queryKey: [queryKey] });
    },
    onError: (err) => {
      toast.error(err.userMessage || 'Failed to delete. Please try again.');
    },
  });

  return {
    // query state
    ...query,
    // create
    createItem: createMutation.mutateAsync,
    isCreating: createMutation.isPending,
    // update
    updateItem: updateMutation.mutateAsync,
    isUpdating: updateMutation.isPending,
    // delete
    deleteItem: deleteMutation.mutateAsync,
    isDeleting: deleteMutation.isPending,
  };
}
