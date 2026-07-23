import { Printer, Layers } from 'lucide-react';
import { FormModal } from '@/shared/components/crud/FormModal';
import { Button } from '@/shared/components/ui/Button';
import { Badge, statusToBadgeVariant } from '@/shared/components/ui/Badge';
import { formatCurrency, formatDateTime } from '@/shared/utils/formatters';

export function InvoicePreviewModal({ isOpen, onClose, sale }) {
  if (!isOpen || !sale) return null;

  const items = sale.items || [];
  const itemsSubtotal = items.reduce((acc, i) => acc + (i.subtotal || i.quantity * i.sellingPrice || 0), 0);
  const discount = Number(sale.discount) || 0;
  const tax = Number(sale.tax) || 0;
  const finalTotal = sale.totalAmount ?? itemsSubtotal - discount + tax;

  const handlePrint = () => {
    window.print();
  };

  return (
    <FormModal isOpen={isOpen} onClose={onClose} title={`Tax Invoice — ${sale.saleNumber || ''}`}>
      <div className="space-y-4 max-h-[75vh] overflow-y-auto pr-1 text-xs">
        {/* Printable Header Container */}
        <div id="printable-invoice" className="p-4 rounded-xl border border-[var(--border-color)] bg-white/5 space-y-4">
          {/* Company & Invoice Header */}
          <div className="flex justify-between items-start border-b border-[var(--border-color)] pb-3">
            <div>
              <div className="flex items-center space-x-2 text-indigo-400 font-extrabold text-base">
                <Layers size={22} />
                <span>StockFlow Inc.</span>
              </div>
              <p className="text-[10px] text-[var(--text-muted)] mt-0.5">Enterprise Inventory & Distribution HQ</p>
              <p className="text-[10px] text-[var(--text-muted)]">support@stockflow.com | +1 (800) 555-FLOW</p>
            </div>
            <div className="text-right">
              <span className="text-base font-extrabold font-mono text-indigo-400">{sale.saleNumber}</span>
              <p className="text-[10px] text-[var(--text-muted)]">{formatDateTime(sale.saleDate)}</p>
              <div className="mt-1">
                <Badge variant={statusToBadgeVariant(sale.paymentStatus)}>
                  {sale.paymentStatus || 'PAID'}
                </Badge>
              </div>
            </div>
          </div>

          {/* Customer & Warehouse Grid */}
          <div className="grid grid-cols-2 gap-4 p-3 rounded-lg bg-[var(--bg-secondary)] border border-[var(--border-color)]">
            <div>
              <p className="text-[10px] font-bold text-[var(--text-muted)] uppercase tracking-wider">Billed To:</p>
              <p className="font-bold text-[var(--text-primary)] text-sm">{sale.customerName || 'Walk-in Customer'}</p>
              {sale.customerEmail && <p className="text-[var(--text-secondary)]">{sale.customerEmail}</p>}
              {sale.customerPhone && <p className="text-[var(--text-secondary)]">{sale.customerPhone}</p>}
            </div>
            <div>
              <p className="text-[10px] font-bold text-[var(--text-muted)] uppercase tracking-wider">Fulfillment Origin:</p>
              <p className="font-bold text-[var(--text-primary)]">{sale.warehouseName || 'Main Warehouse'}</p>
              <p className="text-[var(--text-secondary)]">Payment Method: <strong className="text-[var(--text-primary)]">{sale.paymentMethod || 'CASH'}</strong></p>
            </div>
          </div>

          {/* Line Items Table */}
          <div className="overflow-x-auto rounded-lg border border-[var(--border-color)]">
            <table className="w-full text-left">
              <thead className="bg-[var(--bg-secondary)] border-b border-[var(--border-color)] text-[var(--text-muted)]">
                <tr>
                  <th className="p-2 font-semibold">SKU</th>
                  <th className="p-2 font-semibold">Item Description</th>
                  <th className="p-2 font-semibold text-right">Qty</th>
                  <th className="p-2 font-semibold text-right">Unit Price</th>
                  <th className="p-2 font-semibold text-right">Subtotal</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-[var(--border-color)]">
                {items.map((item, idx) => (
                  <tr key={idx}>
                    <td className="p-2 font-mono text-indigo-400">{item.productSku || '—'}</td>
                    <td className="p-2 font-semibold text-[var(--text-primary)]">{item.productName}</td>
                    <td className="p-2 text-right font-bold">{item.quantity}</td>
                    <td className="p-2 text-right">{formatCurrency(item.sellingPrice)}</td>
                    <td className="p-2 text-right font-bold text-emerald-400">{formatCurrency(item.subtotal)}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>

          {/* Financial Breakdown Summary */}
          <div className="flex justify-end">
            <div className="w-64 space-y-1.5 p-3 rounded-lg bg-[var(--bg-secondary)] border border-[var(--border-color)] text-right">
              <div className="flex justify-between text-[var(--text-secondary)]">
                <span>Items Subtotal:</span>
                <span className="font-medium">{formatCurrency(itemsSubtotal)}</span>
              </div>
              {discount > 0 && (
                <div className="flex justify-between text-rose-400">
                  <span>Discount:</span>
                  <span>- {formatCurrency(discount)}</span>
                </div>
              )}
              {tax > 0 && (
                <div className="flex justify-between text-sky-400">
                  <span>Tax (VAT):</span>
                  <span>+ {formatCurrency(tax)}</span>
                </div>
              )}
              <div className="flex justify-between text-sm font-extrabold text-emerald-400 border-t border-[var(--border-color)] pt-1.5">
                <span>Grand Total:</span>
                <span>{formatCurrency(finalTotal)}</span>
              </div>
            </div>
          </div>

          {sale.remarks && (
            <p className="text-[11px] text-[var(--text-muted)] italic">Remarks: {sale.remarks}</p>
          )}
        </div>

        {/* Action Controls */}
        <div className="flex justify-between items-center pt-2">
          <Button variant="ghost" size="sm" icon={Printer} onClick={handlePrint}>
            Print Invoice
          </Button>
          <Button variant="primary" size="sm" onClick={onClose}>
            Close
          </Button>
        </div>
      </div>
    </FormModal>
  );
}
