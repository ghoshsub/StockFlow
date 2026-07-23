import { Card } from '@/shared/components/ui/Card';
import { SkeletonTable } from '@/shared/components/skeleton/SkeletonTable';
import { EmptyState } from '@/shared/components/EmptyState';

export function RecentPurchasesWidget({ purchases = [], isLoading }) {
  return (
    <Card title="Recent Purchases" subtitle="Latest procurement orders">
      {isLoading ? (
        <SkeletonTable rows={4} cols={3} />
      ) : purchases.length === 0 ? (
        <EmptyState title="No Purchases" description="No recent purchase transactions." />
      ) : (
        <div className="space-y-2">
          {purchases.slice(0, 4).map((p, idx) => (
            <div key={idx} className="flex justify-between text-xs py-1.5 border-b border-[var(--border-color)] last:border-0">
              <div>
                <p className="font-semibold text-[var(--text-primary)]">{p.purchaseNumber || `PO-#${idx + 1}`}</p>
                <p className="text-[var(--text-muted)] text-[10px]">{p.supplierName || 'Supplier'}</p>
              </div>
              <span className="font-medium text-indigo-400">${p.totalAmount || 0}</span>
            </div>
          ))}
        </div>
      )}
    </Card>
  );
}
