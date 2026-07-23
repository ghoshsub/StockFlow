import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { Lock, User } from 'lucide-react';
import { Input } from '@/shared/components/ui/Input';
import { Button } from '@/shared/components/ui/Button';
import { useLogin } from '../hooks/useLogin';

// Validation Schema
const loginSchema = z.object({
  usernameOrEmail: z.string().min(1, 'Username or Email is required'),
  password: z.string().min(4, 'Password must be at least 4 characters'),
});

/**
 * LoginForm — React Hook Form with Zod validation.
 * @param {object} defaultValues - Pre-filled credentials (from ?role= param on LoginPage)
 */
export function LoginForm({ defaultValues = { usernameOrEmail: '', password: '' } }) {
  const { mutate: login, isPending } = useLogin();

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm({
    resolver: zodResolver(loginSchema),
    defaultValues,
  });

  const onSubmit = (data) => {
    login(data);
  };

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
      <Input
        label="Username or Email"
        id="usernameOrEmail"
        placeholder="admin or admin@stockflow.com"
        icon={User}
        error={errors.usernameOrEmail?.message}
        {...register('usernameOrEmail')}
      />

      <Input
        label="Password"
        id="password"
        type="password"
        placeholder="••••••••"
        icon={Lock}
        error={errors.password?.message}
        {...register('password')}
      />

      <Button type="submit" variant="primary" loading={isPending} className="w-full mt-2">
        Sign In
      </Button>
    </form>
  );
}
