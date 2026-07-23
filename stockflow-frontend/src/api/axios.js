import axios from 'axios';
import { tokenService } from '../services/tokenService';

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api',
  timeout: 15000,
  headers: {
    'Content-Type': 'application/json',
    Accept: 'application/json',
  },
});

// Request Interceptor
api.interceptors.request.use(
  (config) => {
    const token = tokenService.getToken() || localStorage.getItem('token') || localStorage.getItem('sf_token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response Interceptor for handling global errors
api.interceptors.response.use(
  (response) => response,
  (error) => {
    const message =
      error?.response?.data?.message ||
      error?.response?.data?.error ||
      error?.message ||
      'An unexpected error occurred';

    if (error.response) {
      if (error.response.status === 401) {
        console.warn('Unauthorized access. Token may be expired.');
        tokenService.clear();
        localStorage.removeItem('token');
        if (!window.location.pathname.includes('/login') && window.location.pathname !== '/') {
          window.location.href = '/';
        }
      }
    }
    error.userMessage = message;
    return Promise.reject(error);
  }
);

export default api;
