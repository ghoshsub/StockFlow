import { useEffect, useState } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { Input } from '@/shared/components/ui/Input';
import { Button } from '@/shared/components/ui/Button';
import { FormModal } from '@/shared/components/crud/FormModal';
import { productService } from '@/features/product/services/productService';
import { warehouseService } from '@/features/warehouse/services/warehouseService';

const movementSchema = z.object({
  productId: z.coerce.number().positive('Please select a product'),
  warehouseId: z.coerce.number().positive('Please select a warehouse'),
  quantity: z.coerce.number().int().positive('Quantity must be greater than zero'),
  referenceNumber: z.string().optional(),
  notes: z.string().optional(),
});

export function StockMovementModal({ isOpen, onClose, type = 'IN', onSubmit, isLoading }) {
  const [products, setProducts] = useState([]);
  const [warehouses, setWarehouses] = useState([]);

  const isStockIn = type === 'IN';
  const isAdjustment = type === 'ADJUSTMENT';

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm({
    resolver: zodResolver(movementSchema),
    defaultValues: { productId: '', warehouseId: '', quantity: '', referenceNumber: '', notes: '' },
  });

  useEffect(() => {
    if (isOpen) {
      reset();
      Promise.all([productService.getAllList(), warehouseService.getAllList()])
        .then(([prods, whs]) => {
          setProducts(prods || []);
          setWarehouses(whs || []);
        })
        .catch((err) => console.error('Failed to load products/warehouses', err));
    }
  }, [isOpen, reset]);

  const title = isStockIn ? 'Stock In (Add Stock)' : isAdjustment ? 'Stock Adjustment' : 'Stock Out (Remove Stock)';

  return (
    <FormModal isOpen={isOpen} onClose={onClose} title={title}>
      <form onSubmit={handleSubmit(onSubmit)} className="space-y-3">
        <div>
          <label className="text-xs font-medium text-[var(--text-secondary)]">Target Product *</label>
          <select
            className="w-full mt-1.5 rounded-lg border text-xs p-2.5 bg-white/5 border-[var(--border-color)] text-[var(--text-primary)] focus:outline-none focus:ring-1 focus:ring-indigo-500"
            {...register('productId')}
          >
            <option value="">Select Product</option>
            {products.map((p) => (
              <option key={p.id} value={p.id} className="bg-slate-900 text-white">
                {p.name} (Current Stock: {p.quantity})
              </option>
            ))}
          </select>
          {errors.productId && <p className="text-xs text-rose-400 mt-1">{errors.productId.message}</p>}
        </div>

        <div>
          <label className="text-xs font-medium text-[var(--text-secondary)]">Warehouse *</label>
          <select
            className="w-full mt-1.5 rounded-lg border text-xs p-2.5 bg-white/5 border-[var(--border-color)] text-[var(--text-primary)] focus:outline-none focus:ring-1 focus:ring-indigo-500"
            {...register('warehouseId')}
          >
            <option value="">Select Warehouse</option>
            {warehouses.map((w) => (
              <option key={w.id} value={w.id} className="bg-slate-900 text-white">
                {w.name} ({w.code})
              </option>
            ))}
          </select>
          {errors.warehouseId && <p className="text-xs text-rose-400 mt-1">{errors.warehouseId.message}</p>}
        </div>

        <Input
          label={isAdjustment ? 'Target Quantity Count *' : 'Quantity Units *'}
          type="number"
          placeholder="e.g. 50"
          error={errors.quantity?.message}
          {...register('quantity')}
        />

        <Input
          label="Reference Number"
          placeholder="e.g. PO-98421 or SO-1204"
          error={errors.referenceNumber?.message}
          {...register('referenceNumber')}
        />

        <div className="flex flex-col gap-1">
          <label className="text-xs font-medium text-[var(--text-secondary)]">Reason / Notes</label>
          <textarea
            rows={2}
            placeholder="Audit remarks, damaged stock reason..."
            className="w-full rounded-lg border text-xs p-2 bg-white/5 border-[var(--border-color)] text-[var(--text-primary)] placeholder:text-[var(--text-muted)] focus:outline-none focus:ring-1 focus:ring-indigo-500"
            {...register('notes')}
          />
        </div>

        <div className="flex justify-end space-x-2 pt-3 border-t border-[var(--border-color)]">
          <Button variant="ghost" size="sm" type="button" onClick={onClose}>
            Cancel
          </Button>
          <Button variant={isStockIn ? 'primary' : isAdjustment ? 'secondary' : 'danger'} size="sm" type="submit" loading={isLoading}>
            Confirm {title}
          </Button>
        </div>
      </form>
    </FormModal>
  );
}
