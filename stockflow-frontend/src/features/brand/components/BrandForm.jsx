import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { Input } from '@/shared/components/ui/Input';
import { Button } from '@/shared/components/ui/Button';

const brandSchema = z.object({
  name: z.string().min(2, 'Name must be at least 2 characters'),
  description: z.string().optional(),
});

export function BrandForm({ initialValues, onSubmit, isLoading, onCancel }) {
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm({
    resolver: zodResolver(brandSchema),
    defaultValues: initialValues || { name: '', description: '' },
  });

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
      <Input
        label="Brand Name"
        placeholder="e.g. Apple, Samsung, Nike"
        error={errors.name?.message}
        {...register('name')}
      />

      <div className="flex flex-col gap-1.5">
        <label className="text-sm font-medium text-[var(--text-secondary)]">Description</label>
        <textarea
          rows={3}
          placeholder="Brief brand description..."
          className="w-full rounded-lg border text-sm p-2.5 bg-white/5 border-[var(--border-color)] text-[var(--text-primary)] placeholder:text-[var(--text-muted)] focus:outline-none focus:ring-2 focus:ring-indigo-500"
          {...register('description')}
        />
      </div>

      <div className="flex justify-end space-x-2 pt-3 border-t border-[var(--border-color)]">
        <Button variant="ghost" size="sm" type="button" onClick={onCancel}>
          Cancel
        </Button>
        <Button variant="primary" size="sm" type="submit" loading={isLoading}>
          Save Brand
        </Button>
      </div>
    </form>
  );
}
