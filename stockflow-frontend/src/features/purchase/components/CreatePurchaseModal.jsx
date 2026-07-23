import { useEffect, useState } from 'react';
import { useForm, useFieldArray } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { Plus, Trash2, ShoppingBag } from 'lucide-react';
import { Input } from '@/shared/components/ui/Input';
import { Button } from '@/shared/components/ui/Button';
import { FormModal } from '@/shared/components/crud/FormModal';
import { supplierService } from '@/features/supplier/services/supplierService';
import { warehouseService } from '@/features/warehouse/services/warehouseService';
import { productService } from '@/features/product/services/productService';
import { formatCurrency } from '@/shared/utils/formatters';

const purchaseItemSchema = z.object({
  productId: z.coerce.number().positive('Select product'),
  quantity: z.coerce.number().int().positive('Qty > 0'),
  unitPrice: z.coerce.number().positive('Price > 0'),
});

const purchaseSchema = z.object({
  supplierId: z.coerce.number().positive('Supplier is required'),
  warehouseId: z.coerce.number().positive('Warehouse is required'),
  invoiceNumber: z.string().optional(),
  paymentStatus: z.string().min(1, 'Select status'),
  paymentMethod: z.string().min(1, 'Select method'),
  remarks: z.string().optional(),
  items: z.array(purchaseItemSchema).min(1, 'Add at least one line item'),
});

