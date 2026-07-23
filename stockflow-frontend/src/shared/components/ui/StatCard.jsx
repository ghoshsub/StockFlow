import { cn } from '@/shared/utils/cn';
import { TrendingUp, TrendingDown, Minus } from 'lucide-react';

/**
 * StatCard — Executive KPI card with glass design, ambient icon background, metric value, and trend indicators.
 */
export function StatCard({
  label,
  value,
  icon: Icon,
  trend,       // number — positive = up, negative = down
  trendLabel,  // string — e.g. "vs last month"
  colorClass = 'text-indigo-500 dark:text-indigo-400',
  bgClass = 'bg-gradient-to-br from-indigo-500/15 to-purple-500/10 border border-indigo-500/20',
  loading = false,
}) {
  if (loading) {
    return (
      <div className="glass-card p-6 rounded-2xl">
        <div className="skeleton h-12 w-12 rounded-xl mb-4" />
        <div className="skeleton h-8 w-28 rounded-lg mb-2" />
        <div className="skeleton h-4 w-36 rounded-md" />
      </div>
    );
  }

  const TrendIcon = trend > 0 ? TrendingUp : trend < 0 ? TrendingDown : Minus;
  const trendColor = trend > 0 ? 'text-emerald-500 bg-emerald-500/10 border-emerald-500/20' : trend < 0 ? 'text-rose-500 bg-rose-500/10 border-rose-500/20' : 'text-slate-400 bg-slate-500/10 border-slate-500/20';

  return (
    <div
      className={cn(
        'glass-card p-6 rounded-2xl relative overflow-hidden transition-all duration-300 hover:-translate-y-1 group hover:border-indigo-500/30'
      )}
    >
      <div className="flex items-start justify-between mb-4">
        <div className={cn('p-3 rounded-xl transition-transform duration-300 group-hover:scale-110 shadow-sm', bgClass)}>
          {Icon && <Icon size={22} className={colorClass} />}
        </div>
        {trend !== undefined && (
          <div className={cn('flex items-center gap-1.5 px-2.5 py-1 rounded-full text-xs font-bold border font-heading', trendColor)}>
            <TrendIcon size={13} />
            <span>{Math.abs(trend)}%</span>
          </div>
        )}
      </div>

      <div className="space-y-1">
        <p className="text-3xl font-extrabold text-[var(--text-primary)] tabular-nums tracking-tight font-heading">
          {value}
        </p>
        <p className="text-xs font-bold uppercase tracking-wider text-[var(--text-secondary)] font-heading">
          {label}
        </p>
        {trendLabel && (
          <p className="text-xs text-[var(--text-muted)] font-medium mt-1">{trendLabel}</p>
        )}
      </div>
    </div>
  );
}

