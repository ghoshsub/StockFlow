import { Card } from '@/shared/components/ui/Card';
import { Badge, statusToBadgeVariant } from '@/shared/components/ui/Badge';
import { SkeletonTable } from '@/shared/components/skeleton/SkeletonTable';
import { EmptyState } from '@/shared/components/EmptyState';
import { formatCurrency } from '@/shared/utils/formatters';

/**
 * LowStockWidget — Table displaying items running out of stock.
 */
export function LowStockWidget({ products = [], isLoading }) {
  return (
    <Card title="Low Stock Alert" subtitle="Products requiring immediate replenishment">
      {isLoading ? (
        <SkeletonTable rows={4} cols={4} />
      ) : products.length === 0 ? (
        <EmptyState title="Stock Level Optimal" description="No products are currently low or out of stock." />
      ) : (
        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs">
            <thead>
              <tr className="border-b border-[var(--border-color)] text-[var(--text-muted)]">
                <th className="py-2.5 font-medium">Product</th>
                <th className="py-2.5 font-medium">SKU</th>
                <th className="py-2.5 font-medium text-right">Qty</th>
                <th className="py-2.5 font-medium text-right">Status</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-[var(--border-color)]">
              {products.slice(0, 5).map((p) => (
                <tr key={p.id} className="hover:bg-slate-800/20">
                  <td className="py-2.5 font-medium text-[var(--text-primary)]">{p.name}</td>
                  <td className="py-2.5 text-[var(--text-muted)] font-mono">{p.sku}</td>
                  <td className="py-2.5 text-right font-bold text-[var(--text-primary)]">{p.quantity}</td>
                  <td className="py-2.5 text-right">
                    <Badge variant={p.quantity === 0 ? 'danger' : 'warning'}>
                      {p.quantity === 0 ? 'OUT OF STOCK' : 'LOW STOCK'}
                    </Badge>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </Card>
  );
}
