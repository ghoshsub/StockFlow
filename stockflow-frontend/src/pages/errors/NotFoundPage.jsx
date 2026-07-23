import { Link } from 'react-router-dom';
import { FileQuestion, ArrowLeft } from 'lucide-react';
import { Button } from '@/shared/components/ui/Button';
import { ROUTES } from '@/constants/routes';

export function NotFoundPage() {
  return (
    <div className="min-h-screen flex flex-col items-center justify-center bg-[var(--bg-primary)] p-4 text-center">
      <div className="p-4 rounded-full bg-indigo-500/10 text-indigo-400 mb-4">
        <FileQuestion size={48} />
      </div>
      <h1 className="text-4xl font-extrabold text-[var(--text-primary)] mb-2">404</h1>
      <h2 className="text-lg font-semibold text-[var(--text-secondary)] mb-1">Page Not Found</h2>
      <p className="text-sm text-[var(--text-muted)] max-w-sm mb-6">
        The page you are looking for does not exist or has been moved.
      </p>
      <Link to={ROUTES.DASHBOARD}>
        <Button variant="primary" icon={ArrowLeft}>
          Back to Dashboard
        </Button>
      </Link>
    </div>
  );
}
