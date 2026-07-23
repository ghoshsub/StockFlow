import { useEffect, useState } from 'react';
import { useForm, useFieldArray } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { Plus, Trash2, ShoppingCart, AlertCircle } from 'lucide-react';
import { Input } from '@/shared/components/ui/Input';
import { Button } from '@/shared/components/ui/Button';
import { FormModal } from '@/shared/components/crud/FormModal';
import { warehouseService } from '@/features/warehouse/services/warehouseService';
import { productService } from '@/features/product/services/productService';
import { formatCurrency } from '@/shared/utils/formatters';

const saleItemSchema = z.object({
  productId: z.coerce.number().positive('Select product'),
  quantity: z.coerce.number().int().positive('Qty > 0'),
  sellingPrice: z.coerce.number().positive('Price > 0'),
});

const saleSchema = z.object({
  customerName: z.string().min(2, 'Customer name required'),
  customerEmail: z.string().email('Invalid email').or(z.literal('')).optional(),
  customerPhone: z.string().optional(),
  warehouseId: z.coerce.number().positive('Select warehouse'),
  paymentMethod: z.string().min(1, 'Select method'),
  paymentStatus: z.string().min(1, 'Select status'),
  discount: z.coerce.number().min(0, 'Discount cannot be negative').optional(),
  tax: z.coerce.number().min(0, 'Tax cannot be negative').optional(),
  remarks: z.string().optional(),
  items: z.array(saleItemSchema).min(1, 'Add at least one item'),
});

