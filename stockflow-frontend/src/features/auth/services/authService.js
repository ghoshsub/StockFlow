import api from '@/services/api';
import { API } from '@/constants/api';

/**
 * authService — Service functions for authentication endpoints.
 */
export const authService = {
  login: async (credentials) => {
    const response = await api.post(API.AUTH.LOGIN, credentials);
    return response.data;
  },

  getCurrentUser: async () => {
    const response = await api.get(API.AUTH.ME);
    return response.data;
  },
};
