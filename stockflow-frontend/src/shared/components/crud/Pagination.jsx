import { ChevronLeft, ChevronRight } from 'lucide-react';
import { Button } from '@/shared/components/ui/Button';

/**
 * Pagination — Reusable pagination component.
 */
export function Pagination({ page = 0, size = 10, totalPages = 0, totalElements = 0, onPageChange, onSizeChange }) {
  if (totalPages <= 0) return null;

  const start = page * size + 1;
  const end = Math.min((page + 1) * size, totalElements);

  return (
    <div className="flex flex-col sm:flex-row items-center justify-between gap-4 py-3 px-4 border-t border-[var(--border-color)] text-xs text-[var(--text-secondary)]">
      <div className="flex items-center space-x-2">
        <span>Rows per page:</span>
        <select
          value={size}
          onChange={(e) => onSizeChange?.(Number(e.target.value))}
          className="bg-[var(--bg-secondary)] border border-[var(--border-color)] text-[var(--text-primary)] rounded px-2 py-1 focus:outline-none focus:ring-1 focus:ring-indigo-500"
        >
          <option value={5}>5</option>
          <option value={10}>10</option>
          <option value={20}>20</option>
          <option value={50}>50</option>
        </select>
        <span>
          Showing <strong className="text-[var(--text-primary)]">{start}</strong>-
          <strong className="text-[var(--text-primary)]">{end}</strong> of{' '}
          <strong className="text-[var(--text-primary)]">{totalElements}</strong>
        </span>
      </div>

      <div className="flex items-center space-x-1">
        <Button
          variant="ghost"
          size="xs"
          disabled={page === 0}
          onClick={() => onPageChange?.(page - 1)}
          icon={ChevronLeft}
        >
          Prev
        </Button>
        <span className="px-2 py-1 text-xs">
          Page <strong>{page + 1}</strong> of <strong>{totalPages}</strong>
        </span>
        <Button
          variant="ghost"
          size="xs"
          disabled={page >= totalPages - 1}
          onClick={() => onPageChange?.(page + 1)}
          icon={ChevronRight}
        >
          Next
        </Button>
      </div>
    </div>
  );
}
