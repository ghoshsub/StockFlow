import { PackageOpen } from 'lucide-react';

/**
 * EmptyState — Placeholder for empty list views or data tables.
 */
export function EmptyState({ title = 'No data available', description = 'There are no items to display at this time.', icon: Icon = PackageOpen, action }) {
  return (
    <div className="flex flex-col items-center justify-center p-8 text-center my-6">
      <div className="p-4 rounded-full bg-slate-800/40 text-slate-400 mb-3">
        <Icon size={32} />
      </div>
      <h4 className="text-base font-medium text-[var(--text-primary)] mb-1">{title}</h4>
      <p className="text-sm text-[var(--text-muted)] max-w-sm mb-4">{description}</p>
      {action && <div>{action}</div>}
    </div>
  );
}
