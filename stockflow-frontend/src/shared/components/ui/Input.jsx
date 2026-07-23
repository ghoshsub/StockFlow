import { forwardRef } from 'react';
import { cn } from '@/shared/utils/cn';

/**
 * Input — Glass-styled input with label, glowing focus state, error indicator, and left icon support.
 */
export const Input = forwardRef(function Input(
  { label, error, icon: Icon, className, id, ...props },
  ref
) {
  return (
    <div className="flex flex-col gap-1.5">
      {label && (
        <label
          htmlFor={id}
          className="text-xs font-bold tracking-wide uppercase text-[var(--text-secondary)] font-heading"
        >
          {label}
        </label>
      )}
      <div className="relative">
        {Icon && (
          <div className="absolute inset-y-0 left-0 flex items-center pl-3.5 pointer-events-none">
            <Icon size={18} className="text-[var(--text-muted)] transition-colors duration-200" />
          </div>
        )}
        <input
          id={id}
          ref={ref}
          className={cn(
            'w-full rounded-xl border text-sm transition-all duration-200',
            'bg-[var(--bg-card)] border-[var(--border-color)] text-[var(--text-primary)]',
            'placeholder:text-[var(--text-muted)] font-medium',
            'focus:outline-none focus:ring-4 focus:ring-indigo-500/20 focus:border-indigo-500 shadow-sm',
            Icon ? 'pl-10 pr-4 py-2.5' : 'px-4 py-2.5',
            error && 'border-rose-500 focus:ring-rose-500/20 focus:border-rose-500',
            className
          )}
          {...props}
        />
      </div>
      {error && (
        <p className="text-xs text-rose-400 font-semibold flex items-center gap-1 mt-0.5">
          <span>•</span> {error}
        </p>
      )}
    </div>
  );
});

