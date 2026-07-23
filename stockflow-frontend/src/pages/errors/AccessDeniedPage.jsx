import { Link, useNavigate } from 'react-router-dom';
import { ShieldAlert, ArrowLeft, LayoutDashboard, Lock } from 'lucide-react';
import { Button } from '@/shared/components/ui/Button';
import { ROUTES } from '@/constants/routes';
import useAuthStore from '@/store/authStore';

/**
 * AccessDeniedPage — 403 Forbidden Access Denied page for unauthorized role access.
 */
export function AccessDeniedPage() {
  const navigate = useNavigate();
  const { role } = useAuthStore();

  return (
    <div className="min-h-[80vh] flex flex-col items-center justify-center text-center p-6 space-y-6 animate-in fade-in duration-300">
      {/* Icon Badge */}
      <div className="h-20 w-20 rounded-3xl bg-rose-500/10 border border-rose-500/30 flex items-center justify-center text-rose-500 shadow-xl shadow-rose-500/10 mb-2">
        <ShieldAlert size={42} />
      </div>

      {/* Title & Badge */}
      <div className="space-y-2 max-w-md">
        <div className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-rose-500/15 border border-rose-500/30 text-rose-400 text-xs font-mono font-bold uppercase tracking-wider">
          <Lock size={12} />
          <span>HTTP 403 Forbidden</span>
        </div>
        <h1
          className="text-3xl sm:text-4xl font-extrabold text-[var(--text-primary)]"
          style={{ fontFamily: 'var(--font-heading)' }}
        >
          Access Denied
        </h1>
        <p className="text-xs sm:text-sm text-[var(--text-secondary)] font-medium leading-relaxed">
          Your account role (<strong className="text-rose-400 font-mono uppercase">{role || 'STAFF'}</strong>) does not have authorization to view this enterprise module.
        </p>
      </div>

      {/* Action Buttons */}
      <div className="flex flex-wrap items-center justify-center gap-3 pt-4">
        <Button
          variant="outline"
          size="sm"
          icon={ArrowLeft}
          onClick={() => navigate(-1)}
        >
          Go Back
        </Button>
        <Button
          variant="primary"
          size="sm"
          icon={LayoutDashboard}
          onClick={() => navigate(ROUTES.DASHBOARD)}
        >
          Back to Dashboard
        </Button>
      </div>

      <p className="text-[11px] text-[var(--text-muted)] font-mono">
        If you believe this is an error, please contact your System Administrator.
      </p>
    </div>
  );
}
