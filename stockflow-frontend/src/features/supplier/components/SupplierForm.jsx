import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { Input } from '@/shared/components/ui/Input';
import { Button } from '@/shared/components/ui/Button';

const supplierSchema = z.object({
  name: z.string().min(2, 'Name must be at least 2 characters'),
  contactPerson: z.string().optional(),
  email: z.string().email('Invalid email address').or(z.literal('')).optional(),
  phone: z.string().optional(),
  address: z.string().optional(),
});

export function SupplierForm({ initialValues, onSubmit, isLoading, onCancel }) {
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm({
    resolver: zodResolver(supplierSchema),
    defaultValues: initialValues || { name: '', contactPerson: '', email: '', phone: '', address: '' },
  });

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="space-y-3">
      <Input
        label="Supplier Name *"
        placeholder="e.g. Acme Corporation"
        error={errors.name?.message}
        {...register('name')}
      />

      <div className="grid grid-cols-2 gap-3">
        <Input
          label="Contact Person"
          placeholder="e.g. John Doe"
          error={errors.contactPerson?.message}
          {...register('contactPerson')}
        />
        <Input
          label="Phone Number"
          placeholder="+1 (555) 000-0000"
          error={errors.phone?.message}
          {...register('phone')}
        />
      </div>

      <Input
        label="Email Address"
        type="email"
        placeholder="contact@acme.com"
        error={errors.email?.message}
        {...register('email')}
      />

      <div className="flex flex-col gap-1">
        <label className="text-xs font-medium text-[var(--text-secondary)]">Address</label>
        <textarea
          rows={2}
          placeholder="Street address, city, state..."
          className="w-full rounded-lg border text-xs p-2 bg-white/5 border-[var(--border-color)] text-[var(--text-primary)] placeholder:text-[var(--text-muted)] focus:outline-none focus:ring-1 focus:ring-indigo-500"
          {...register('address')}
        />
      </div>

      <div className="flex justify-end space-x-2 pt-3 border-t border-[var(--border-color)]">
        <Button variant="ghost" size="sm" type="button" onClick={onCancel}>
          Cancel
        </Button>
        <Button variant="primary" size="sm" type="submit" loading={isLoading}>
          Save Supplier
        </Button>
      </div>
    </form>
  );
}
