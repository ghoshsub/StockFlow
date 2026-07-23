import axios from 'axios';
import { tokenService } from './tokenService';

/**
 * api.js — Central Axios instance for the StockFlow backend.
 *
 * - Base URL from environment variable (never hardcoded)
 * - Request interceptor: attaches JWT Bearer token
 * - Response interceptor: handles 401 auto-logout + extracts error message
 */
const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  headers: { 'Content-Type': 'application/json' },
  timeout: 15000,
});

// ── Request Interceptor ────────────────────────────────────────────────────
api.interceptors.request.use(
  (config) => {
    const token = tokenService.getToken();
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// ── Response Interceptor ──────────────────────────────────────────────────
api.interceptors.response.use(
  (response) => response,
  (error) => {
    // Extract user-friendly message from backend error envelope:
    // { timestamp, status, error, message } from GlobalExceptionHandler
    const message =
      error?.response?.data?.message ||
      error?.response?.data?.error ||
      error?.message ||
      'An unexpected error occurred';

    // Auto-logout on 401 Unauthorized
    if (error?.response?.status === 401) {
      tokenService.clear();
      // Redirect to landing page (portal selection), not directly to /login
      if (!window.location.pathname.includes('/login') && window.location.pathname !== '/') {
        window.location.href = '/';
      }
    }

    // Attach extracted message to the error for React Query / catch blocks
    error.userMessage = message;
    return Promise.reject(error);
  }
);

export default api;
