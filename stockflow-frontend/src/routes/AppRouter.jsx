import React, { lazy, Suspense } from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import { AuthLayout } from '@/layouts/AuthLayout';
import { DashboardLayout } from '@/layouts/DashboardLayout';
import { FullPageSpinner } from '@/shared/components/ui/Spinner';
import { ProtectedRoute } from './ProtectedRoute';
import { ROUTES } from '@/constants/routes';

// ── Lazy-loaded page components (route-based code splitting) ─────────────────

// Public pages
const LandingPage  = lazy(() => import('@/pages/LandingPage').then((m) => ({ default: m.LandingPage })));
const LoginPage    = lazy(() => import('@/features/auth/pages/LoginPage').then((m) => ({ default: m.LoginPage })));

// Shared (all authenticated roles)
const DashboardPage = lazy(() => import('@/features/dashboard/pages/DashboardPage').then((m) => ({ default: m.DashboardPage })));
const ProductPage   = lazy(() => import('@/features/product/pages/ProductPage').then((m) => ({ default: m.ProductPage })));
const InventoryPage = lazy(() => import('@/features/inventory/pages/InventoryPage').then((m) => ({ default: m.InventoryPage })));
const PurchasePage  = lazy(() => import('@/features/purchase/pages/PurchasePage').then((m) => ({ default: m.PurchasePage })));
const SalesPage     = lazy(() => import('@/features/sales/pages/SalesPage').then((m) => ({ default: m.SalesPage })));

// Admin / Manager only
const CategoryPage  = lazy(() => import('@/features/category/pages/CategoryPage').then((m) => ({ default: m.CategoryPage })));
const BrandPage     = lazy(() => import('@/features/brand/pages/BrandPage').then((m) => ({ default: m.BrandPage })));
const SupplierPage  = lazy(() => import('@/features/supplier/pages/SupplierPage').then((m) => ({ default: m.SupplierPage })));
const WarehousePage = lazy(() => import('@/features/warehouse/pages/WarehousePage').then((m) => ({ default: m.WarehousePage })));
const ReportsPage   = lazy(() => import('@/features/reports/pages/ReportsPage').then((m) => ({ default: m.ReportsPage })));
const SettingsPage  = lazy(() => import('@/features/settings/pages/SettingsPage').then((m) => ({ default: m.SettingsPage })));

// Admin only
const UserPage = lazy(() => import('@/features/users/pages/UserPage').then((m) => ({ default: m.UserPage })));

// Error pages
const AccessDeniedPage = lazy(() => import('@/pages/errors/AccessDeniedPage').then((m) => ({ default: m.AccessDeniedPage })));
const NotFoundPage     = lazy(() => import('@/pages/errors/NotFoundPage').then((m) => ({ default: m.NotFoundPage })));

/** Roles allowed for admin-level management routes */
const ADMIN_ROLES = ['ADMIN'];

export function AppRouter() {
  return (
    <Suspense fallback={<FullPageSpinner message="Loading StockFlow..." />}>
      <Routes>

        {/* ── Public Landing Page ─────────────────────────────────────── */}
        <Route path={ROUTES.LANDING} element={<LandingPage />} />

        {/* ── Public Auth Routes (inside AuthLayout) ─────────────────── */}
        <Route element={<AuthLayout />}>
          <Route path={ROUTES.LOGIN} element={<LoginPage />} />
        </Route>

        {/* ── Protected Dashboard Routes ──────────────────────────────── */}
        {/* Any authenticated user may enter the dashboard shell */}
        <Route element={<ProtectedRoute />}>
          <Route element={<DashboardLayout />}>

            {/* Shared routes — ADMIN and STAFF */}
            <Route path={ROUTES.DASHBOARD}     element={<DashboardPage />} />
            <Route path={ROUTES.PRODUCTS}      element={<ProductPage />} />
            <Route path={ROUTES.INVENTORY}     element={<InventoryPage />} />
            <Route path={ROUTES.PURCHASES}     element={<PurchasePage />} />
            <Route path={ROUTES.SALES}         element={<SalesPage />} />
            <Route path={ROUTES.ACCESS_DENIED} element={<AccessDeniedPage />} />

            {/* ── Admin / Manager Only Routes ─────────────────────────── */}
            <Route element={<ProtectedRoute allowedRoles={ADMIN_ROLES} />}>
              <Route path={ROUTES.CATEGORIES} element={<CategoryPage />} />
              <Route path={ROUTES.BRANDS}     element={<BrandPage />} />
              <Route path={ROUTES.SUPPLIERS}  element={<SupplierPage />} />
              <Route path={ROUTES.WAREHOUSES} element={<WarehousePage />} />
              <Route path={ROUTES.REPORTS}    element={<ReportsPage />} />
              <Route path={ROUTES.SETTINGS}   element={<SettingsPage />} />
            </Route>

            {/* ── Admin Only Routes ────────────────────────────────────── */}
            <Route element={<ProtectedRoute allowedRoles={['ADMIN']} />}>
              <Route path={ROUTES.USERS} element={<UserPage />} />
            </Route>

          </Route>
        </Route>

        {/* ── Catch-all 404 ─────────────────────────────────────────────── */}
        <Route path="*" element={<NotFoundPage />} />

      </Routes>
    </Suspense>
  );
}
