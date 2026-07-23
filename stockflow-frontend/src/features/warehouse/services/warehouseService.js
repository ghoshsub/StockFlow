import api from '@/services/api';
import { API } from '@/constants/api';

/**
 * warehouseService
 * GET /api/warehouses/page    → paginated (params: page, size, sort, direction)
 * GET /api/warehouses/search  → search (params: keyword, page, size, sort, direction)
 * GET /api/warehouses         → full list (all active)
 */
export const warehouseService = {
  getPaginated: async (page = 0, size = 10, sortBy = 'name', direction = 'ASC') => {
    const res = await api.get(`${API.WAREHOUSES}/page`, {
      params: { page, size, sort: sortBy, direction },
    });
    return res.data;
  },

  getAllList: async () => {
    const res = await api.get(API.WAREHOUSES);
    return res.data;
  },

  search: async (keyword, page = 0, size = 10, sortBy = 'name', direction = 'ASC') => {
    const res = await api.get(`${API.WAREHOUSES}/search`, {
      params: { keyword, page, size, sort: sortBy, direction },
    });
    return res.data;
  },

  getById: async (id) => {
    const res = await api.get(`${API.WAREHOUSES}/${id}`);
    return res.data;
  },

  create: async (data) => {
    const res = await api.post(API.WAREHOUSES, data);
    return res.data;
  },

  update: async (id, data) => {
    const res = await api.put(`${API.WAREHOUSES}/${id}`, data);
    return res.data;
  },

  delete: async (id) => {
    const res = await api.delete(`${API.WAREHOUSES}/${id}`);
    return res.data;
  },
};
