import { useState } from 'react';
import { FileText, Plus } from 'lucide-react';
import { useTableState } from '@/shared/hooks/crud/useTableState';
import { useSales, useCreateSale } from '../hooks/useSales';
import { Toolbar } from '@/shared/components/crud/Toolbar';
import { DataTable } from '@/shared/components/crud/DataTable';
import { Pagination } from '@/shared/components/crud/Pagination';
import { Badge, statusToBadgeVariant } from '@/shared/components/ui/Badge';
import { CreateSaleModal } from '../components/CreateSaleModal';
import { InvoicePreviewModal } from '../components/InvoicePreviewModal';
import { formatCurrency, formatDateTime } from '@/shared/utils/formatters';
import useAuthStore from '@/store/authStore';

export function SalesPage() {
  const { role } = useAuthStore();
  const tableState = useTableState('saleDate', 'DESC');

  const [isCreateOpen, setIsCreateOpen] = useState(false);
  const [viewInvoice, setViewInvoice] = useState(null);

  const canCreate = ['ADMIN', 'STAFF'].includes(role);

  const { data: pageData, isLoading, refetch } = useSales(tableState);
  const { mutateAsync: createSale, isPending: isCreating } = useCreateSale();

  const columns = [
    {
      header: 'SO Number',
      accessor: 'saleNumber',
      sortable: true,
      cell: (row) => <span className="font-mono font-bold text-indigo-400">{row.saleNumber}</span>,
    },
    { header: 'Customer Name', accessor: 'customerName', sortable: false, cell: (r) => r.customerName || 'Walk-in Customer' },
    { header: 'Warehouse', accessor: 'warehouseName', sortable: false, cell: (r) => r.warehouseName || '—' },
    {
      header: 'Sale Date',
      accessor: 'saleDate',
      sortable: true,
      cell: (row) => formatDateTime(row.saleDate),
    },
    {
      header: 'Total Receivable',
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
      header: 'Invoice',
      accessor: 'actions',
      sortable: false,
      cell: (row) => (
        <button
          onClick={() => setViewInvoice(row)}
          className="p-1.5 rounded text-[var(--text-muted)] hover:text-indigo-400 hover:bg-indigo-500/10"
          title="View Invoice"
        >
          <FileText size={16} />
        </button>
      ),
    },
  ];

  const handleCreateSubmit = async (values) => {
    await createSale(values);
    setIsCreateOpen(false);
  };

  return (
    <div className="space-y-4">
      <Toolbar
        title="Sales Orders & Invoicing"
        subtitle="Manage outbound sales, POS transactions, and customer invoices"
        onSearch={(kw) => { tableState.setKeyword(kw); tableState.setPage(0); }}
        onRefresh={refetch}
        onAdd={() => setIsCreateOpen(true)}
        addLabel="Create Sales Order"
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
        emptyTitle="No sales orders found"
        emptyDescription="Create your first sales order to process customer transactions."
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
      <CreateSaleModal
        isOpen={isCreateOpen}
        onClose={() => setIsCreateOpen(false)}
        onSubmit={handleCreateSubmit}
        isLoading={isCreating}
      />

      {/* Invoice Preview Modal */}
      <InvoicePreviewModal
        isOpen={!!viewInvoice}
        onClose={() => setViewInvoice(null)}
        sale={viewInvoice}
      />
    </div>
  );
}
