import api from '@/services/api';
import { API } from '@/constants/api';

/**
 * purchaseService — API integration service for Purchase Management.
 */
export const purchaseService = {
  getPaginated: async (page = 0, size = 10, sortBy = 'purchaseDate', direction = 'DESC') => {
    const res = await api.get(`${API.PURCHASES}/page`, {
      params: { page, size, sort: sortBy, direction },
    });
    return res.data;
  },

  getAllList: async () => {
    const res = await api.get(API.PURCHASES);
    return res.data;
  },

  getById: async (id) => {
    const res = await api.get(`${API.PURCHASES}/${id}`);
    return res.data;
  },

  search: async (params = {}, page = 0, size = 10, sortBy = 'purchaseDate', direction = 'DESC') => {
    const res = await api.get(`${API.PURCHASES}/search`, {
      params: {
        purchaseNumber: params.purchaseNumber || undefined,
        supplierName: params.supplierName || undefined,
        invoiceNumber: params.invoiceNumber || undefined,
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
    const res = await api.post(API.PURCHASES, data);
    return res.data;
  },
};