export function CreateSaleModal({ isOpen, onClose, onSubmit, isLoading }) {
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
    resolver: zodResolver(saleSchema),
    defaultValues: {
      customerName: 'Walk-in Customer',
      customerEmail: '',
      customerPhone: '',
      warehouseId: '',
      paymentMethod: 'CASH',
      paymentStatus: 'PAID',
      discount: 0,
      tax: 0,
      remarks: '',
      items: [{ productId: '', quantity: 1, sellingPrice: '' }],
    },
  });

  const { fields, append, remove } = useFieldArray({
    control,
    name: 'items',
  });

  const watchedItems = watch('items');
  const watchedDiscount = Number(watch('discount')) || 0;
  const watchedTax = Number(watch('tax')) || 0;

  useEffect(() => {
    if (isOpen) {
      reset({
        customerName: 'Walk-in Customer',
        customerEmail: '',
        customerPhone: '',
        warehouseId: '',
        paymentMethod: 'CASH',
        paymentStatus: 'PAID',
        discount: 0,
        tax: 0,
        remarks: '',
        items: [{ productId: '', quantity: 1, sellingPrice: '' }],
      });
      Promise.all([
        warehouseService.getAllList(),
        productService.getAllList(),
      ]).then(([whs, prods]) => {
        setWarehouses(whs || []);
        setProducts(prods || []);
      });
    }
  }, [isOpen, reset]);

  // Handle product selection: auto-prefill selling price and check active/stock status
  const handleProductSelect = (index, productIdStr) => {
    const pId = Number(productIdStr);
    const prod = products.find((p) => p.id === pId);
    if (prod) {
      setValue(`items.${index}.sellingPrice`, prod.sellingPrice || '');
    }
  };

  // Calculation Logic
  const itemsSubtotal = watchedItems.reduce((acc, item) => {
    const qty = Number(item.quantity) || 0;
    const price = Number(item.sellingPrice) || 0;
    return acc + qty * price;
  }, 0);

  const grandTotal = Math.max(0, itemsSubtotal - watchedDiscount + watchedTax);

  // Validation Checks
  const selectedProductIds = watchedItems.map((i) => String(i.productId)).filter(Boolean);
  const hasDuplicateProducts = new Set(selectedProductIds).size !== selectedProductIds.length;

  // Real-time stock validation check
  const stockErrors = watchedItems.map((item) => {
    if (!item.productId) return null;
    const prod = products.find((p) => p.id === Number(item.productId));
    if (!prod) return null;
    if (prod.active === false) return `Product "${prod.name}" is inactive`;
    if ((Number(item.quantity) || 0) > prod.quantity) {
      return `Insufficient stock for "${prod.name}" (Available: ${prod.quantity})`;
    }
    return null;
  }).filter(Boolean);

  const hasStockErrors = stockErrors.length > 0;

  return (
    <FormModal isOpen={isOpen} onClose={onClose} title="Create Sales Order (SO)">
      <form onSubmit={handleSubmit(onSubmit)} className="space-y-4 max-h-[75vh] overflow-y-auto pr-1">
        {/* Customer Information */}
        <div className="p-3 rounded-xl border border-[var(--border-color)] bg-[var(--bg-secondary)] space-y-3">
          <p className="text-xs font-bold text-[var(--text-primary)]">Customer Details</p>
          <div className="grid grid-cols-3 gap-3">
            <Input label="Customer Name *" placeholder="Walk-in Customer" error={errors.customerName?.message} {...register('customerName')} />
            <Input label="Customer Email" type="email" placeholder="customer@email.com" error={errors.customerEmail?.message} {...register('customerEmail')} />
            <Input label="Customer Phone" placeholder="+1 (555) 000-0000" {...register('customerPhone')} />
          </div>
        </div>

        {/* Warehouse & Payment Method */}
        <div className="grid grid-cols-3 gap-3">
          <div>
            <label className="text-xs font-medium text-[var(--text-secondary)]">Fulfillment Warehouse *</label>
            <select
              className="w-full mt-1.5 rounded-lg border text-xs p-2.5 bg-white/5 border-[var(--border-color)] text-[var(--text-primary)] focus:outline-none focus:ring-1 focus:ring-indigo-500"
              {...register('warehouseId')}
            >
              <option value="">Select Warehouse</option>
              {warehouses.map((w) => (
                <option key={w.id} value={w.id} className="bg-slate-900 text-white">{w.name}</option>
              ))}
            </select>
            {errors.warehouseId && <p className="text-xs text-rose-400 mt-0.5">{errors.warehouseId.message}</p>}
          </div>

          <div>
            <label className="text-xs font-medium text-[var(--text-secondary)]">Payment Method</label>
            <select
              className="w-full mt-1.5 rounded-lg border text-xs p-2.5 bg-white/5 border-[var(--border-color)] text-[var(--text-primary)] focus:outline-none focus:ring-1 focus:ring-indigo-500"
              {...register('paymentMethod')}
            >
              <option value="CASH">CASH</option>
              <option value="CREDIT_CARD">CREDIT CARD</option>
              <option value="BANK_TRANSFER">BANK TRANSFER</option>
              <option value="UPI">UPI / WALLET</option>
            </select>
          </div>

          <div>
            <label className="text-xs font-medium text-[var(--text-secondary)]">Payment Status</label>
            <select
              className="w-full mt-1.5 rounded-lg border text-xs p-2.5 bg-white/5 border-[var(--border-color)] text-[var(--text-primary)] focus:outline-none focus:ring-1 focus:ring-indigo-500"
              {...register('paymentStatus')}
            >
              <option value="PAID">PAID</option>
              <option value="PENDING">PENDING</option>
            </select>
          </div>
        </div>

        {/* Dynamic Line Items */}
        <div className="space-y-2 border-t border-[var(--border-color)] pt-3">
          <div className="flex items-center justify-between">
            <span className="text-xs font-bold text-[var(--text-primary)] flex items-center gap-1">
              <ShoppingCart size={14} /> Sales Items
            </span>
            <Button
              variant="secondary"
              size="xs"
              icon={Plus}
              type="button"
              onClick={() => append({ productId: '', quantity: 1, sellingPrice: '' })}
            >
              Add Product
            </Button>
          </div>

          {hasDuplicateProducts && (
            <p className="text-xs text-rose-400 font-medium">⚠️ Duplicate products selected across lines.</p>
          )}

          {stockErrors.map((errStr, i) => (
            <div key={i} className="flex items-center space-x-1.5 text-xs text-rose-400 bg-rose-500/10 p-2 rounded-lg border border-rose-500/20">
              <AlertCircle size={14} />
              <span>{errStr}</span>
            </div>
          ))}

          {fields.map((field, index) => {
            const currentItem = watchedItems[index] || {};
            const prod = products.find((p) => p.id === Number(currentItem.productId));
            const lineSubtotal = (Number(currentItem.quantity) || 0) * (Number(currentItem.sellingPrice) || 0);

            return (
              <div key={field.id} className="grid grid-cols-12 gap-2 items-end p-2 rounded-lg bg-[var(--bg-secondary)] border border-[var(--border-color)] text-xs">
                <div className="col-span-5">
                  <label className="text-[10px] text-[var(--text-muted)] flex justify-between">
                    <span>Product</span>
                    {prod && (
                      <span className={`font-mono ${prod.quantity > 0 ? 'text-emerald-400' : 'text-rose-400'}`}>
                        Stock: {prod.quantity}
                      </span>
                    )}
                  </label>
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
                      <option key={p.id} value={p.id} disabled={!p.active} className="bg-slate-900 text-white">
                        {p.name} {!p.active ? '(Inactive)' : `(Avail: ${p.quantity})`}
                      </option>
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
                  <label className="text-[10px] text-[var(--text-muted)]">Price ($)</label>
                  <input
                    type="number"
                    step="0.01"
                    className="w-full mt-0.5 rounded border text-xs p-1.5 bg-white/5 border-[var(--border-color)] text-[var(--text-primary)] focus:outline-none"
                    {...register(`items.${index}.sellingPrice`)}
                  />
                </div>

                <div className="col-span-2 text-right">
                  <p className="text-[10px] text-[var(--text-muted)]">Subtotal</p>
                  <p className="font-bold text-emerald-400 mt-1">{formatCurrency(lineSubtotal)}</p>
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
        </div>

        {/* Discount & Tax Adjustment Row */}
        <div className="grid grid-cols-2 gap-3 pt-2">
          <Input label="Discount ($)" type="number" step="0.01" placeholder="0.00" {...register('discount')} />
          <Input label="Tax ($)" type="number" step="0.01" placeholder="0.00" {...register('tax')} />
        </div>

        {/* Real-Time Calculation Grand Total Bar */}
        <div className="flex items-center justify-between p-3 rounded-xl bg-emerald-500/10 border border-emerald-500/30">
          <div>
            <span className="text-xs font-bold text-emerald-300">Grand Total Receivable</span>
            <p className="text-[10px] text-[var(--text-muted)]">Items Subtotal: {formatCurrency(itemsSubtotal)}</p>
          </div>
          <span className="text-xl font-extrabold text-emerald-400 tabular-nums">{formatCurrency(grandTotal)}</span>
        </div>

        <Input label="Remarks" placeholder="Customer notes or invoice remarks..." {...register('remarks')} />

        <div className="flex justify-end space-x-2 pt-3 border-t border-[var(--border-color)]">
          <Button variant="ghost" size="sm" type="button" onClick={onClose}>
            Cancel
          </Button>
          <Button
            variant="primary"
            size="sm"
            type="submit"
            loading={isLoading}
            disabled={hasDuplicateProducts || hasStockErrors}
          >
            Process Sales Order
          </Button>
        </div>
      </form>
    </FormModal>
  );
}
