/** All backend API endpoint paths (relative to baseURL) */
export const API = {
  // Auth
  AUTH: {
    LOGIN:    '/auth/login',
    REGISTER: '/auth/register',
    ME:       '/auth/me',
    PING:     '/auth/ping',
  },

  // Dashboard
  DASHBOARD: {
    SUMMARY:                '/dashboard/summary',
    INVENTORY:              '/dashboard/inventory',
    SALES:                  '/dashboard/sales',
    PURCHASES:              '/dashboard/purchases',
    CHARTS: {
      REVENUE_MONTHLY:      '/dashboard/charts/revenue-monthly',
      PURCHASES_MONTHLY:    '/dashboard/charts/purchases-monthly',
      CATEGORY_DISTRIBUTION:'/dashboard/charts/category-distribution',
      WAREHOUSE_DISTRIBUTION:'/dashboard/charts/warehouse-distribution',
    },
  },

  // Products
  PRODUCTS:   '/products',

  // Categories
  CATEGORIES: '/categories',

  // Brands
  BRANDS:     '/brands',

  // Suppliers
  SUPPLIERS:  '/suppliers',

  // Warehouses
  WAREHOUSES: '/warehouses',

  // Inventory
  INVENTORY:  '/inventory',

  // Purchases
  PURCHASES:  '/purchases',

  // Sales
  SALES:      '/sales',

  // Reports
  REPORTS:    '/reports',
};
