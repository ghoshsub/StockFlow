import { Outlet, useSearchParams } from 'react-router-dom';
import { ShieldCheck, Zap, BarChart3, Lock } from 'lucide-react';

/**
 * AuthLayout — Modern glassmorphic hero container layout for Login & Register flows.
 * Adjusts the form panel width based on whether the user is on the role chooser (wider)
 * or a specific login form (narrower).
 */
export function AuthLayout() {
  const [searchParams] = useSearchParams();
  const roleParam = searchParams.get('role');

  // On the role chooser (/login without ?role=), we need a wider right panel
  const isChooser = !roleParam;

  return (
    <div className="min-h-screen relative flex items-center justify-center bg-[var(--bg-primary)] bg-mesh-gradient p-4 sm:p-6 overflow-hidden transition-all duration-300">
      {/* Background Glowing Ambient Orbs */}
      <div className="absolute top-1/4 left-10 w-72 h-72 bg-indigo-500/20 rounded-full filter blur-3xl animate-pulse pointer-events-none" />
      <div className="absolute bottom-1/4 right-10 w-96 h-96 bg-purple-500/15 rounded-full filter blur-3xl animate-pulse pointer-events-none" />

      <div className={`w-full z-10 my-auto ${isChooser ? 'max-w-2xl' : 'max-w-5xl grid grid-cols-1 lg:grid-cols-12 gap-8 items-center'}`}>

        {/* Left Hero Brand Panel (visible on desktop, only when not on role chooser) */}
        {!isChooser && (
          <div className="hidden lg:flex lg:col-span-7 flex-col justify-between p-8 space-y-8">
            <div className="space-y-4">
              <div className="inline-flex items-center gap-2 px-3 py-1.5 rounded-full bg-indigo-500/15 border border-indigo-500/30 text-indigo-400 text-xs font-bold font-heading uppercase tracking-wider">
                <Zap size={14} className="text-amber-400 fill-amber-400" /> Enterprise Supply Chain v2.0
              </div>
              <h1 className="text-4xl xl:text-5xl font-extrabold tracking-tight font-heading bg-gradient-to-r from-slate-100 via-indigo-100 to-purple-200 bg-clip-text text-transparent leading-tight">
                Intelligent Inventory &amp; Procurement Operating System
              </h1>
              <p className="text-sm xl:text-base text-[var(--text-secondary)] font-medium max-w-lg leading-relaxed">
                Streamline multi-warehouse stock allocations, purchase orders, sales fulfillment, and real-time executive analytics in one unified platform.
              </p>
            </div>

            <div className="grid grid-cols-3 gap-4 pt-4 border-t border-[var(--border-color)]">
              <div className="p-3.5 rounded-xl bg-slate-500/5 border border-[var(--border-color)]">
                <ShieldCheck className="text-emerald-400 mb-2" size={20} />
                <p className="text-xs font-bold text-[var(--text-primary)] font-heading">Stateless JWT</p>
                <p className="text-[11px] text-[var(--text-muted)]">Spring Security 6</p>
              </div>
              <div className="p-3.5 rounded-xl bg-slate-500/5 border border-[var(--border-color)]">
                <BarChart3 className="text-indigo-400 mb-2" size={20} />
                <p className="text-xs font-bold text-[var(--text-primary)] font-heading">11 Real-Time KPIs</p>
                <p className="text-[11px] text-[var(--text-muted)]">Recharts Analytics</p>
              </div>
              <div className="p-3.5 rounded-xl bg-slate-500/5 border border-[var(--border-color)]">
                <Lock className="text-purple-400 mb-2" size={20} />
                <p className="text-xs font-bold text-[var(--text-primary)] font-heading">RBAC Control</p>
                <p className="text-[11px] text-[var(--text-muted)]">Admin & Staff</p>
              </div>
            </div>
          </div>
        )}

        {/* Right Form Card Container */}
        <div className={isChooser ? 'w-full' : 'lg:col-span-5 w-full max-w-md mx-auto'}>
          <Outlet />
        </div>
      </div>
    </div>
  );
}
