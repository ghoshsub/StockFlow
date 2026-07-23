import { ArrowUpDown, Edit, Trash2 } from 'lucide-react';
import { Card } from '@/shared/components/ui/Card';
import { SkeletonTable } from '@/shared/components/skeleton/SkeletonTable';
import { EmptyState } from '@/shared/components/EmptyState';

/**
 * DataTable — Reusable data table supporting columns, sorting, role-gated action buttons, and glass hover states.
 */
export function DataTable({
  columns = [],
  data = [],
  isLoading = false,
  sortBy,
  direction,
  onSort,
  onEdit,
  onDelete,
  canEdit = true,
  canDelete = true,
  emptyTitle = 'No records found',
  emptyDescription = 'There are no items to display.',
}) {
  return (
    <Card noPadding className="overflow-hidden glass-card shadow-lg">
      {isLoading ? (
        <div className="p-6">
          <SkeletonTable rows={5} cols={columns.length + 1} />
        </div>
      ) : data.length === 0 ? (
        <EmptyState title={emptyTitle} description={emptyDescription} />
      ) : (
        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs border-collapse">
            <thead>
              <tr className="border-b border-[var(--border-color)] bg-slate-500/10 text-[var(--text-secondary)] font-heading uppercase tracking-wider text-[11px]">
                {columns.map((col) => (
                  <th
                    key={col.key || col.accessor}
                    className={`py-3.5 px-5 font-bold ${col.sortable ? 'cursor-pointer select-none hover:text-indigo-500 dark:hover:text-indigo-400 transition-colors' : ''} ${col.className || ''}`}
                    onClick={() => col.sortable && onSort?.(col.accessor)}
                  >
                    <div className="flex items-center space-x-1.5">
                      <span>{col.header}</span>
                      {col.sortable && (
                        <ArrowUpDown
                          size={13}
                          className={sortBy === col.accessor ? 'text-indigo-500 font-bold' : 'opacity-40'}
                        />
                      )}
                    </div>
                  </th>
                ))}
                {(canEdit || canDelete) && (
                  <th className="py-3.5 px-5 font-bold text-right">Actions</th>
                )}
              </tr>
            </thead>
            <tbody className="divide-y divide-[var(--border-color)]">
              {data.map((row, idx) => (
                <tr key={row.id || idx} className="hover:bg-indigo-500/5 transition-colors duration-150 group">
                  {columns.map((col) => (
                    <td key={col.key || col.accessor} className={`py-4 px-5 text-sm font-medium text-[var(--text-primary)] ${col.className || ''}`}>
                      {col.cell ? col.cell(row) : row[col.accessor] ?? '—'}
                    </td>
                  ))}
                  {(canEdit || canDelete) && (
                    <td className="py-4 px-5 text-right space-x-1.5">
                      {canEdit && onEdit && (
                        <button
                          onClick={() => onEdit(row)}
                          className="p-2 rounded-lg text-[var(--text-muted)] hover:text-indigo-500 hover:bg-indigo-500/15 active:scale-95 transition-all duration-150"
                          title="Edit"
                        >
                          <Edit size={16} />
                        </button>
                      )}
                      {canDelete && onDelete && (
                        <button
                          onClick={() => onDelete(row)}
                          className="p-2 rounded-lg text-[var(--text-muted)] hover:text-rose-500 hover:bg-rose-500/15 active:scale-95 transition-all duration-150"
                          title="Delete"
                        >
                          <Trash2 size={16} />
                        </button>
                      )}
                    </td>
                  )}
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </Card>
  );
}

