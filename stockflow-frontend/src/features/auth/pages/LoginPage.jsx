import { useEffect } from 'react';
import { useSearchParams, useNavigate } from 'react-router-dom';
import {
  ArrowLeft,
  Layers,
  ShieldCheck,
  UserCheck,
  Sparkles,
  ArrowRight,
} from 'lucide-react';
import { Card } from '@/shared/components/ui/Card';
import { LoginForm } from '../components/LoginForm';
import { ROUTES } from '@/constants/routes';
import useAuthStore from '@/store/authStore';

/**
 * LoginPage — Glassmorphic login page with role selection.
 *
 * Flow:
 *  /login              → Role Chooser: two cards (Admin / Staff)
 *  /login?role=admin   → Admin login form (pre-filled demo creds)
 *  /login?role=staff   → Staff login form (pre-filled demo creds)
 *
 * Auto-redirects already-authenticated users to /dashboard.
 */
export function LoginPage() {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const { isAuthenticated } = useAuthStore();
  const roleParam = searchParams.get('role')?.toLowerCase(); // 'admin' | 'staff' | null

  // If already logged in, redirect to dashboard
  useEffect(() => {
    if (isAuthenticated) {
      navigate(ROUTES.DASHBOARD, { replace: true });
    }
  }, [isAuthenticated, navigate]);

  const isAdminFlow = roleParam === 'admin';
  const isStaffFlow = roleParam === 'staff';
  const isRoleChooser = !isAdminFlow && !isStaffFlow;

  // Pre-fill demo credentials based on ?role= param
  const defaultValues = isAdminFlow
    ? { usernameOrEmail: 'admin', password: 'Admin@1234' }
    : isStaffFlow
    ? { usernameOrEmail: 'staff', password: 'Staff@1234' }
    : { usernameOrEmail: '', password: '' };

  // Visual config per role
  const config = isAdminFlow
    ? {
        gradient: 'from-indigo-600 via-purple-600 to-pink-500',
        shadow: 'shadow-indigo-500/30',
        Icon: ShieldCheck,
        title: 'Administrator Portal',
        subtitle: 'Sign in to access full system management controls',
        badge: 'ADMIN ACCESS',
        badgeClass: 'bg-indigo-500/15 border-indigo-500/30 text-indigo-400',
        demoText: (
          <>
            Demo Admin: <strong className="text-white">admin</strong> /{' '}
            <strong className="text-white">Admin@1234</strong>
          </>
        ),
      }
    : isStaffFlow
    ? {
        gradient: 'from-emerald-600 via-teal-600 to-cyan-500',
        shadow: 'shadow-emerald-500/30',
        Icon: UserCheck,
        title: 'Staff Operations Portal',
        subtitle: 'Sign in to manage inventory, purchases, and sales',
        badge: 'STAFF ACCESS',
        badgeClass: 'bg-emerald-500/15 border-emerald-500/30 text-emerald-400',
        demoText: (
          <>
            Demo Staff: <strong className="text-white">staff</strong> /{' '}
            <strong className="text-white">Staff@1234</strong>
          </>
        ),
      }
    : null;

  // ── Role Chooser View ─────────────────────────────────────────────────────
  if (isRoleChooser) {
    return (
      <div className="space-y-6 w-full">
        {/* Back to landing */}
        <button
          id="login-back-btn"
          onClick={() => navigate(ROUTES.LANDING)}
          className="flex items-center gap-1.5 text-[10px] font-bold text-[var(--text-muted)] hover:text-[var(--text-primary)] transition-colors"
          style={{ fontFamily: 'var(--font-heading)', textTransform: 'uppercase', letterSpacing: '0.05em' }}
        >
          <ArrowLeft size={13} />
          Back to Home
        </button>

        {/* Header */}
        <div className="text-center space-y-2">
          <div className="inline-flex items-center justify-center h-14 w-14 rounded-2xl bg-gradient-to-tr from-indigo-600 via-purple-600 to-pink-500 text-white shadow-lg shadow-indigo-500/30 mb-2">
            <Layers size={28} />
          </div>
          <h2
            className="text-2xl font-extrabold tracking-tight text-[var(--text-primary)]"
            style={{ fontFamily: 'var(--font-heading)' }}
          >
            Select Your Portal
          </h2>
          <p className="text-xs text-[var(--text-secondary)] font-medium">
            Choose your role to access the correct login portal
          </p>
        </div>

        {/* Role cards */}
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
          {/* Admin Card */}
          <button
            id="chooser-admin-btn"
            onClick={() => navigate('/login?role=admin')}
            className="group relative overflow-hidden p-6 rounded-2xl border text-left transition-all duration-200 hover:scale-[1.02] active:scale-[0.98]"
            style={{
              background: 'linear-gradient(135deg, rgba(99,102,241,0.1), rgba(168,85,247,0.05))',
              border: '1px solid rgba(99,102,241,0.25)',
            }}
            onMouseEnter={e => (e.currentTarget.style.borderColor = 'rgba(99,102,241,0.6)')}
            onMouseLeave={e => (e.currentTarget.style.borderColor = 'rgba(99,102,241,0.25)')}
          >
            <div className="absolute -top-6 -right-6 w-24 h-24 bg-indigo-500/10 rounded-full blur-xl pointer-events-none" />
            <div className="flex items-center justify-between mb-3">
              <div className="h-11 w-11 rounded-xl bg-indigo-500/20 border border-indigo-500/30 flex items-center justify-center text-indigo-400 group-hover:scale-110 transition-transform">
                <ShieldCheck size={22} />
              </div>
              <ArrowRight size={16} className="text-indigo-400 opacity-0 group-hover:opacity-100 transition-opacity" />
            </div>
            <p className="text-sm font-bold text-[var(--text-primary)] font-heading">Admin Portal</p>
            <p className="text-[11px] text-[var(--text-muted)] mt-0.5">Full system access & management</p>
            <span className="mt-3 inline-block text-[9px] font-extrabold text-indigo-400 bg-indigo-500/15 border border-indigo-500/30 px-2 py-0.5 rounded-full uppercase tracking-wider font-mono">
              ADMIN
            </span>
          </button>

          {/* Staff Card */}
          <button
            id="chooser-staff-btn"
            onClick={() => navigate('/login?role=staff')}
            className="group relative overflow-hidden p-6 rounded-2xl border text-left transition-all duration-200 hover:scale-[1.02] active:scale-[0.98]"
            style={{
              background: 'linear-gradient(135deg, rgba(16,185,129,0.1), rgba(20,184,166,0.05))',
              border: '1px solid rgba(16,185,129,0.25)',
            }}
            onMouseEnter={e => (e.currentTarget.style.borderColor = 'rgba(16,185,129,0.6)')}
            onMouseLeave={e => (e.currentTarget.style.borderColor = 'rgba(16,185,129,0.25)')}
          >
            <div className="absolute -top-6 -right-6 w-24 h-24 bg-emerald-500/10 rounded-full blur-xl pointer-events-none" />
            <div className="flex items-center justify-between mb-3">
              <div className="h-11 w-11 rounded-xl bg-emerald-500/20 border border-emerald-500/30 flex items-center justify-center text-emerald-400 group-hover:scale-110 transition-transform">
                <UserCheck size={22} />
              </div>
              <ArrowRight size={16} className="text-emerald-400 opacity-0 group-hover:opacity-100 transition-opacity" />
            </div>
            <p className="text-sm font-bold text-[var(--text-primary)] font-heading">Staff Portal</p>
            <p className="text-[11px] text-[var(--text-muted)] mt-0.5">Inventory, purchases & sales operations</p>
            <span className="mt-3 inline-block text-[9px] font-extrabold text-emerald-400 bg-emerald-500/15 border border-emerald-500/30 px-2 py-0.5 rounded-full uppercase tracking-wider font-mono">
              STAFF
            </span>
          </button>
        </div>
      </div>
    );
  }

  // ── Role-specific Login Form View ─────────────────────────────────────────
  const { gradient, shadow, Icon, title, subtitle, badge, badgeClass, demoText } = config;

  return (
    <Card className="glass-card shadow-2xl p-8 border border-[var(--glass-border)]">
      {/* Back to role chooser */}
      <button
        id="login-back-btn"
        onClick={() => navigate(ROUTES.LOGIN)}
        className="flex items-center gap-1.5 text-[10px] font-bold text-[var(--text-muted)] hover:text-[var(--text-primary)] mb-6 transition-colors"
        style={{ fontFamily: 'var(--font-heading)', textTransform: 'uppercase', letterSpacing: '0.05em' }}
      >
        <ArrowLeft size={13} />
        Back to Portal Selection
      </button>

      <div className="text-center mb-8">
        {/* Icon */}
        <div
          className={`inline-flex items-center justify-center h-14 w-14 rounded-2xl bg-gradient-to-tr ${gradient} text-white shadow-lg ${shadow} mb-4`}
        >
          <Icon size={30} />
        </div>

        {/* Role badge */}
        {badge && (
          <div className={`inline-flex items-center gap-1.5 px-3 py-1 rounded-full border text-[10px] font-extrabold font-mono uppercase tracking-wider mb-3 ${badgeClass}`}>
            {badge}
          </div>
        )}

        <h2
          className="text-2xl font-extrabold tracking-tight text-[var(--text-primary)]"
          style={{ fontFamily: 'var(--font-heading)' }}
        >
          {title}
        </h2>
        <p className="text-xs text-[var(--text-secondary)] mt-1.5 font-medium">{subtitle}</p>
      </div>

      {/* Login form — receives pre-filled defaults */}
      <LoginForm defaultValues={defaultValues} />

      {/* Demo credentials strip */}
      <div className="mt-8 text-center text-xs text-[var(--text-muted)] border-t border-[var(--border-color)] pt-5 space-y-3 font-medium">
        <div className="inline-flex items-center justify-center gap-1.5 px-3 py-1.5 rounded-xl bg-indigo-500/10 border border-indigo-500/20 text-[11px] text-indigo-400 font-mono">
          <Sparkles size={12} className="text-amber-400" />
          <span>{demoText}</span>
        </div>
      </div>
    </Card>
  );
}
