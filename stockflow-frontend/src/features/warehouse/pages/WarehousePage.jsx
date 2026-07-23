import { useState } from 'react';
import { useTableState } from '@/shared/hooks/crud/useTableState';
import { useGenericCrud } from '@/shared/hooks/crud/useGenericCrud';
import { warehouseService } from '../services/warehouseService';
import { Toolbar } from '@/shared/components/crud/Toolbar';
import { DataTable } from '@/shared/components/crud/DataTable';
import { Pagination } from '@/shared/components/crud/Pagination';
import { FormModal } from '@/shared/components/crud/FormModal';
import { ConfirmDeleteDialog } from '@/shared/components/crud/ConfirmDeleteDialog';
import { StatusBadge } from '@/shared/components/crud/StatusBadge';
import { WarehouseForm } from '../components/WarehouseForm';
import { formatNumber } from '@/shared/utils/formatters';
import useAuthStore from '@/store/authStore';

export function WarehousePage() {
  const { role } = useAuthStore();
  const tableState = useTableState('name', 'ASC');
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [selectedWarehouse, setSelectedWarehouse] = useState(null);
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
    queryKey: 'warehouses',
    service: warehouseService,
    ...tableState,
  });

  const columns = [
    { header: 'ID', accessor: 'id', sortable: true, className: 'w-14 text-slate-400 font-mono' },
    { header: 'Name', accessor: 'name', sortable: true, className: 'font-semibold text-[var(--text-primary)]' },
    { header: 'Code', accessor: 'code', sortable: true, className: 'font-mono text-indigo-400' },
    {
      header: 'Location',
      accessor: 'city',
      sortable: false,
      cell: (row) => [row.city, row.state, row.country].filter(Boolean).join(', ') || '—',
    },
    {
      header: 'Capacity',
      accessor: 'capacity',
      sortable: true,
      cell: (row) => (row.capacity ? formatNumber(row.capacity) + ' units' : '—'),
    },
    { header: 'Manager', accessor: 'managerName', sortable: false, cell: (row) => row.managerName || '—' },
    { header: 'Status', accessor: 'active', sortable: true, cell: (row) => <StatusBadge active={row.active} /> },
  ];

  const handleCreate = () => {
    setSelectedWarehouse(null);
    setIsModalOpen(true);
  };

  const handleEdit = (warehouse) => {
    setSelectedWarehouse(warehouse);
    setIsModalOpen(true);
  };

  const handleDeletePrompt = (warehouse) => {
    setDeleteTarget(warehouse);
  };

  const handleSubmitForm = async (values) => {
    if (selectedWarehouse) {
      await updateItem({ id: selectedWarehouse.id, data: values });
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
        title="Warehouse Management"
        subtitle="Manage storage locations and distribution centers"
        onSearch={(kw) => { tableState.setKeyword(kw); tableState.setPage(0); }}
        onRefresh={refetch}
        onAdd={handleCreate}
        addLabel="Add Warehouse"
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
        emptyTitle="No warehouses found"
        emptyDescription="Add storage locations to track inventory by warehouse."
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
        title={selectedWarehouse ? 'Edit Warehouse' : 'Create Warehouse'}
      >
        <WarehouseForm
          initialValues={selectedWarehouse}
          onSubmit={handleSubmitForm}
          isLoading={isCreating || isUpdating}
          onCancel={() => setIsModalOpen(false)}
        />
      </FormModal>

      <ConfirmDeleteDialog
        isOpen={!!deleteTarget}
        onClose={() => setDeleteTarget(null)}
        onConfirm={handleConfirmDelete}
        title="Deactivate Warehouse"
        message={`Are you sure you want to deactivate warehouse "${deleteTarget?.name}"? This will prevent new inventory from being assigned to it.`}
        isLoading={isDeleting}
      />
    </div>
  );
}
