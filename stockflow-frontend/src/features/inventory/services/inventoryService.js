import api from '@/services/api';
import { API } from '@/constants/api';

/**
 * inventoryService — API service for Inventory & Stock Management endpoints.
 */
export const inventoryService = {
  stockIn: async (data) => {
    const res = await api.post(`${API.INVENTORY}/stock-in`, data);
    return res.data;
  },

  stockOut: async (data) => {
    const res = await api.post(`${API.INVENTORY}/stock-out`, data);
    return res.data;
  },

  adjustStock: async (data) => {
    const res = await api.post(`${API.INVENTORY}/adjust`, data);
    return res.data;
  },

  getProductInventory: async (productId) => {
    const res = await api.get(`${API.INVENTORY}/product/${productId}`);
    return res.data;
  },

  getProductHistory: async (productId, page = 0, size = 10, sortBy = 'movementDate', direction = 'DESC') => {
    const res = await api.get(`${API.INVENTORY}/history/${productId}`, {
      params: { page, size, sort: sortBy, direction },
    });
    return res.data;
  },

  getLowStockProducts: async (page = 0, size = 10, sortBy = 'quantity', direction = 'ASC') => {
    const res = await api.get(`${API.INVENTORY}/low-stock`, {
      params: { page, size, sort: sortBy, direction },
    });
    return res.data;
  },

  searchMovements: async (filters = {}, page = 0, size = 10, sortBy = 'movementDate', direction = 'DESC') => {
    const res = await api.get(`${API.INVENTORY}/movements/search`, {
      params: {
        productId: filters.productId || undefined,
        movementType: filters.movementType || undefined,
        dateFrom: filters.dateFrom || undefined,
        dateTo: filters.dateTo || undefined,
        username: filters.username || undefined,
        page,
        size,
        sort: sortBy,
        direction,
      },
    });
    return res.data;
  },
};
