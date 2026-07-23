import { useState } from 'react';
import { useTableState } from '@/shared/hooks/crud/useTableState';
import { useGenericCrud } from '@/shared/hooks/crud/useGenericCrud';
import { supplierService } from '../services/supplierService';
import { Toolbar } from '@/shared/components/crud/Toolbar';
import { DataTable } from '@/shared/components/crud/DataTable';
import { Pagination } from '@/shared/components/crud/Pagination';
import { FormModal } from '@/shared/components/crud/FormModal';
import { ConfirmDeleteDialog } from '@/shared/components/crud/ConfirmDeleteDialog';
import { StatusBadge } from '@/shared/components/crud/StatusBadge';
import { SupplierForm } from '../components/SupplierForm';
import useAuthStore from '@/store/authStore';

export function SupplierPage() {
  const { role } = useAuthStore();
  const tableState = useTableState('name', 'ASC');
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [selectedSupplier, setSelectedSupplier] = useState(null);
  const [deleteTarget, setDeleteTarget] = useState(null);

  const canEdit = ['ADMIN'].includes(role);
  const canDelete = ['ADMIN'].includes(role);

  const {
    data: pageData,
    isLoading,
    refetch,
    createItem,
    isCreating,
    updateItem,
    isUpdating,
    deleteItem,
    isDeleting,
  } = useGenericCrud({
    queryKey: 'suppliers',
    service: supplierService,
    ...tableState,
  });

  const columns = [
    { header: 'ID', accessor: 'id', sortable: true, className: 'w-16 text-slate-400 font-mono' },
    { header: 'Supplier Name', accessor: 'name', sortable: true, className: 'font-semibold text-[var(--text-primary)]' },
    { header: 'Contact Person', accessor: 'contactPerson', sortable: false, cell: (r) => r.contactPerson || '—' },
    { header: 'Email', accessor: 'email', sortable: false, cell: (r) => r.email || '—' },
    { header: 'Phone', accessor: 'phone', sortable: false, cell: (r) => r.phone || '—' },
    { header: 'Status', accessor: 'active', sortable: true, cell: (row) => <StatusBadge active={row.active} /> },
  ];

  const handleCreate = () => {
    setSelectedSupplier(null);
    setIsModalOpen(true);
  };

  const handleEdit = (supplier) => {
    setSelectedSupplier(supplier);
    setIsModalOpen(true);
  };

  const handleDeletePrompt = (supplier) => {
    setDeleteTarget(supplier);
  };

  const handleSubmitForm = async (values) => {
    if (selectedSupplier) {
      await updateItem({ id: selectedSupplier.id, data: values });
    } else {
      await createItem(values);
    }
    setIsModalOpen(false);
  };

  const handleConfirmDelete = async () => {
    if (deleteTarget) {
      await deleteItem(deleteTarget.id);
      setDeleteTarget(null);
    }
  };

  return (
    <div className="space-y-4">
      <Toolbar
        title="Supplier Directory"
        subtitle="Manage product vendors and procurement partners"
        onSearch={(kw) => { tableState.setKeyword(kw); tableState.setPage(0); }}
        onRefresh={refetch}
        onAdd={handleCreate}
        addLabel="Add Supplier"
        allowedRoles={['ADMIN']}
      />

      <DataTable
        columns={columns}
        data={pageData?.content || []}
        isLoading={isLoading}
        sortBy={tableState.sortBy}
        direction={tableState.direction}
        onSort={tableState.handleSort}
        onEdit={handleEdit}
        onDelete={handleDeletePrompt}
        canEdit={canEdit}
        canDelete={canDelete}
        emptyTitle="No suppliers found"
        emptyDescription="Add vendor contact details to manage purchase orders."
      />

      <Pagination
        page={tableState.page}
        size={tableState.size}
        totalPages={pageData?.totalPages || 0}
        totalElements={pageData?.totalElements || 0}
        onPageChange={tableState.setPage}
        onSizeChange={(newSize) => { tableState.setSize(newSize); tableState.setPage(0); }}
      />

      <FormModal
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        title={selectedSupplier ? 'Edit Supplier' : 'Create Supplier'}
      >
        <SupplierForm
          initialValues={selectedSupplier}
          onSubmit={handleSubmitForm}
          isLoading={isCreating || isUpdating}
          onCancel={() => setIsModalOpen(false)}
        />
      </FormModal>

      <ConfirmDeleteDialog
        isOpen={!!deleteTarget}
        onClose={() => setDeleteTarget(null)}
        onConfirm={handleConfirmDelete}
        title="Deactivate Supplier"
        message={`Are you sure you want to soft-delete supplier "${deleteTarget?.name}"?`}
        isLoading={isDeleting}
      />
    </div>
  );
}
