import api from '@/services/api';
import { API } from '@/constants/api';

/**
 * brandService
 * GET /api/brands/page    → paginated (params: page, size, sort, direction)
 * GET /api/brands/search  → search (params: keyword, page, size, sort, direction)
 * GET /api/brands         → full list (for dropdowns)
 *
 * NOTE: Brand/Supplier/Warehouse use 'sort' (not 'sortBy') and '/page' subpath.
 */
export const brandService = {
  getPaginated: async (page = 0, size = 10, sortBy = 'name', direction = 'ASC') => {
    const res = await api.get(`${API.BRANDS}/page`, {
      params: { page, size, sort: sortBy, direction },
    });
    return res.data;
  },

  getAllList: async () => {
    const res = await api.get(API.BRANDS);
    return res.data;
  },

  search: async (keyword, page = 0, size = 10, sortBy = 'name', direction = 'ASC') => {
    const res = await api.get(`${API.BRANDS}/search`, {
      params: { keyword, page, size, sort: sortBy, direction },
    });
    return res.data;
  },

  create: async (data) => {
    const res = await api.post(API.BRANDS, data);
    return res.data;
  },

  update: async (id, data) => {
    const res = await api.put(`${API.BRANDS}/${id}`, data);
    return res.data;
  },

  delete: async (id) => {
    const res = await api.delete(`${API.BRANDS}/${id}`);
    return res.data;
  },
};
