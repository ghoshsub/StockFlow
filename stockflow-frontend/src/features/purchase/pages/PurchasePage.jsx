import { useState } from 'react';
import { Eye, Plus } from 'lucide-react';
import { useTableState } from '@/shared/hooks/crud/useTableState';
import { usePurchases, useCreatePurchase } from '../hooks/usePurchases';
import { Toolbar } from '@/shared/components/crud/Toolbar';
import { DataTable } from '@/shared/components/crud/DataTable';
import { Pagination } from '@/shared/components/crud/Pagination';
import { Badge, statusToBadgeVariant } from '@/shared/components/ui/Badge';
import { CreatePurchaseModal } from '../components/CreatePurchaseModal';
import { PurchaseDetailsModal } from '../components/PurchaseDetailsModal';
import { formatCurrency, formatDateTime } from '@/shared/utils/formatters';
import useAuthStore from '@/store/authStore';

export function PurchasePage() {
  const { role } = useAuthStore();
  const tableState = useTableState('purchaseDate', 'DESC');

  const [isCreateOpen, setIsCreateOpen] = useState(false);
  const [viewPurchase, setViewPurchase] = useState(null);

  const canCreate = ['ADMIN', 'STAFF'].includes(role);

  const { data: pageData, isLoading, refetch } = usePurchases(tableState);
  const { mutateAsync: createPurchase, isPending: isCreating } = useCreatePurchase();

  const columns = [
    {
      header: 'PO Number',
      accessor: 'purchaseNumber',
      sortable: true,
      cell: (row) => <span className="font-mono font-bold text-indigo-400">{row.purchaseNumber}</span>,
    },
    { header: 'Supplier Name', accessor: 'supplierName', sortable: false, cell: (r) => r.supplierName || '—' },
    { header: 'Warehouse', accessor: 'warehouseName', sortable: false, cell: (r) => r.warehouseName || '—' },
    {
      header: 'Purchase Date',
      accessor: 'purchaseDate',
      sortable: true,
      cell: (row) => formatDateTime(row.purchaseDate),
    },
    {
      header: 'Total Amount',
      accessor: 'totalAmount',
      sortable: true,
      cell: (row) => <span className="font-bold text-emerald-400">{formatCurrency(row.totalAmount)}</span>,
    },
    {
      header: 'Payment Status',
      accessor: 'paymentStatus',
      sortable: false,
      cell: (row) => (
        <Badge variant={statusToBadgeVariant(row.paymentStatus)}>
          {row.paymentStatus || 'PAID'}
        </Badge>
      ),
    },
    {
      header: 'Details',
      accessor: 'actions',
      sortable: false,
      cell: (row) => (
        <button
          onClick={() => setViewPurchase(row)}
          className="p-1.5 rounded text-[var(--text-muted)] hover:text-indigo-400 hover:bg-indigo-500/10"
          title="View Details"
        >
          <Eye size={16} />
        </button>
      ),
    },
  ];

  const handleCreateSubmit = async (values) => {
    await createPurchase(values);
    setIsCreateOpen(false);
  };

  return (
    <div className="space-y-4">
      <Toolbar
        title="Purchase Orders"
        subtitle="Manage procurement orders and incoming stock receipts"
        onSearch={(kw) => { tableState.setKeyword(kw); tableState.setPage(0); }}
        onRefresh={refetch}
        onAdd={() => setIsCreateOpen(true)}
        addLabel="Create Purchase Order"
        allowedRoles={['ADMIN', 'STAFF']}
      />

      <DataTable
        columns={columns}
        data={pageData?.content || []}
        isLoading={isLoading}
        sortBy={tableState.sortBy}
        direction={tableState.direction}
        onSort={tableState.handleSort}
        canEdit={false}
        canDelete={false}
        emptyTitle="No purchase orders found"
        emptyDescription="Create your first purchase order to replenish stock."
      />

      <Pagination
        page={tableState.page}
        size={tableState.size}
        totalPages={pageData?.totalPages || 0}
        totalElements={pageData?.totalElements || 0}
        onPageChange={tableState.setPage}
        onSizeChange={(s) => { tableState.setSize(s); tableState.setPage(0); }}
      />

      {/* Create Order Modal */}
      <CreatePurchaseModal
        isOpen={isCreateOpen}
        onClose={() => setIsCreateOpen(false)}
        onSubmit={handleCreateSubmit}
        isLoading={isCreating}
      />

      {/* View Order Modal */}
      <PurchaseDetailsModal
        isOpen={!!viewPurchase}
        onClose={() => setViewPurchase(null)}
        purchase={viewPurchase}
      />
    </div>
  );
}
