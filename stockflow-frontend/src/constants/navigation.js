import {
  LayoutDashboard,
  Package,
  Tag,
  Boxes,
  Truck,
  Warehouse,
  ShoppingCart,
  Receipt,
  BarChart3,
  Users,
  Settings,
} from 'lucide-react';
import { ROUTES } from './routes';

/**
 * navigation.js — Role-based sidebar navigation map.
 * ADMIN vs STAFF role separation.
 */
export const NAV_ITEMS = [
  {
    label: 'Dashboard',
    icon: LayoutDashboard,
    path: ROUTES.DASHBOARD,
    roles: ['ADMIN', 'STAFF'],
  },
  {
    label: 'Products',
    icon: Package,
    path: ROUTES.PRODUCTS,
    roles: ['ADMIN', 'STAFF'],
  },
  {
    label: 'Inventory',
    icon: Boxes,
    path: ROUTES.INVENTORY,
    roles: ['ADMIN', 'STAFF'],
  },
  {
    label: 'Purchases',
    icon: ShoppingCart,
    path: ROUTES.PURCHASES,
    roles: ['ADMIN', 'STAFF'],
  },
  {
    label: 'Sales',
    icon: Receipt,
    path: ROUTES.SALES,
    roles: ['ADMIN', 'STAFF'],
  },
  {
    label: 'Categories',
    icon: Tag,
    path: ROUTES.CATEGORIES,
    roles: ['ADMIN'],
  },
  {
    label: 'Brands',
    icon: Tag,
    path: ROUTES.BRANDS,
    roles: ['ADMIN'],
  },
  {
    label: 'Suppliers',
    icon: Truck,
    path: ROUTES.SUPPLIERS,
    roles: ['ADMIN'],
  },
  {
    label: 'Warehouses',
    icon: Warehouse,
    path: ROUTES.WAREHOUSES,
    roles: ['ADMIN'],
  },
  {
    label: 'Reports',
    icon: BarChart3,
    path: ROUTES.REPORTS,
    roles: ['ADMIN'],
  },
  {
    label: 'User Management',
    icon: Users,
    path: ROUTES.USERS,
    roles: ['ADMIN'],
  },
  {
    label: 'Settings',
    icon: Settings,
    path: ROUTES.SETTINGS,
    roles: ['ADMIN'],
  },
];

export const getNavItemsForRole = (role) =>
  NAV_ITEMS.filter((item) => item.roles.includes(role?.toUpperCase() || 'STAFF'));


