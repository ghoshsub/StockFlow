import api from '@/services/api';
import { API } from '@/constants/api';

/**
 * supplierService
 * GET /api/suppliers/page    → paginated (params: page, size, sort, direction)
 * GET /api/suppliers/search  → search (params: keyword, page, size, sort, direction)
 * GET /api/suppliers         → full list
 */
export const supplierService = {
  getPaginated: async (page = 0, size = 10, sortBy = 'name', direction = 'ASC') => {
    const res = await api.get(`${API.SUPPLIERS}/page`, {
      params: { page, size, sort: sortBy, direction },
    });
    return res.data;
  },

  getAllList: async () => {
    const res = await api.get(API.SUPPLIERS);
    return res.data;
  },

  search: async (keyword, page = 0, size = 10, sortBy = 'name', direction = 'ASC') => {
    const res = await api.get(`${API.SUPPLIERS}/search`, {
      params: { keyword, page, size, sort: sortBy, direction },
    });
    return res.data;
  },

  create: async (data) => {
    const res = await api.post(API.SUPPLIERS, data);
    return res.data;
  },

  update: async (id, data) => {
    const res = await api.put(`${API.SUPPLIERS}/${id}`, data);
    return res.data;
  },

  delete: async (id) => {
    const res = await api.delete(`${API.SUPPLIERS}/${id}`);
    return res.data;
  },
};
