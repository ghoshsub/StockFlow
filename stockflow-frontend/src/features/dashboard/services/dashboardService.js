import api from '@/services/api';
import { API } from '@/constants/api';

/**
 * dashboardService — Service functions for dashboard endpoints.
 */
export const dashboardService = {
  getSummary: async () => {
    const res = await api.get(API.DASHBOARD.SUMMARY);
    return res.data;
  },

  getInventoryAnalytics: async () => {
    const res = await api.get(API.DASHBOARD.INVENTORY);
    return res.data;
  },

  getSalesAnalytics: async () => {
    const res = await api.get(API.DASHBOARD.SALES);
    return res.data;
  },

  getPurchaseAnalytics: async () => {
    const res = await api.get(API.DASHBOARD.PURCHASES);
    return res.data;
  },

  getRevenueChart: async () => {
    const res = await api.get(API.DASHBOARD.CHARTS.REVENUE_MONTHLY);
    return res.data;
  },

  getPurchasesChart: async () => {
    const res = await api.get(API.DASHBOARD.CHARTS.PURCHASES_MONTHLY);
    return res.data;
  },

  getCategoryDistribution: async () => {
    const res = await api.get(API.DASHBOARD.CHARTS.CATEGORY_DISTRIBUTION);
    return res.data;
  },

  getWarehouseDistribution: async () => {
    const res = await api.get(API.DASHBOARD.CHARTS.WAREHOUSE_DISTRIBUTION);
    return res.data;
  },
};
