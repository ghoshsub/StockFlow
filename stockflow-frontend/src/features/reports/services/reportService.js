import api from '@/services/api';
import { API } from '@/constants/api';

/**
 * reportService — Service for fetching report data and downloading file exports (PDF, Excel, CSV).
 */
export const reportService = {
  getInventoryReport: async () => {
    const res = await api.get(`${API.REPORTS}/inventory`);
    return res.data;
  },

  getPurchaseReport: async (params = {}) => {
    const res = await api.get(`${API.REPORTS}/purchases`, { params });
    return res.data;
  },

  getSalesReport: async (params = {}) => {
    const res = await api.get(`${API.REPORTS}/sales`, { params });
    return res.data;
  },

  getFinancialReport: async () => {
    const res = await api.get(`${API.REPORTS}/financial`);
    return res.data;
  },

  /**
   * Helper to trigger binary blob file download in browser.
   */
  exportReport: async (endpoint, params = {}, defaultFilename = 'report.pdf') => {
    const res = await api.get(`${API.REPORTS}/${endpoint}/export`, {
      params,
      responseType: 'blob',
    });

    const url = window.URL.createObjectURL(new Blob([res.data]));
    const link = document.createElement('a');
    link.href = url;
    
    // Extract filename from header or format
    const format = params.format || 'pdf';
    const ext = format === 'excel' ? 'xlsx' : format;
    link.setAttribute('download', `${defaultFilename}.${ext}`);
    
    document.body.appendChild(link);
    link.click();
    link.remove();
    window.URL.revokeObjectURL(url);
  },
};
