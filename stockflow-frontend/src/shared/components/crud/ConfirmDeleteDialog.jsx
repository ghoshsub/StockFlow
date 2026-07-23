import { AlertTriangle } from 'lucide-react';
import { Button } from '@/shared/components/ui/Button';

/**
 * ConfirmDeleteDialog — Reusable delete confirmation modal.
 */
export function ConfirmDeleteDialog({ isOpen, onClose, onConfirm, title = 'Confirm Delete', message = 'Are you sure you want to delete this item? This action cannot be undone.', isLoading = false }) {
  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/50 backdrop-blur-xs">
      <div className="bg-[var(--bg-card)] border border-[var(--border-color)] rounded-xl p-6 max-w-sm w-full shadow-2xl space-y-4 animate-in fade-in zoom-in duration-150">
        <div className="flex items-center space-x-3 text-rose-500">
          <div className="p-2 rounded-full bg-rose-500/10">
            <AlertTriangle size={24} />
          </div>
          <h3 className="text-base font-semibold text-[var(--text-primary)]">{title}</h3>
        </div>

        <p className="text-xs text-[var(--text-secondary)] leading-relaxed">{message}</p>

        <div className="flex items-center justify-end space-x-2 pt-2 border-t border-[var(--border-color)]">
          <Button variant="ghost" size="sm" onClick={onClose} disabled={isLoading}>
            Cancel
          </Button>
          <Button variant="danger" size="sm" onClick={onConfirm} loading={isLoading}>
            Delete
          </Button>
        </div>
      </div>
    </div>
  );
}
