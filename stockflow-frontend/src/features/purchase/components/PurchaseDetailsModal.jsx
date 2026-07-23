import { FormModal } from '@/shared/components/crud/FormModal';
import { Badge, statusToBadgeVariant } from '@/shared/components/ui/Badge';
import { formatCurrency, formatDateTime } from '@/shared/utils/formatters';

export function PurchaseDetailsModal({ isOpen, onClose, purchase }) {
  if (!isOpen || !purchase) return null;

  return (
    <FormModal isOpen={isOpen} onClose={onClose} title={`Purchase Order ${purchase.purchaseNumber || ''}`}>
      <div className="space-y-4 max-h-[75vh] overflow-y-auto pr-1">
        {/* Header Metadata */}
        <div className="grid grid-cols-2 gap-3 text-xs p-3 rounded-xl bg-slate-800/30 border border-[var(--border-color)]">
          <div>
            <p className="text-[var(--text-muted)]">Supplier</p>
            <p className="font-bold text-[var(--text-primary)]">{purchase.supplierName || '—'}</p>
          </div>
          <div>
            <p className="text-[var(--text-muted)]">Destination Warehouse</p>
            <p className="font-bold text-[var(--text-primary)]">{purchase.warehouseName || '—'}</p>
          </div>
          <div>
            <p className="text-[var(--text-muted)]">Purchase Date</p>
            <p className="font-medium text-[var(--text-primary)]">{formatDateTime(purchase.purchaseDate)}</p>
          </div>
          <div>
            <p className="text-[var(--text-muted)]">Status</p>
            <Badge variant={statusToBadgeVariant(purchase.paymentStatus)}>
              {purchase.paymentStatus || 'PAID'}
            </Badge>
          </div>
        </div>

        {/* Line Items Table */}
        <div>
          <h4 className="text-xs font-bold text-[var(--text-primary)] mb-2">Purchased Items Breakdown</h4>
          <div className="overflow-x-auto rounded-lg border border-[var(--border-color)]">
            <table className="w-full text-left text-xs">
              <thead className="bg-[var(--bg-secondary)] border-b border-[var(--border-color)] text-[var(--text-muted)]">
                <tr>
                  <th className="p-2 font-medium">SKU</th>
                  <th className="p-2 font-medium">Product</th>
                  <th className="p-2 font-medium text-right">Qty</th>
                  <th className="p-2 font-medium text-right">Unit Cost</th>
                  <th className="p-2 font-medium text-right">Subtotal</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-[var(--border-color)]">
                {(purchase.items || []).map((item, idx) => (
                  <tr key={idx}>
                    <td className="p-2 font-mono text-indigo-400">{item.productSku || '—'}</td>
                    <td className="p-2 font-semibold text-[var(--text-primary)]">{item.productName}</td>
                    <td className="p-2 text-right font-bold">{item.quantity}</td>
                    <td className="p-2 text-right">{formatCurrency(item.unitPrice)}</td>
                    <td className="p-2 text-right font-bold text-indigo-400">{formatCurrency(item.subtotal)}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>

        {/* Grand Total Summary */}
        <div className="flex items-center justify-between p-3 rounded-xl bg-indigo-500/10 border border-indigo-500/30">
          <span className="text-xs font-bold text-indigo-300">Total Expenditure</span>
          <span className="text-lg font-extrabold text-indigo-400 tabular-nums">{formatCurrency(purchase.totalAmount)}</span>
        </div>

        {purchase.remarks && (
          <p className="text-xs text-[var(--text-muted)] italic">Remarks: {purchase.remarks}</p>
        )}
      </div>
    </FormModal>
  );
}
