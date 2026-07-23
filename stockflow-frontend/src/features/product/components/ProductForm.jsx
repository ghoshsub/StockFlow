import { useState, useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { Upload, Image as ImageIcon } from 'lucide-react';
import { Input } from '@/shared/components/ui/Input';
import { Button } from '@/shared/components/ui/Button';
import { categoryService } from '@/features/category/services/categoryService';
import { brandService } from '@/features/brand/services/brandService';
import { supplierService } from '@/features/supplier/services/supplierService';
import { warehouseService } from '@/features/warehouse/services/warehouseService';

const productSchema = z
  .object({
    name: z.string().min(2, 'Product name must be at least 2 characters'),
    barcode: z.string().optional(),
    description: z.string().optional(),
    buyingPrice: z.coerce.number().positive('Buying price must be > 0'),
    sellingPrice: z.coerce.number().positive('Selling price must be > 0'),
    quantity: z.coerce.number().int().min(0, 'Quantity cannot be negative'),
    minimumStock: z.coerce.number().int().min(0, 'Minimum stock threshold cannot be negative'),
    categoryId: z.coerce.number().positive('Please select a valid Category'),
    brandId: z.coerce.number().positive('Please select a valid Brand'),
    supplierId: z.coerce.number().positive('Please select a valid Supplier'),
    warehouseId: z.coerce.number().positive('Please select a valid Warehouse'),
    imageUrl: z.string().optional(),
  })
  .refine((data) => data.sellingPrice > data.buyingPrice, {
    message: 'Selling price must be greater than buying price',
    path: ['sellingPrice'],
  });

export function ProductForm({ initialValues, onSubmit, isLoading, onCancel }) {
  const [categories, setCategories] = useState([]);
  const [brands, setBrands] = useState([]);
  const [suppliers, setSuppliers] = useState([]);
  const [warehouses, setWarehouses] = useState([]);
  const [imagePreview, setImagePreview] = useState(initialValues?.imageUrl || '');

  const {
    register,
    handleSubmit,
    setValue,
    formState: { errors },
  } = useForm({
    resolver: zodResolver(productSchema),
    defaultValues: initialValues
      ? {
          name: initialValues.name || '',
          barcode: initialValues.barcode || '',
          description: initialValues.description || '',
          buyingPrice: initialValues.buyingPrice || '',
          sellingPrice: initialValues.sellingPrice || '',
          quantity: initialValues.quantity ?? 0,
          minimumStock: initialValues.minimumStock ?? 5,
          categoryId: initialValues.categoryId || '',
          brandId: initialValues.brandId || '',
          supplierId: initialValues.supplierId || '',
          warehouseId: initialValues.warehouseId || '',
          imageUrl: initialValues.imageUrl || '',
        }
      : {
          name: '', barcode: '', description: '', buyingPrice: '', sellingPrice: '',
          quantity: 0, minimumStock: 5, categoryId: '', brandId: '', supplierId: '', warehouseId: '', imageUrl: '',
        },
  });

  useEffect(() => {
    // Populate select dropdowns from APIs
    Promise.all([
      categoryService.getAllList(),
      brandService.getAllList(),
      supplierService.getAllList(),
      warehouseService.getAllList(),
    ])
      .then(([cats, brs, sups, whs]) => {
        setCategories(cats || []);
        setBrands(brs || []);
        setSuppliers(sups || []);
        setWarehouses(whs || []);
      })
      .catch((err) => console.error('Failed to load entity dropdowns', err));
  }, []);

  const handleImageFileChange = (e) => {
    const file = e.target.files?.[0];
    if (file) {
      const fakeUrl = URL.createObjectURL(file);
      setImagePreview(fakeUrl);
      setValue('imageUrl', fakeUrl);
    }
  };

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="space-y-4 max-h-[75vh] overflow-y-auto pr-1">
      {/* Image Preview / Upload Section */}
      <div className="flex items-center space-x-4 p-3 border border-dashed border-[var(--border-color)] rounded-xl bg-white/5">
        <div className="w-16 h-16 rounded-lg bg-[var(--bg-secondary)] border border-[var(--border-color)] flex items-center justify-center overflow-hidden shrink-0">
          {imagePreview ? (
            <img src={imagePreview} alt="Preview" className="w-full h-full object-cover" />
          ) : (
            <ImageIcon size={24} className="text-[var(--text-muted)]" />
          )}
        </div>
        <div className="flex-1">
          <label className="inline-flex items-center space-x-2 px-3 py-1.5 rounded-lg text-xs font-medium bg-indigo-600/20 text-indigo-400 border border-indigo-500/30 cursor-pointer hover:bg-indigo-600/30 transition-colors">
            <Upload size={14} />
            <span>Select Product Image</span>
            <input type="file" accept="image/*" className="hidden" onChange={handleImageFileChange} />
          </label>
          <p className="text-[10px] text-[var(--text-muted)] mt-1">PNG, JPG or WebP up to 5MB (Preview only)</p>
        </div>
      </div>

      {/* Basic Info */}
      <div className="grid grid-cols-2 gap-3">
        <Input label="Product Name *" placeholder="e.g. Wireless Gaming Mouse" error={errors.name?.message} {...register('name')} />
        <Input label="Barcode" placeholder="e.g. 8901234567890" error={errors.barcode?.message} {...register('barcode')} />
      </div>

      {/* Select Dropdowns Grid */}
      <div className="grid grid-cols-2 gap-3">
        <div>
          <label className="text-xs font-medium text-[var(--text-secondary)]">Category *</label>
          <select
            className="w-full mt-1.5 rounded-lg border text-xs p-2.5 bg-white/5 border-[var(--border-color)] text-[var(--text-primary)] focus:outline-none focus:ring-1 focus:ring-indigo-500"
            {...register('categoryId')}
          >
            <option value="">Select Category</option>
            {categories.map((c) => (
              <option key={c.id} value={c.id} className="bg-slate-900 text-white">{c.name}</option>
            ))}
          </select>
          {errors.categoryId && <p className="text-xs text-rose-400 mt-1">{errors.categoryId.message}</p>}
        </div>

        <div>
          <label className="text-xs font-medium text-[var(--text-secondary)]">Brand *</label>
          <select
            className="w-full mt-1.5 rounded-lg border text-xs p-2.5 bg-white/5 border-[var(--border-color)] text-[var(--text-primary)] focus:outline-none focus:ring-1 focus:ring-indigo-500"
            {...register('brandId')}
          >
            <option value="">Select Brand</option>
            {brands.map((b) => (
              <option key={b.id} value={b.id} className="bg-slate-900 text-white">{b.name}</option>
            ))}
          </select>
          {errors.brandId && <p className="text-xs text-rose-400 mt-1">{errors.brandId.message}</p>}
        </div>

        <div>
          <label className="text-xs font-medium text-[var(--text-secondary)]">Supplier *</label>
          <select
            className="w-full mt-1.5 rounded-lg border text-xs p-2.5 bg-white/5 border-[var(--border-color)] text-[var(--text-primary)] focus:outline-none focus:ring-1 focus:ring-indigo-500"
            {...register('supplierId')}
          >
            <option value="">Select Supplier</option>
            {suppliers.map((s) => (
              <option key={s.id} value={s.id} className="bg-slate-900 text-white">{s.name}</option>
            ))}
          </select>
          {errors.supplierId && <p className="text-xs text-rose-400 mt-1">{errors.supplierId.message}</p>}
        </div>

        <div>
          <label className="text-xs font-medium text-[var(--text-secondary)]">Warehouse *</label>
          <select
            className="w-full mt-1.5 rounded-lg border text-xs p-2.5 bg-white/5 border-[var(--border-color)] text-[var(--text-primary)] focus:outline-none focus:ring-1 focus:ring-indigo-500"
            {...register('warehouseId')}
          >
            <option value="">Select Warehouse</option>
            {warehouses.map((w) => (
              <option key={w.id} value={w.id} className="bg-slate-900 text-white">{w.name} ({w.code})</option>
            ))}
          </select>
          {errors.warehouseId && <p className="text-xs text-rose-400 mt-1">{errors.warehouseId.message}</p>}
        </div>
      </div>

      {/* Pricing & Stock Grid */}
      <div className="grid grid-cols-2 gap-3">
        <Input label="Buying Price ($) *" type="number" step="0.01" placeholder="49.99" error={errors.buyingPrice?.message} {...register('buyingPrice')} />
        <Input label="Selling Price ($) *" type="number" step="0.01" placeholder="89.99" error={errors.sellingPrice?.message} {...register('sellingPrice')} />
        <Input label="Initial Quantity *" type="number" placeholder="50" error={errors.quantity?.message} {...register('quantity')} />
        <Input label="Min Stock Alert *" type="number" placeholder="5" error={errors.minimumStock?.message} {...register('minimumStock')} />
      </div>

      {/* Description */}
      <div className="flex flex-col gap-1">
        <label className="text-xs font-medium text-[var(--text-secondary)]">Description</label>
        <textarea
          rows={2}
          placeholder="Product specifications, features..."
          className="w-full rounded-lg border text-xs p-2 bg-white/5 border-[var(--border-color)] text-[var(--text-primary)] placeholder:text-[var(--text-muted)] focus:outline-none focus:ring-1 focus:ring-indigo-500"
          {...register('description')}
        />
      </div>

      <div className="flex justify-end space-x-2 pt-3 border-t border-[var(--border-color)]">
        <Button variant="ghost" size="sm" type="button" onClick={onCancel}>
          Cancel
        </Button>
        <Button variant="primary" size="sm" type="submit" loading={isLoading}>
          Save Product
        </Button>
      </div>
    </form>
  );
}
