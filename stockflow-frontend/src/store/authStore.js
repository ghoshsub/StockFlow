import { create } from 'zustand';
import { tokenService } from '@/services/tokenService';

/**
 * authStore — Zustand store for authentication state.
 *
 * Initialized from tokenService on app load so the user stays
 * logged in across page refreshes.
 */
const useAuthStore = create((set) => ({
  // ── State ────────────────────────────────────────────────────────────────
  user: tokenService.getUser(),
  token: tokenService.getToken(),
  role: tokenService.getUser()?.role ?? null,
  isAuthenticated: !!tokenService.getToken(),

  // ── Actions ──────────────────────────────────────────────────────────────

  /**
   * Called after successful login.
   * Persists token + user via tokenService, then updates Zustand state.
   * @param {object} loginResponse — backend LoginResponse DTO
   */
  login: (loginResponse) => {
    const { token, ...user } = loginResponse;
    tokenService.setToken(token);
    tokenService.setUser(user);
    set({
      token,
      user,
      role: user.role,
      isAuthenticated: true,
    });
  },

  /**
   * Called on logout or 401 auto-logout.
   * Clears tokenService and resets Zustand state.
   */
  logout: () => {
    tokenService.clear();
    // Also clear any legacy token keys
    localStorage.removeItem('token');
    localStorage.removeItem('sf_token');
    localStorage.removeItem('sf_user');
    set({
      token: null,
      user: null,
      role: null,
      isAuthenticated: false,
    });
  },
}));

export default useAuthStore;
