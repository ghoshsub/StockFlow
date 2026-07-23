import api from '@/services/api';
import { API } from '@/constants/api';

/**
 * productService — API service for Product module.
 * Maps exact backend parameters (sort, direction, page, size).
 */
export const productService = {
  getPaginated: async (page = 0, size = 10, sortBy = 'name', direction = 'ASC') => {
    const res = await api.get(API.PRODUCTS, {
      params: { page, size, sort: sortBy, direction },
    });
    return res.data;
  },

  getAllList: async () => {
    const res = await api.get(`${API.PRODUCTS}/all`);
    return res.data;
  },

  getById: async (id) => {
    const res = await api.get(`${API.PRODUCTS}/${id}`);
    return res.data;
  },

  search: async (keyword, page = 0, size = 10, sortBy = 'name', direction = 'ASC') => {
    const res = await api.get(`${API.PRODUCTS}/search`, {
      params: { keyword, page, size, sort: sortBy, direction },
    });
    return res.data;
  },

  filter: async (filters = {}, page = 0, size = 10, sortBy = 'name', direction = 'ASC') => {
    const res = await api.get(`${API.PRODUCTS}/filter`, {
      params: {
        categoryId: filters.categoryId || undefined,
        brandId: filters.brandId || undefined,
        supplierId: filters.supplierId || undefined,
        warehouseId: filters.warehouseId || undefined,
        active: filters.active !== undefined && filters.active !== '' ? filters.active : undefined,
        page,
        size,
        sort: sortBy,
        direction,
      },
    });
    return res.data;
  },

  getLowStock: async (page = 0, size = 10, sortBy = 'quantity', direction = 'ASC') => {
    const res = await api.get(`${API.PRODUCTS}/low-stock`, {
      params: { page, size, sort: sortBy, direction },
    });
    return res.data;
  },

  create: async (data) => {
    const res = await api.post(API.PRODUCTS, data);
    return res.data;
  },

  update: async (id, data) => {
    const res = await api.put(`${API.PRODUCTS}/${id}`, data);
    return res.data;
  },

  delete: async (id) => {
    const res = await api.delete(`${API.PRODUCTS}/${id}`);
    return res.data;
  },
};
