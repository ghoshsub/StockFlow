import { Card } from '@/shared/components/ui/Card';
import { SkeletonTable } from '@/shared/components/skeleton/SkeletonTable';
import { EmptyState } from '@/shared/components/EmptyState';

export function RecentSalesWidget({ sales = [], isLoading }) {
  return (
    <Card title="Recent Sales" subtitle="Latest outbound sales">
      {isLoading ? (
        <SkeletonTable rows={4} cols={3} />
      ) : sales.length === 0 ? (
        <EmptyState title="No Sales" description="No recent sales orders recorded." />
      ) : (
        <div className="space-y-2">
          {sales.slice(0, 4).map((s, idx) => (
            <div key={idx} className="flex justify-between text-xs py-1.5 border-b border-[var(--border-color)] last:border-0">
              <div>
                <p className="font-semibold text-[var(--text-primary)]">{s.saleNumber || `SO-#${idx + 1}`}</p>
                <p className="text-[var(--text-muted)] text-[10px]">{s.customerName || 'Walk-in Customer'}</p>
              </div>
              <span className="font-medium text-emerald-400">${s.totalAmount || 0}</span>
            </div>
          ))}
        </div>
      )}
    </Card>
  );
}
