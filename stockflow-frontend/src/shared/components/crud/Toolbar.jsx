import { Plus, RefreshCw } from 'lucide-react';
import { Button } from '@/shared/components/ui/Button';
import { SearchBar } from './SearchBar';
import useAuthStore from '@/store/authStore';

/**
 * Toolbar — Generic top action bar for CRUD screens.
 */
export function Toolbar({
  title,
  subtitle,
  onSearch,
  onAdd,
  onRefresh,
  addLabel = 'Add New',
  allowedRoles = ['ADMIN'],
}) {
  const { role } = useAuthStore();
  const canAdd = allowedRoles.includes(role);

  return (
    <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 mb-6">
      <div>
        <h1 className="text-xl font-bold text-[var(--text-primary)]">{title}</h1>
        {subtitle && <p className="text-xs text-[var(--text-muted)] mt-0.5">{subtitle}</p>}
      </div>

      <div className="flex items-center space-x-2">
        {onSearch && <SearchBar onSearch={onSearch} />}
        {onRefresh && (
          <Button variant="ghost" size="sm" onClick={onRefresh} title="Refresh Data">
            <RefreshCw size={16} />
          </Button>
        )}
        {canAdd && onAdd && (
          <Button variant="primary" size="sm" icon={Plus} onClick={onAdd}>
            {addLabel}
          </Button>
        )}
      </div>
    </div>
  );
}
