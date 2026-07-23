import { Package, Tag, Building2, Truck, Warehouse, DollarSign, Barcode, ShieldAlert } from 'lucide-react';
import { FormModal } from '@/shared/components/crud/FormModal';
import { Badge } from '@/shared/components/ui/Badge';
import { formatCurrency, formatDate } from '@/shared/utils/formatters';

export function ProductDetailsModal({ isOpen, onClose, product }) {
  if (!isOpen || !product) return null;

  const margin = product.sellingPrice && product.buyingPrice
    ? (((product.sellingPrice - product.buyingPrice) / product.sellingPrice) * 100).toFixed(1)
    : 0;

  return (
    <FormModal isOpen={isOpen} onClose={onClose} title="Product Catalog Specifications">
      <div className="space-y-4 max-h-[75vh] overflow-y-auto pr-1">
        {/* Header Hero */}
        <div className="flex items-center space-x-4 p-3 rounded-xl bg-slate-800/40 border border-[var(--border-color)]">
          <div className="w-16 h-16 rounded-lg bg-indigo-500/10 border border-indigo-500/30 flex items-center justify-center shrink-0 overflow-hidden">
            {product.imageUrl ? (
              <img src={product.imageUrl} alt={product.name} className="w-full h-full object-cover" />
            ) : (
              <Package size={28} className="text-indigo-400" />
            )}
          </div>
          <div>
            <h2 className="text-base font-bold text-[var(--text-primary)]">{product.name}</h2>
            <div className="flex items-center space-x-2 mt-1">
              <span className="font-mono text-xs text-indigo-400 font-medium">SKU: {product.sku || 'N/A'}</span>
              <Badge variant={product.quantity <= (product.minimumStock || 5) ? 'warning' : 'success'}>
                {product.quantity <= 0 ? 'OUT OF STOCK' : product.quantity <= (product.minimumStock || 5) ? 'LOW STOCK' : 'IN STOCK'}
              </Badge>
            </div>
          </div>
        </div>

        {/* Specifications Grid */}
        <div className="grid grid-cols-2 gap-3 text-xs">
          <div className="p-3 rounded-lg border border-[var(--border-color)] bg-[var(--bg-secondary)]">
            <span className="text-[var(--text-muted)] flex items-center gap-1 mb-1">
              <Tag size={12} /> Category
            </span>
            <p className="font-semibold text-[var(--text-primary)]">{product.categoryName || '—'}</p>
          </div>

          <div className="p-3 rounded-lg border border-[var(--border-color)] bg-[var(--bg-secondary)]">
            <span className="text-[var(--text-muted)] flex items-center gap-1 mb-1">
              <Building2 size={12} /> Brand
            </span>
            <p className="font-semibold text-[var(--text-primary)]">{product.brandName || '—'}</p>
          </div>

          <div className="p-3 rounded-lg border border-[var(--border-color)] bg-[var(--bg-secondary)]">
            <span className="text-[var(--text-muted)] flex items-center gap-1 mb-1">
              <Truck size={12} /> Supplier
            </span>
            <p className="font-semibold text-[var(--text-primary)]">{product.supplierName || '—'}</p>
          </div>

          <div className="p-3 rounded-lg border border-[var(--border-color)] bg-[var(--bg-secondary)]">
            <span className="text-[var(--text-muted)] flex items-center gap-1 mb-1">
              <Warehouse size={12} /> Warehouse
            </span>
            <p className="font-semibold text-[var(--text-primary)]">{product.warehouseName || '—'}</p>
          </div>
        </div>

        {/* Financial Specs */}
        <div className="p-3 rounded-xl border border-indigo-500/20 bg-indigo-500/5 space-y-2">
          <p className="text-xs font-semibold text-indigo-400 flex items-center gap-1">
            <DollarSign size={14} /> Financial Valuation & Profit Margins
          </p>
          <div className="grid grid-cols-3 gap-2 text-center text-xs">
            <div>
              <p className="text-[var(--text-muted)] text-[10px]">Cost Price</p>
              <p className="font-bold text-[var(--text-primary)]">{formatCurrency(product.buyingPrice)}</p>
            </div>
            <div>
              <p className="text-[var(--text-muted)] text-[10px]">Retail Price</p>
              <p className="font-bold text-emerald-400">{formatCurrency(product.sellingPrice)}</p>
            </div>
            <div>
              <p className="text-[var(--text-muted)] text-[10px]">Profit Margin</p>
              <p className="font-bold text-indigo-400">+{margin}%</p>
            </div>
          </div>
        </div>

        {/* Stock & Barcode info */}
        <div className="grid grid-cols-2 gap-3 text-xs">
          <div className="p-2.5 rounded-lg border border-[var(--border-color)] flex justify-between items-center">
            <span className="text-[var(--text-muted)] flex items-center gap-1">
              <Barcode size={14} /> Barcode
            </span>
            <span className="font-mono font-medium text-[var(--text-primary)]">{product.barcode || '—'}</span>
          </div>
          <div className="p-2.5 rounded-lg border border-[var(--border-color)] flex justify-between items-center">
            <span className="text-[var(--text-muted)] flex items-center gap-1">
              <ShieldAlert size={14} /> Min Threshold
            </span>
            <span className="font-bold text-amber-400">{product.minimumStock || 5} units</span>
          </div>
        </div>

        {/* Timestamp */}
        <div className="text-[10px] text-[var(--text-muted)] text-right pt-2 border-t border-[var(--border-color)]">
          Added on: {formatDate(product.createdAt)}
        </div>
      </div>
    </FormModal>
  );
}
