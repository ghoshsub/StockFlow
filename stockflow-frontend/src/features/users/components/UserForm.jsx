import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';

const userSchema = z.object({
  username: z.string().min(3, 'Username must be at least 3 characters'),
  email: z.string().email('Invalid email address'),
  firstName: z.string().min(1, 'First name is required'),
  lastName: z.string().min(1, 'Last name is required'),
  role: z.enum(['ADMIN', 'STAFF']),
  password: z.string().optional(),
});

export function UserForm({ initialValues, onSubmit, isLoading, onCancel }) {
  const isEditing = Boolean(initialValues?.id);

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm({
    resolver: zodResolver(userSchema),
    defaultValues: {
      username: initialValues?.username || '',
      email: initialValues?.email || '',
      firstName: initialValues?.firstName || '',
      lastName: initialValues?.lastName || '',
      role: initialValues?.role || 'STAFF',
      password: '',
    },
  });

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="space-y-4 text-xs">
      <div>
        <label className="block text-[var(--text-secondary)] font-medium mb-1">Username *</label>
        <input
          {...register('username')}
          type="text"
          className="w-full px-3 py-2 rounded-lg border bg-[var(--bg-secondary)] border-[var(--border-color)] text-[var(--text-primary)] focus:outline-none focus:ring-1 focus:ring-indigo-500"
          placeholder="e.g. john_doe"
        />
        {errors.username && <p className="text-rose-400 mt-1">{errors.username.message}</p>}
      </div>

      <div>
        <label className="block text-[var(--text-secondary)] font-medium mb-1">Email *</label>
        <input
          {...register('email')}
          type="email"
          className="w-full px-3 py-2 rounded-lg border bg-[var(--bg-secondary)] border-[var(--border-color)] text-[var(--text-primary)] focus:outline-none focus:ring-1 focus:ring-indigo-500"
          placeholder="e.g. john@stockflow.com"
        />
        {errors.email && <p className="text-rose-400 mt-1">{errors.email.message}</p>}
      </div>

      <div className="grid grid-cols-2 gap-3">
        <div>
          <label className="block text-[var(--text-secondary)] font-medium mb-1">First Name *</label>
          <input
            {...register('firstName')}
            type="text"
            className="w-full px-3 py-2 rounded-lg border bg-[var(--bg-secondary)] border-[var(--border-color)] text-[var(--text-primary)] focus:outline-none focus:ring-1 focus:ring-indigo-500"
            placeholder="John"
          />
          {errors.firstName && <p className="text-rose-400 mt-1">{errors.firstName.message}</p>}
        </div>

        <div>
          <label className="block text-[var(--text-secondary)] font-medium mb-1">Last Name *</label>
          <input
            {...register('lastName')}
            type="text"
            className="w-full px-3 py-2 rounded-lg border bg-[var(--bg-secondary)] border-[var(--border-color)] text-[var(--text-primary)] focus:outline-none focus:ring-1 focus:ring-indigo-500"
            placeholder="Doe"
          />
          {errors.lastName && <p className="text-rose-400 mt-1">{errors.lastName.message}</p>}
        </div>
      </div>

      <div>
        <label className="block text-[var(--text-secondary)] font-medium mb-1">Assign Role *</label>
        <select
          {...register('role')}
          className="w-full px-3 py-2 rounded-lg border bg-[var(--bg-secondary)] border-[var(--border-color)] text-[var(--text-primary)] focus:outline-none focus:ring-1 focus:ring-indigo-500"
        >
          <option value="ADMIN">ADMIN (Full System Access)</option>
          <option value="STAFF">STAFF (Operational Access)</option>
        </select>
        {errors.role && <p className="text-rose-400 mt-1">{errors.role.message}</p>}
      </div>

      <div>
        <label className="block text-[var(--text-secondary)] font-medium mb-1">
          {isEditing ? 'Password (leave blank to keep current)' : 'Password (optional — defaults to StockFlow@123)'}
        </label>
        <input
          {...register('password')}
          type="password"
          className="w-full px-3 py-2 rounded-lg border bg-[var(--bg-secondary)] border-[var(--border-color)] text-[var(--text-primary)] focus:outline-none focus:ring-1 focus:ring-indigo-500"
          placeholder="••••••••"
        />
      </div>

      <div className="flex justify-end gap-2 pt-3 border-t border-[var(--border-color)]">
        <button
          type="button"
          onClick={onCancel}
          className="px-4 py-2 rounded-lg text-[var(--text-secondary)] hover:bg-[var(--bg-secondary)] transition-colors"
        >
          Cancel
        </button>
        <button
          type="submit"
          disabled={isLoading}
          className="px-4 py-2 rounded-lg bg-indigo-600 hover:bg-indigo-500 text-white font-medium disabled:opacity-50 transition-colors"
        >
          {isLoading ? 'Saving...' : isEditing ? 'Update User' : 'Create User'}
        </button>
      </div>
    </form>
  );
}
