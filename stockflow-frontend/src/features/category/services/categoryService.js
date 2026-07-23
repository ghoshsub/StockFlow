import api from '@/services/api';
import { API } from '@/constants/api';

/**
 * categoryService
 * GET /api/categories          → paginated (params: page, size, sortBy, direction)
 * GET /api/categories/search   → search (params: keyword, page, size, sortBy, direction)
 * GET /api/categories/all      → full list (for dropdowns)
 */
export const categoryService = {
  getPaginated: async (page = 0, size = 10, sortBy = 'name', direction = 'ASC') => {
    const res = await api.get(API.CATEGORIES, {
      params: { page, size, sortBy, direction },
    });
    return res.data;
  },

  getAllList: async () => {
    const res = await api.get(`${API.CATEGORIES}/all`);
    return res.data;
  },

  search: async (keyword, page = 0, size = 10, sortBy = 'name', direction = 'ASC') => {
    const res = await api.get(`${API.CATEGORIES}/search`, {
      params: { keyword, page, size, sortBy, direction },
    });
    return res.data;
  },

  getById: async (id) => {
    const res = await api.get(`${API.CATEGORIES}/${id}`);
    return res.data;
  },

  create: async (data) => {
    const res = await api.post(API.CATEGORIES, data);
    return res.data;
  },

  update: async (id, data) => {
    const res = await api.put(`${API.CATEGORIES}/${id}`, data);
    return res.data;
  },

  delete: async (id) => {
    const res = await api.delete(`${API.CATEGORIES}/${id}`);
    return res.data;
  },
};
