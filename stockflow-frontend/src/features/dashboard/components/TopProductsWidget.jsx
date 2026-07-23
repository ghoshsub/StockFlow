import { Card } from '@/shared/components/ui/Card';
import { SkeletonTable } from '@/shared/components/skeleton/SkeletonTable';
import { EmptyState } from '@/shared/components/EmptyState';
import { formatNumber } from '@/shared/utils/formatters';

/**
 * TopProductsWidget — List displaying top selling items.
 */
export function TopProductsWidget({ items = [], isLoading }) {
  return (
    <Card title="Top Selling Products" subtitle="Most frequently purchased items">
      {isLoading ? (
        <SkeletonTable rows={4} cols={2} />
      ) : items.length === 0 ? (
        <EmptyState title="No Sales Data" description="No top products tracked yet." />
      ) : (
        <div className="space-y-3">
          {items.map((item, idx) => (
            <div key={idx} className="flex items-center justify-between text-xs py-1 border-b border-[var(--border-color)] last:border-0">
              <span className="font-medium text-[var(--text-primary)] truncate max-w-[200px]">{item.label}</span>
              <span className="font-bold text-indigo-400">{formatNumber(item.value)} sold</span>
            </div>
          ))}
        </div>
      )}
    </Card>
  );
}