export function CreatePurchaseModal({ isOpen, onClose, onSubmit, isLoading }) {
  const [suppliers, setSuppliers] = useState([]);
  const [warehouses, setWarehouses] = useState([]);
  const [products, setProducts] = useState([]);

  const {
    register,
    control,
    handleSubmit,
    watch,
    setValue,
    reset,
    formState: { errors },
  } = useForm({
    resolver: zodResolver(purchaseSchema),
    defaultValues: {
      supplierId: '',
      warehouseId: '',
      invoiceNumber: '',
      paymentStatus: 'PAID',
      paymentMethod: 'CASH',
      remarks: '',
      items: [{ productId: '', quantity: 1, unitPrice: '' }],
    },
  });

  const { fields, append, remove } = useFieldArray({
    control,
    name: 'items',
  });

  const watchedItems = watch('items');

  useEffect(() => {
    if (isOpen) {
      reset({
        supplierId: '',
        warehouseId: '',
        invoiceNumber: '',
        paymentStatus: 'PAID',
        paymentMethod: 'CASH',
        remarks: '',
        items: [{ productId: '', quantity: 1, unitPrice: '' }],
      });
      Promise.all([
        supplierService.getAllList(),
        warehouseService.getAllList(),
        productService.getAllList(),
      ]).then(([sups, whs, prods]) => {
        setSuppliers(sups || []);
        setWarehouses(whs || []);
        setProducts(prods || []);
      });
    }
  }, [isOpen, reset]);

  // Handle product selection to auto-prefill unit buying price
  const handleProductSelect = (index, productIdStr) => {
    const pId = Number(productIdStr);
    const prod = products.find((p) => p.id === pId);
    if (prod) {
      setValue(`items.${index}.unitPrice`, prod.buyingPrice || '');
    }
  };

  // Real-time calculation logic
  const grandTotal = watchedItems.reduce((acc, item) => {
    const qty = Number(item.quantity) || 0;
    const price = Number(item.unitPrice) || 0;
    return acc + qty * price;
  }, 0);

  // Check duplicate product selections
  const selectedProductIds = watchedItems.map((i) => String(i.productId)).filter(Boolean);
  const hasDuplicateProducts = new Set(selectedProductIds).size !== selectedProductIds.length;

  return (
    <FormModal isOpen={isOpen} onClose={onClose} title="Create Purchase Order (PO)">
      <form onSubmit={handleSubmit(onSubmit)} className="space-y-4 max-h-[75vh] overflow-y-auto pr-1">
        {/* Supplier & Warehouse Header */}
        <div className="grid grid-cols-2 gap-3">
          <div>
            <label className="text-xs font-medium text-[var(--text-secondary)]">Supplier *</label>
            <select
              className="w-full mt-1 rounded-lg border text-xs p-2.5 bg-white/5 border-[var(--border-color)] text-[var(--text-primary)] focus:outline-none focus:ring-1 focus:ring-indigo-500"
              {...register('supplierId')}
            >
              <option value="">Select Supplier</option>
              {suppliers.map((s) => (
                <option key={s.id} value={s.id} className="bg-slate-900 text-white">{s.name}</option>
              ))}
            </select>
            {errors.supplierId && <p className="text-xs text-rose-400 mt-0.5">{errors.supplierId.message}</p>}
          </div>

          <div>
            <label className="text-xs font-medium text-[var(--text-secondary)]">Destination Warehouse *</label>
            <select
              className="w-full mt-1 rounded-lg border text-xs p-2.5 bg-white/5 border-[var(--border-color)] text-[var(--text-primary)] focus:outline-none focus:ring-1 focus:ring-indigo-500"
              {...register('warehouseId')}
            >
              <option value="">Select Warehouse</option>
              {warehouses.map((w) => (
                <option key={w.id} value={w.id} className="bg-slate-900 text-white">{w.name} ({w.code})</option>
              ))}
            </select>
            {errors.warehouseId && <p className="text-xs text-rose-400 mt-0.5">{errors.warehouseId.message}</p>}
          </div>
        </div>

        {/* Payment & Invoice details */}
        <div className="grid grid-cols-3 gap-3">
          <Input label="Invoice Ref #" placeholder="e.g. INV-9901" {...register('invoiceNumber')} />
          <div>
            <label className="text-xs font-medium text-[var(--text-secondary)]">Payment Status</label>
            <select
              className="w-full mt-1.5 rounded-lg border text-xs p-2.5 bg-white/5 border-[var(--border-color)] text-[var(--text-primary)] focus:outline-none focus:ring-1 focus:ring-indigo-500"
              {...register('paymentStatus')}
            >
              <option value="PAID">PAID</option>
              <option value="PENDING">PENDING</option>
              <option value="PARTIAL">PARTIAL</option>
            </select>
          </div>
          <div>
            <label className="text-xs font-medium text-[var(--text-secondary)]">Payment Method</label>
            <select
              className="w-full mt-1.5 rounded-lg border text-xs p-2.5 bg-white/5 border-[var(--border-color)] text-[var(--text-primary)] focus:outline-none focus:ring-1 focus:ring-indigo-500"
              {...register('paymentMethod')}
            >
              <option value="CASH">CASH</option>
              <option value="BANK_TRANSFER">BANK TRANSFER</option>
              <option value="CREDIT_CARD">CREDIT CARD</option>
              <option value="CHEQUE">CHEQUE</option>
            </select>
          </div>
        </div>

        {/* Dynamic Product Line Items Section */}
        <div className="space-y-2 border-t border-[var(--border-color)] pt-3">
          <div className="flex items-center justify-between">
            <span className="text-xs font-bold text-[var(--text-primary)] flex items-center gap-1">
              <ShoppingBag size={14} /> Line Items List
            </span>
            <Button
              variant="secondary"
              size="xs"
              icon={Plus}
              type="button"
              onClick={() => append({ productId: '', quantity: 1, unitPrice: '' })}
            >
              Add Item
            </Button>
          </div>

          {hasDuplicateProducts && (
            <p className="text-xs text-rose-400 font-medium">⚠️ Duplicate products selected across lines. Please merge duplicates.</p>
          )}

          {fields.map((field, index) => {
            const currentItem = watchedItems[index] || {};
            const lineSubtotal = (Number(currentItem.quantity) || 0) * (Number(currentItem.unitPrice) || 0);

            return (
              <div key={field.id} className="grid grid-cols-12 gap-2 items-end p-2 rounded-lg bg-[var(--bg-secondary)] border border-[var(--border-color)] text-xs">
                <div className="col-span-5">
                  <label className="text-[10px] text-[var(--text-muted)]">Product</label>
                  <select
                    className="w-full mt-0.5 rounded border text-xs p-1.5 bg-white/5 border-[var(--border-color)] text-[var(--text-primary)] focus:outline-none"
                    {...register(`items.${index}.productId`)}
                    onChange={(e) => {
                      register(`items.${index}.productId`).onChange(e);
                      handleProductSelect(index, e.target.value);
                    }}
                  >
                    <option value="">Select Item</option>
                    {products.map((p) => (
                      <option key={p.id} value={p.id} className="bg-slate-900 text-white">{p.name} ({p.sku})</option>
                    ))}
                  </select>
                </div>

                <div className="col-span-2">
                  <label className="text-[10px] text-[var(--text-muted)]">Qty</label>
                  <input
                    type="number"
                    min="1"
                    className="w-full mt-0.5 rounded border text-xs p-1.5 bg-white/5 border-[var(--border-color)] text-[var(--text-primary)] focus:outline-none"
                    {...register(`items.${index}.quantity`)}
                  />
                </div>

                <div className="col-span-2">
                  <label className="text-[10px] text-[var(--text-muted)]">Unit Cost ($)</label>
                  <input
                    type="number"
                    step="0.01"
                    className="w-full mt-0.5 rounded border text-xs p-1.5 bg-white/5 border-[var(--border-color)] text-[var(--text-primary)] focus:outline-none"
                    {...register(`items.${index}.unitPrice`)}
                  />
                </div>

                <div className="col-span-2 text-right">
                  <p className="text-[10px] text-[var(--text-muted)]">Subtotal</p>
                  <p className="font-bold text-indigo-400 mt-1">{formatCurrency(lineSubtotal)}</p>
                </div>

                <div className="col-span-1 text-right">
                  {fields.length > 1 && (
                    <button
                      type="button"
                      onClick={() => remove(index)}
                      className="p-1 text-slate-400 hover:text-rose-400"
                      title="Remove Item"
                    >
                      <Trash2 size={14} />
                    </button>
                  )}
                </div>
              </div>
            );
          })}
          {errors.items && typeof errors.items.message === 'string' && (
            <p className="text-xs text-rose-400">{errors.items.message}</p>
          )}
        </div>

        {/* Calculation Grand Total Bar */}
        <div className="flex items-center justify-between p-3 rounded-xl bg-indigo-500/10 border border-indigo-500/30">
          <span className="text-xs font-bold text-indigo-300">Grand Total Payable</span>
          <span className="text-lg font-extrabold text-indigo-400 tabular-nums">{formatCurrency(grandTotal)}</span>
        </div>

        <Input label="Remarks" placeholder="Internal notes, delivery tracking details..." {...register('remarks')} />

        <div className="flex justify-end space-x-2 pt-3 border-t border-[var(--border-color)]">
          <Button variant="ghost" size="sm" type="button" onClick={onClose}>
            Cancel
          </Button>
          <Button variant="primary" size="sm" type="submit" loading={isLoading} disabled={hasDuplicateProducts}>
            Submit Purchase Order
          </Button>
        </div>
      </form>
    </FormModal>
  );
}
