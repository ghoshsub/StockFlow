import { cn } from '@/shared/utils/cn';
import { Loader2 } from 'lucide-react';

/**
 * Spinner — Animated loading spinner.
 * Sizes: sm | md | lg
 */
const sizes = { sm: 16, md: 24, lg: 40 };

export function Spinner({ size = 'md', className }) {
  return (
    <Loader2
      size={sizes[size]}
      className={cn('animate-spin text-indigo-400', className)}
    />
  );
}

/**
 * FullPageSpinner — Centered full-screen loading state.
 */
export function FullPageSpinner({ message = 'Loading...' }) {
  return (
    <div className="flex flex-col items-center justify-center min-h-screen gap-3">
      <Spinner size="lg" />
      <p className="text-sm text-[var(--text-muted)]">{message}</p>
    </div>
  );
}
