import { cn } from '@/shared/utils/cn';

/**
 * Badge — Status badge with semantic color variants and pulsing dot indicator.
 */
const variants = {
  success: 'bg-emerald-500/10 text-emerald-600 dark:text-emerald-400 border border-emerald-500/30 shadow-sm shadow-emerald-500/10',
  warning: 'bg-amber-500/10 text-amber-600 dark:text-amber-400 border border-amber-500/30 shadow-sm shadow-amber-500/10',
  danger:  'bg-rose-500/10 text-rose-600 dark:text-rose-400 border border-rose-500/30 shadow-sm shadow-rose-500/10',
  info:    'bg-sky-500/10 text-sky-600 dark:text-sky-400 border border-sky-500/30 shadow-sm shadow-sky-500/10',
  primary: 'bg-indigo-500/10 text-indigo-600 dark:text-indigo-400 border border-indigo-500/30 shadow-sm shadow-indigo-500/10',
  neutral: 'bg-slate-500/10 text-slate-600 dark:text-slate-400 border border-slate-500/30 shadow-sm',
};

const dotColors = {
  success: 'bg-emerald-500',
  warning: 'bg-amber-500',
  danger:  'bg-rose-500',
  info:    'bg-sky-500',
  primary: 'bg-indigo-500',
  neutral: 'bg-slate-400',
};

export function Badge({ children, variant = 'neutral', showDot = true, className }) {
  return (
    <span
      className={cn(
        'inline-flex items-center gap-1.5 px-2.5 py-0.5 rounded-full text-xs font-bold tracking-wide font-heading uppercase',
        variants[variant],
        className
      )}
    >
      {showDot && (
        <span className="relative flex h-2 w-2">
          <span className={cn("animate-ping absolute inline-flex h-full w-full rounded-full opacity-75", dotColors[variant])}></span>
          <span className={cn("relative inline-flex rounded-full h-2 w-2", dotColors[variant])}></span>
        </span>
      )}
      {children}
    </span>
  );
}

/**
 * Map common status strings to Badge variants.
 */
export function statusToBadgeVariant(status) {
  const map = {
    PAID: 'success', COMPLETED: 'success', ACTIVE: 'success', IN_STOCK: 'success', DELIVERED: 'success', CONFIRMED: 'success',
    PENDING: 'warning', PARTIAL: 'warning', LOW_STOCK: 'warning', SHIPPED: 'info',
    UNPAID: 'danger', CANCELLED: 'danger', OUT_OF_STOCK: 'danger', INACTIVE: 'danger',
    DRAFT: 'neutral',
  };
  return map[status?.toUpperCase()] ?? 'neutral';
}

