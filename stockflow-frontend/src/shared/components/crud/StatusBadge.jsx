import { Badge } from '@/shared/components/ui/Badge';

/**
 * StatusBadge — Helper component to display Active/Inactive badge status.
 */
export function StatusBadge({ active }) {
  return (
    <Badge variant={active ? 'success' : 'neutral'}>
      {active ? 'ACTIVE' : 'INACTIVE'}
    </Badge>
  );
}
