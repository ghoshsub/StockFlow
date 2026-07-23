import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  ShieldCheck,
  UserCheck,
  ArrowRight,
  Layers,
  Sparkles,
  PackageCheck,
  Boxes,
  Truck,
  FileSpreadsheet,
  BarChart3,
  CheckCircle2,
  Zap,
  Lock,
  LogIn,
} from 'lucide-react';
import { ROUTES } from '@/constants/routes';
import useAuthStore from '@/store/authStore';

/**
 * LandingPage — Professional StockFlow enterprise portal.
 * - Single Sign In entry point leading to role selection portal (Admin or Staff).
 * - Auto-redirects authenticated users to their dashboard.
 */
export function LandingPage() {
  const navigate = useNavigate();
  const { isAuthenticated } = useAuthStore();

  // Auto-redirect authenticated users to dashboard
  useEffect(() => {
    if (isAuthenticated) {
      navigate(ROUTES.DASHBOARD, { replace: true });
    }
  }, [isAuthenticated, navigate]);

  return (
    <div
      className="min-h-screen bg-[var(--bg-primary)] bg-mesh-gradient text-[var(--text-primary)] flex flex-col overflow-x-hidden"
      style={{ fontFamily: 'var(--font-sans)' }}
    >
      {/* ── Top Header ──────────────────────────────────────────────────── */}
      <header className="sticky top-0 z-30 glass-header px-6 lg:px-12 py-4 flex items-center justify-between">
        {/* Brand */}
        <div className="flex items-center space-x-3">
          <div className="h-10 w-10 rounded-xl bg-gradient-to-tr from-indigo-600 via-purple-600 to-pink-500 flex items-center justify-center shadow-lg shadow-indigo-500/30">
            <Layers className="text-white" size={22} />
          </div>
          <div>
            <span
              className="text-xl font-extrabold tracking-tight"
              style={{
                fontFamily: 'var(--font-heading)',
                background: 'linear-gradient(to right, #f8fafc, #c7d2fe, #e9d5ff)',
                WebkitBackgroundClip: 'text',
                WebkitTextFillColor: 'transparent',
              }}
            >
              StockFlow
            </span>
            <span className="ml-2 text-[10px] font-extrabold text-indigo-400 bg-indigo-500/15 border border-indigo-500/30 px-2 py-0.5 rounded-full font-mono uppercase tracking-wider">
              Enterprise
            </span>
          </div>
        </div>

        {/* Header Actions */}
        <div className="flex items-center space-x-3">
          <button
            id="landing-login-btn"
            onClick={() => navigate(ROUTES.LOGIN)}
            className="flex items-center gap-2 px-5 py-2.5 text-xs font-bold rounded-xl bg-gradient-to-r from-indigo-600 to-purple-600 text-white shadow-md shadow-indigo-500/20 hover:from-indigo-500 hover:to-purple-500 transition-all active:scale-[0.98]"
            style={{ fontFamily: 'var(--font-heading)' }}
          >
            <LogIn size={14} />
            Sign In
          </button>
        </div>
      </header>

      {/* ── Main Content ─────────────────────────────────────────────────── */}
      <main className="max-w-7xl mx-auto px-6 lg:px-12 py-12 lg:py-16 space-y-16 flex-1 w-full">

        {/* Hero Section */}
        <div className="text-center space-y-6 max-w-4xl mx-auto">
          <div className="inline-flex items-center gap-2 px-4 py-1.5 rounded-full bg-indigo-500/15 border border-indigo-500/30 text-indigo-400 text-xs font-bold uppercase tracking-wider shadow-sm">
            <Sparkles size={14} className="text-amber-400 fill-amber-400" />
            <span style={{ fontFamily: 'var(--font-heading)' }}>Enterprise Warehouse Management System</span>
          </div>

          <h1
            className="text-4xl sm:text-6xl font-extrabold tracking-tight leading-tight"
            style={{
              fontFamily: 'var(--font-heading)',
              background: 'linear-gradient(to right, #f8fafc, #c7d2fe, #e9d5ff)',
              WebkitBackgroundClip: 'text',
              WebkitTextFillColor: 'transparent',
              lineHeight: 1.15,
            }}
          >
            Unified Inventory, Procurement &amp; Fulfillment Portal
          </h1>

          <p className="text-base sm:text-lg text-[var(--text-secondary)] font-medium leading-relaxed max-w-2xl mx-auto">
            Role-gated operating system supporting automated multi-warehouse stock deduction, purchase order receipts, real-time KPI dashboards, and PDF/Excel financial reports.
          </p>

          {/* CTA Buttons */}
          <div className="flex items-center justify-center gap-4 flex-wrap pt-2">
            <button
              id="landing-hero-login-btn"
              onClick={() => navigate(ROUTES.LOGIN)}
              className="flex items-center gap-2 px-6 py-3 text-sm font-bold rounded-xl bg-gradient-to-r from-indigo-600 to-purple-600 text-white shadow-lg shadow-indigo-500/25 hover:from-indigo-500 hover:to-purple-500 transition-all active:scale-[0.98]"
              style={{ fontFamily: 'var(--font-heading)' }}
            >
              <LogIn size={16} />
              Sign In to Portal
              <ArrowRight size={16} />
            </button>

          </div>

          {/* System Status Badges */}
          <div className="flex items-center justify-center gap-4 flex-wrap pt-2">
            <div className="inline-flex items-center gap-1.5 px-3 py-1.5 rounded-full bg-emerald-500/10 border border-emerald-500/25 text-emerald-400 text-xs font-bold font-mono">
              <span className="h-1.5 w-1.5 rounded-full bg-emerald-400 animate-ping" />
              API Online
            </div>
            <div className="inline-flex items-center gap-1.5 px-3 py-1.5 rounded-full bg-sky-500/10 border border-sky-500/25 text-sky-400 text-xs font-bold font-mono">
              <Zap size={12} />
              JWT Secured
            </div>
            <div className="inline-flex items-center gap-1.5 px-3 py-1.5 rounded-full bg-purple-500/10 border border-purple-500/25 text-purple-400 text-xs font-bold font-mono">
              <Lock size={12} />
              RBAC Enforced
            </div>
          </div>
        </div>

        {/* ── Dual Portal Selection Cards ──────────────────────────────── */}
        <div className="grid grid-cols-1 md:grid-cols-2 gap-8 max-w-5xl mx-auto">

          {/* Admin Portal Card */}
          <div
            id="landing-admin-card"
            className="glass-card p-8 rounded-3xl relative overflow-hidden group flex flex-col justify-between space-y-6 cursor-pointer"
            style={{ borderColor: 'rgba(99,102,241,0.2)', transition: 'border-color 0.3s' }}
            onClick={() => navigate('/login?role=admin')}
            onMouseEnter={e => e.currentTarget.style.borderColor = 'rgba(99,102,241,0.5)'}
            onMouseLeave={e => e.currentTarget.style.borderColor = 'rgba(99,102,241,0.2)'}
          >
            {/* Glow orb */}
            <div className="absolute -top-10 -right-10 w-40 h-40 bg-indigo-500/10 rounded-full filter blur-2xl pointer-events-none" />

            <div className="space-y-4">
              <div className="flex items-center justify-between">
                <div
                  className="h-14 w-14 rounded-2xl flex items-center justify-center text-indigo-400 transition-transform duration-300 group-hover:scale-110"
                  style={{ background: 'linear-gradient(135deg, rgba(99,102,241,0.2), rgba(168,85,247,0.1))', border: '1px solid rgba(99,102,241,0.3)' }}
                >
                  <ShieldCheck size={28} />
                </div>
                <span className="px-3 py-1 rounded-full bg-indigo-500/15 border border-indigo-500/30 text-indigo-300 text-xs font-bold font-mono uppercase">
                  ROLE: ADMIN
                </span>
              </div>

              <div>
                <h3
                  className="text-2xl font-bold text-[var(--text-primary)]"
                  style={{ fontFamily: 'var(--font-heading)' }}
                >
                  Administrator Portal
                </h3>
                <p className="text-xs text-[var(--text-muted)] mt-1 font-medium">
                  Full system authority across all operational &amp; management modules
                </p>
              </div>

              <ul className="space-y-2.5 pt-2">
                {[
                  'Executive KPI Dashboard & 4 Recharts Analytics',
                  'User Management & Role Assignment (CRUD)',
                  'Category, Brand, Supplier & Warehouse Master Data',
                  'Enterprise PDF, Excel & CSV Multi-Format Reports',
                  'System Settings & Theme Preferences',
                ].map((feat, i) => (
                  <li key={i} className="flex items-center gap-2.5 text-xs text-[var(--text-secondary)] font-medium">
                    <CheckCircle2 size={15} className="text-indigo-400 shrink-0" />
                    <span>{feat}</span>
                  </li>
                ))}
              </ul>
            </div>

            <div className="pt-4 border-t border-[var(--border-color)] space-y-3">
              <button
                id="landing-admin-portal-btn"
                onClick={e => { e.stopPropagation(); navigate('/login?role=admin'); }}
                className="w-full py-3 px-5 rounded-xl text-white font-bold text-sm flex items-center justify-center gap-2 transition-all active:scale-[0.98]"
                style={{
                  fontFamily: 'var(--font-heading)',
                  background: 'linear-gradient(to right, #4f46e5, #7c3aed)',
                  boxShadow: '0 4px 15px -4px rgba(99,102,241,0.4)',
                }}
                onMouseEnter={e => e.currentTarget.style.background = 'linear-gradient(to right, #6366f1, #8b5cf6)'}
                onMouseLeave={e => e.currentTarget.style.background = 'linear-gradient(to right, #4f46e5, #7c3aed)'}
              >
                <span>Access Admin Portal</span>
                <ArrowRight size={16} />
              </button>
              <p className="text-[11px] text-center text-[var(--text-muted)] font-mono">
                Demo: <strong className="text-indigo-400">admin</strong> / <strong className="text-indigo-400">Admin@1234</strong>
              </p>
            </div>
          </div>

          {/* Staff Operations Portal Card */}
          <div
            id="landing-staff-card"
            className="glass-card p-8 rounded-3xl relative overflow-hidden group flex flex-col justify-between space-y-6 cursor-pointer"
            style={{ borderColor: 'rgba(16,185,129,0.2)', transition: 'border-color 0.3s' }}
            onClick={() => navigate('/login?role=staff')}
            onMouseEnter={e => e.currentTarget.style.borderColor = 'rgba(16,185,129,0.5)'}
            onMouseLeave={e => e.currentTarget.style.borderColor = 'rgba(16,185,129,0.2)'}
          >
            {/* Glow orb */}
            <div className="absolute -top-10 -right-10 w-40 h-40 bg-emerald-500/10 rounded-full filter blur-2xl pointer-events-none" />

            <div className="space-y-4">
              <div className="flex items-center justify-between">
                <div
                  className="h-14 w-14 rounded-2xl flex items-center justify-center text-emerald-400 transition-transform duration-300 group-hover:scale-110"
                  style={{ background: 'linear-gradient(135deg, rgba(16,185,129,0.2), rgba(20,184,166,0.1))', border: '1px solid rgba(16,185,129,0.3)' }}
                >
                  <UserCheck size={28} />
                </div>
                <span className="px-3 py-1 rounded-full bg-emerald-500/15 border border-emerald-500/30 text-emerald-300 text-xs font-bold font-mono uppercase">
                  ROLE: STAFF
                </span>
              </div>

              <div>
                <h3
                  className="text-2xl font-bold text-[var(--text-primary)]"
                  style={{ fontFamily: 'var(--font-heading)' }}
                >
                  Staff Operations Portal
                </h3>
                <p className="text-xs text-[var(--text-muted)] mt-1 font-medium">
                  Streamlined daily execution interface focused on inventory tasks
                </p>
              </div>

              <ul className="space-y-2.5 pt-2">
                {[
                  'Product Catalog & Price Inquiry',
                  'Multi-Warehouse Inventory Stock Adjustment',
                  'Purchase Order Itemized Entry (Stock Inflow)',
                  'Sales Fulfillment & Validation (Stock Outflow)',
                  'Low Stock & Out-of-Stock Real-time Alerts',
                ].map((feat, i) => (
                  <li key={i} className="flex items-center gap-2.5 text-xs text-[var(--text-secondary)] font-medium">
                    <CheckCircle2 size={15} className="text-emerald-400 shrink-0" />
                    <span>{feat}</span>
                  </li>
                ))}
              </ul>
            </div>

            <div className="pt-4 border-t border-[var(--border-color)] space-y-3">
              <button
                id="landing-staff-portal-btn"
                onClick={e => { e.stopPropagation(); navigate('/login?role=staff'); }}
                className="w-full py-3 px-5 rounded-xl text-white font-bold text-sm flex items-center justify-center gap-2 transition-all active:scale-[0.98]"
                style={{
                  fontFamily: 'var(--font-heading)',
                  background: 'linear-gradient(to right, #059669, #0d9488)',
                  boxShadow: '0 4px 15px -4px rgba(16,185,129,0.4)',
                }}
                onMouseEnter={e => e.currentTarget.style.background = 'linear-gradient(to right, #10b981, #14b8a6)'}
                onMouseLeave={e => e.currentTarget.style.background = 'linear-gradient(to right, #059669, #0d9488)'}
              >
                <span>Access Staff Portal</span>
                <ArrowRight size={16} />
              </button>
              <p className="text-[11px] text-center text-[var(--text-muted)] font-mono">
                Demo: <strong className="text-emerald-400">staff</strong> / <strong className="text-emerald-400">Staff@1234</strong>
              </p>
            </div>
          </div>
        </div>

        {/* ── Feature Highlights Grid ──────────────────────────────────── */}
        <div className="pt-4">
          <div className="text-center mb-8">
            <h2
              className="text-xl font-bold uppercase tracking-wider text-[var(--text-primary)]"
              style={{ fontFamily: 'var(--font-heading)' }}
            >
              Enterprise Operating Pillars
            </h2>
            <p className="text-xs text-[var(--text-muted)] mt-1">Built on Spring Boot 3 + React 18 + Recharts</p>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
            {[
              {
                icon: Boxes,
                color: 'text-indigo-400',
                title: 'Automated Stock Logic',
                desc: 'Stock increments on purchase and decrements on sales with negative quantity guards and movement history.',
              },
              {
                icon: Truck,
                color: 'text-sky-400',
                title: 'Multi-Warehouse',
                desc: 'Track capacity allocations, SKU counts, and stock movements across multiple storage hubs.',
              },
              {
                icon: BarChart3,
                color: 'text-amber-400',
                title: '11 Executive KPIs',
                desc: 'Real-time financial valuation, revenue metrics, gross profit, and inventory health alerts.',
              },
              {
                icon: FileSpreadsheet,
                color: 'text-emerald-400',
                title: 'PDF & Excel Reports',
                desc: 'Download structured binary reports for financial audits, purchase summaries, and inventory states.',
              },
            ].map(({ icon: Icon, color, title, desc }, i) => (
              <div
                key={i}
                className="p-6 rounded-2xl glass-card space-y-3 hover:scale-[1.02] transition-transform duration-200"
              >
                <Icon className={color} size={24} />
                <h4
                  className="text-base font-bold text-[var(--text-primary)]"
                  style={{ fontFamily: 'var(--font-heading)' }}
                >
                  {title}
                </h4>
                <p className="text-xs text-[var(--text-secondary)] font-medium leading-relaxed">{desc}</p>
              </div>
            ))}
          </div>
        </div>

        {/* ── Metrics Strip ─────────────────────────────────────────────── */}
        <div className="grid grid-cols-2 sm:grid-cols-4 gap-6 max-w-3xl mx-auto text-center">
          {[
            { value: '13+', label: 'REST API Endpoints' },
            { value: 'JWT', label: 'Stateless Auth' },
            { value: 'RBAC', label: 'Role-Based Access' },
            { value: '6+', label: 'Report Types' },
          ].map(({ value, label }, i) => (
            <div key={i} className="space-y-1">
              <p
                className="text-3xl font-extrabold"
                style={{
                  fontFamily: 'var(--font-heading)',
                  background: 'linear-gradient(to right, #818cf8, #c084fc)',
                  WebkitBackgroundClip: 'text',
                  WebkitTextFillColor: 'transparent',
                }}
              >
                {value}
              </p>
              <p className="text-xs text-[var(--text-muted)] font-medium">{label}</p>
            </div>
          ))}
        </div>
      </main>

      {/* ── Footer ──────────────────────────────────────────────────────── */}
      <footer className="border-t border-[var(--border-color)] py-6 px-6 text-center text-xs text-[var(--text-muted)] font-medium">
        <p>© 2026 StockFlow Inc. Enterprise Supply Chain Management System. All rights reserved.</p>
      </footer>
    </div>
  );
}
