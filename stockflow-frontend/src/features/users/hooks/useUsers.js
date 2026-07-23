import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { userService } from '../services/userService';
import toast from 'react-hot-toast';

export function useUsers(tableState) {
  const { page, size, sortBy, direction, keyword } = tableState;

  return useQuery({
    queryKey: ['users', { page, size, sortBy, direction, keyword }],
    queryFn: () => {
      if (keyword) {
        return userService.search(keyword, { page, size, sortBy, direction });
      }
      return userService.getAllPaginated({ page, size, sortBy, direction });
    },
    keepPreviousData: true,
  });
}

export function useCreateUser() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (userData) => userService.create(userData),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['users'] });
      toast.success('User created successfully!');
    },
    onError: (err) => {
      toast.error(err.response?.data?.message || 'Failed to create user');
    },
  });
}

export function useUpdateUser() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ id, data }) => userService.update(id, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['users'] });
      toast.success('User updated successfully!');
    },
    onError: (err) => {
      toast.error(err.response?.data?.message || 'Failed to update user');
    },
  });
}

export function useDeleteUser() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (id) => userService.delete(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['users'] });
      toast.success('User deactivated successfully!');
    },
    onError: (err) => {
      toast.error(err.response?.data?.message || 'Failed to deactivate user');
    },
  });
}
