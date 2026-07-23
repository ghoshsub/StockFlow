import { useEffect, useRef } from 'react';
import { Navigate, Outlet } from 'react-router-dom';
import toast from 'react-hot-toast';
import useAuthStore from '@/store/authStore';
import { ROUTES } from '@/constants/routes';

/**
 * ProtectedRoute — Route guard checking authentication and required roles.
 *
 * Behaviour:
 *  - Unauthenticated → redirect to /login
 *  - Authenticated but wrong role → toast warning + redirect to /dashboard
 *  - Authenticated + correct role → render children via <Outlet />
 *
 * @param {string[]} allowedRoles - Roles that may access this route group.
 *                                   If omitted, any authenticated user is allowed.
 */
export function ProtectedRoute({ allowedRoles }) {
  const { isAuthenticated, role } = useAuthStore();
  const hasToasted = useRef(false);

  const isDenied = isAuthenticated && allowedRoles && !allowedRoles.includes(role);

  // Fire the toast once per denied render (avoid duplicate toasts on re-renders)
  useEffect(() => {
    if (isDenied && !hasToasted.current) {
      hasToasted.current = true;
      toast.error(
        `Access Denied — Your ${role || 'STAFF'} account cannot access this section.`,
        { duration: 4000, icon: '🔒' }
      );
    }
  }, [isDenied, role]);

  if (!isAuthenticated) {
    return <Navigate to={ROUTES.LANDING} replace />;
  }

  if (isDenied) {
    return <Navigate to={ROUTES.ACCESS_DENIED} replace />;
  }

  return <Outlet />;
}
