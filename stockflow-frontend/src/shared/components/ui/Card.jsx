import { cn } from '@/shared/utils/cn';

/**
 * Card — Glassmorphic styled card container with hover depth.
 */
export function Card({ children, className, title, subtitle, action, noPadding = false }) {
  return (
    <div
      className={cn(
        'glass-card relative overflow-hidden transition-all duration-300',
        className
      )}
    >
      {(title || action) && (
        <div className="flex items-center justify-between px-6 py-4 border-b border-[var(--border-color)] bg-slate-500/5">
          <div>
            {title && (
              <h3 className="text-base font-bold tracking-tight text-[var(--text-primary)] font-heading">
                {title}
              </h3>
            )}
            {subtitle && (
              <p className="text-xs text-[var(--text-muted)] mt-0.5 font-medium">{subtitle}</p>
            )}
          </div>
          {action && <div className="flex items-center gap-2">{action}</div>}
        </div>
      )}
      <div className={cn(!noPadding && 'p-6')}>{children}</div>
    </div>
  );
}

