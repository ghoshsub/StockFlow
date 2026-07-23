import api from '@/services/api';

export const userService = {
  getAllPaginated: async (params = {}) => {
    const response = await api.get('/users', { params });
    return response.data;
  },

  getAllList: async () => {
    const response = await api.get('/users/all');
    return response.data;
  },

  getById: async (id) => {
    const response = await api.get(`/users/${id}`);
    return response.data;
  },

  search: async (keyword, params = {}) => {
    const response = await api.get('/users/search', {
      params: { keyword, ...params },
    });
    return response.data;
  },

  create: async (userData) => {
    const response = await api.post('/users', userData);
    return response.data;
  },

  update: async (id, userData) => {
    const response = await api.put(`/users/${id}`, userData);
    return response.data;
  },

  delete: async (id) => {
    const response = await api.delete(`/users/${id}`);
    return response.data;
  },

  getRoles: async () => {
    const response = await api.get('/users/roles');
    return response.data;
  },
};
