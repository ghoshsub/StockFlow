import api from '@/services/api';
import { API } from '@/constants/api';

/**
 * saleService — API integration service for Sales Management.
 */
export const saleService = {
  getPaginated: async (page = 0, size = 10, sortBy = 'saleDate', direction = 'DESC') => {
    const res = await api.get(`${API.SALES}/page`, {
      params: { page, size, sort: sortBy, direction },
    });
    return res.data;
  },

  getAllList: async () => {
    const res = await api.get(API.SALES);
    return res.data;
  },

  getById: async (id) => {
    const res = await api.get(`${API.SALES}/${id}`);
    return res.data;
  },

  search: async (params = {}, page = 0, size = 10, sortBy = 'saleDate', direction = 'DESC') => {
    const res = await api.get(`${API.SALES}/search`, {
      params: {
        saleNumber: params.saleNumber || undefined,
        customerName: params.customerName || undefined,
        customerEmail: params.customerEmail || undefined,
        productKeyword: params.productKeyword || undefined,
        page,
        size,
        sort: sortBy,
        direction,
      },
    });
    return res.data;
  },

  create: async (data) => {
    const res = await api.post(API.SALES, data);
    return res.data;
  },
};
