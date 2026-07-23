import { cn } from '@/shared/utils/cn';
import { Loader2 } from 'lucide-react';

/**
 * Button — Reusable glass-styled button component with press scale animations.
 *
 * Variants: primary | secondary | danger | success | ghost | outline
 * Sizes:    xs | sm | md | lg
 */
const variants = {
  primary:   'bg-gradient-to-r from-indigo-600 via-indigo-500 to-purple-600 text-white hover:from-indigo-500 hover:to-purple-500 shadow-md shadow-indigo-500/20 active:scale-[0.98]',
  secondary: 'bg-slate-800 text-slate-100 hover:bg-slate-700 border border-slate-700 shadow-sm active:scale-[0.98]',
  danger:    'bg-gradient-to-r from-rose-600 to-pink-600 text-white hover:from-rose-500 hover:to-pink-500 shadow-md shadow-rose-500/20 active:scale-[0.98]',
  success:   'bg-gradient-to-r from-emerald-600 to-teal-600 text-white hover:from-emerald-500 hover:to-teal-500 shadow-md shadow-emerald-500/20 active:scale-[0.98]',
  ghost:     'text-slate-400 hover:text-indigo-400 hover:bg-indigo-500/10 active:scale-[0.98]',
  outline:   'border border-[var(--border-color)] text-[var(--text-primary)] hover:bg-indigo-500/10 hover:border-indigo-500/50 active:scale-[0.98]',
};

const sizes = {
  xs: 'px-2.5 py-1 text-xs gap-1 rounded-md font-semibold',
  sm: 'px-3.5 py-1.5 text-sm gap-1.5 rounded-lg font-semibold',
  md: 'px-4.5 py-2.5 text-sm gap-2 rounded-xl font-bold',
  lg: 'px-6 py-3 text-base gap-2.5 rounded-xl font-bold',
};

export function Button({
  children,
  variant = 'primary',
  size = 'md',
  loading = false,
  disabled = false,
  icon: Icon,
  className,
  type = 'button',
  ...props
}) {
  return (
    <button
      type={type}
      disabled={disabled || loading}
      className={cn(
        'inline-flex items-center justify-center transition-all duration-200 cursor-pointer select-none font-heading',
        'focus:outline-none focus:ring-2 focus:ring-indigo-500/40 focus:ring-offset-2 focus:ring-offset-slate-900',
        'disabled:opacity-50 disabled:cursor-not-allowed disabled:transform-none',
        variants[variant],
        sizes[size],
        className
      )}
      {...props}
    >
      {loading ? (
        <Loader2 className="animate-spin" size={size === 'xs' ? 12 : size === 'sm' ? 14 : 18} />
      ) : Icon ? (
        <Icon size={size === 'xs' ? 12 : size === 'sm' ? 14 : 18} />
      ) : null}
      {children}
    </button>
  );
}

