import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { Input } from '@/shared/components/ui/Input';
import { Button } from '@/shared/components/ui/Button';

const warehouseSchema = z.object({
  name: z.string().min(2, 'Name must be at least 2 characters'),
  code: z.string().min(2, 'Code must be at least 2 characters'),
  address: z.string().optional(),
  city: z.string().optional(),
  state: z.string().optional(),
  country: z.string().optional(),
  postalCode: z.string().optional(),
  capacity: z.coerce.number().int().positive('Capacity must be a positive number').optional(),
  managerName: z.string().optional(),
  managerEmail: z.string().email('Invalid email').or(z.literal('')).optional(),
  managerPhone: z.string().optional(),
});

export function WarehouseForm({ initialValues, onSubmit, isLoading, onCancel }) {
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm({
    resolver: zodResolver(warehouseSchema),
    defaultValues: initialValues
      ? {
          name: initialValues.name || '',
          code: initialValues.code || '',
          address: initialValues.address || '',
          city: initialValues.city || '',
          state: initialValues.state || '',
          country: initialValues.country || '',
          postalCode: initialValues.postalCode || '',
          capacity: initialValues.capacity || '',
          managerName: initialValues.managerName || '',
          managerEmail: initialValues.managerEmail || '',
          managerPhone: initialValues.managerPhone || '',
        }
      : {
          name: '', code: '', address: '', city: '', state: '',
          country: '', postalCode: '', capacity: '', managerName: '',
          managerEmail: '', managerPhone: '',
        },
  });

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="space-y-3 max-h-[70vh] overflow-y-auto pr-1">
      {/* Core Info */}
      <div className="grid grid-cols-2 gap-3">
        <Input
          label="Warehouse Name *"
          placeholder="e.g. Main Warehouse"
          error={errors.name?.message}
          {...register('name')}
        />
        <Input
          label="Warehouse Code *"
          placeholder="e.g. WH-001"
          error={errors.code?.message}
          {...register('code')}
        />
      </div>

      {/* Location */}
      <p className="text-xs font-semibold text-[var(--text-muted)] uppercase tracking-wide pt-1">Location</p>
      <Input label="Address" placeholder="Street address" {...register('address')} />
      <div className="grid grid-cols-3 gap-3">
        <Input label="City" placeholder="City" {...register('city')} />
        <Input label="State" placeholder="State / Province" {...register('state')} />
        <Input label="Postal Code" placeholder="12345" {...register('postalCode')} />
      </div>
      <Input label="Country" placeholder="Country" {...register('country')} />

      {/* Operational */}
      <p className="text-xs font-semibold text-[var(--text-muted)] uppercase tracking-wide pt-1">Operations</p>
      <Input
        label="Capacity (units)"
        type="number"
        placeholder="e.g. 5000"
        error={errors.capacity?.message}
        {...register('capacity')}
      />

      {/* Manager */}
      <p className="text-xs font-semibold text-[var(--text-muted)] uppercase tracking-wide pt-1">Manager Info</p>
      <Input label="Manager Name" placeholder="e.g. Jane Smith" {...register('managerName')} />
      <div className="grid grid-cols-2 gap-3">
        <Input
          label="Manager Email"
          type="email"
          placeholder="manager@company.com"
          error={errors.managerEmail?.message}
          {...register('managerEmail')}
        />
        <Input label="Manager Phone" placeholder="+1 (555) 000-0000" {...register('managerPhone')} />
      </div>

      <div className="flex justify-end space-x-2 pt-3 border-t border-[var(--border-color)]">
        <Button variant="ghost" size="sm" type="button" onClick={onCancel}>
          Cancel
        </Button>
        <Button variant="primary" size="sm" type="submit" loading={isLoading}>
          Save Warehouse
        </Button>
      </div>
    </form>
  );
}
