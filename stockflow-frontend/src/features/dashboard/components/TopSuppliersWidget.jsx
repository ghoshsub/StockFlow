import { Card } from '@/shared/components/ui/Card';
import { SkeletonTable } from '@/shared/components/skeleton/SkeletonTable';
import { EmptyState } from '@/shared/components/EmptyState';
import { formatCurrency } from '@/shared/utils/formatters';

/**
 * TopSuppliersWidget — List displaying highest volume suppliers.
 */
export function TopSuppliersWidget({ suppliers = [], isLoading }) {
  return (
    <Card title="Top Suppliers" subtitle="Suppliers with highest purchase volume">
      {isLoading ? (
        <SkeletonTable rows={4} cols={2} />
      ) : suppliers.length === 0 ? (
        <EmptyState title="No Suppliers Data" description="No supplier records recorded yet." />
      ) : (
        <div className="space-y-3">
          {suppliers.map((s, idx) => (
            <div key={idx} className="flex items-center justify-between text-xs py-1 border-b border-[var(--border-color)] last:border-0">
              <span className="font-medium text-[var(--text-primary)] truncate max-w-[200px]">{s.label}</span>
              <span className="font-bold text-emerald-400">{formatCurrency(s.value)}</span>
            </div>
          ))}
        </div>
      )}
    </Card>
  );
}
