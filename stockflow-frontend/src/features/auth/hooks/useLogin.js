import { useMutation } from '@tanstack/react-query';
import { useNavigate } from 'react-router-dom';
import toast from 'react-hot-toast';
import { authService } from '../services/authService';
import useAuthStore from '@/store/authStore';
import { ROUTES } from '@/constants/routes';

/**
 * useLogin — TanStack mutation hook for authentication.
 * After successful login, redirects to /dashboard (both ADMIN and STAFF).
 */
export function useLogin() {
  const navigate = useNavigate();
  const loginToStore = useAuthStore((state) => state.login);

  return useMutation({
    mutationFn: authService.login,
    onSuccess: (data) => {
      loginToStore(data);
      const roleName = data.role?.toUpperCase() || 'USER';
      toast.success(`Welcome back, ${data.username || 'User'}! Signed in as ${roleName}.`);
      // Both ADMIN and STAFF land on /dashboard; sidebar shows only role-appropriate items
      navigate(ROUTES.DASHBOARD, { replace: true });
    },
    onError: (error) => {
      toast.error(error.userMessage || 'Login failed. Please check your credentials.');
    },
  });
}
