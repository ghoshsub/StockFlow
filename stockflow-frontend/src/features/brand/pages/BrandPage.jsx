import { useState } from 'react';
import { useTableState } from '@/shared/hooks/crud/useTableState';
import { useGenericCrud } from '@/shared/hooks/crud/useGenericCrud';
import { brandService } from '../services/brandService';
import { Toolbar } from '@/shared/components/crud/Toolbar';
import { DataTable } from '@/shared/components/crud/DataTable';
import { Pagination } from '@/shared/components/crud/Pagination';
import { FormModal } from '@/shared/components/crud/FormModal';
import { ConfirmDeleteDialog } from '@/shared/components/crud/ConfirmDeleteDialog';
import { StatusBadge } from '@/shared/components/crud/StatusBadge';
import { BrandForm } from '../components/BrandForm';
import useAuthStore from '@/store/authStore';

export function BrandPage() {
  const { role } = useAuthStore();
  const tableState = useTableState('name', 'ASC');
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [selectedBrand, setSelectedBrand] = useState(null);
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
    queryKey: 'brands',
    service: brandService,
    ...tableState,
  });

  const columns = [
    { header: 'ID', accessor: 'id', sortable: true, className: 'w-16 text-slate-400 font-mono' },
    { header: 'Brand Name', accessor: 'name', sortable: true, className: 'font-semibold text-[var(--text-primary)]' },
    { header: 'Description', accessor: 'description', sortable: false, cell: (row) => row.description || '—' },
    { header: 'Status', accessor: 'active', sortable: true, cell: (row) => <StatusBadge active={row.active} /> },
  ];

  const handleCreate = () => {
    setSelectedBrand(null);
    setIsModalOpen(true);
  };

  const handleEdit = (brand) => {
    setSelectedBrand(brand);
    setIsModalOpen(true);
  };

  const handleDeletePrompt = (brand) => {
    setDeleteTarget(brand);
  };

  const handleSubmitForm = async (values) => {
    if (selectedBrand) {
      await updateItem({ id: selectedBrand.id, data: values });
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
        title="Brand Management"
        subtitle="Manage product manufacturers and brand entities"
        onSearch={(kw) => { tableState.setKeyword(kw); tableState.setPage(0); }}
        onRefresh={refetch}
        onAdd={handleCreate}
        addLabel="Add Brand"
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
        emptyTitle="No brands found"
        emptyDescription="Create your first product brand to start organizing stock."
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
        title={selectedBrand ? 'Edit Brand' : 'Create Brand'}
      >
        <BrandForm
          initialValues={selectedBrand}
          onSubmit={handleSubmitForm}
          isLoading={isCreating || isUpdating}
          onCancel={() => setIsModalOpen(false)}
        />
      </FormModal>

      <ConfirmDeleteDialog
        isOpen={!!deleteTarget}
        onClose={() => setDeleteTarget(null)}
        onConfirm={handleConfirmDelete}
        title="Deactivate Brand"
        message={`Are you sure you want to soft-delete brand "${deleteTarget?.name}"?`}
        isLoading={isDeleting}
      />
    </div>
  );
}
