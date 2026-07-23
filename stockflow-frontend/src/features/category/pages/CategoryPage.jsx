import { useState } from 'react';
import { useTableState } from '@/shared/hooks/crud/useTableState';
import { useGenericCrud } from '@/shared/hooks/crud/useGenericCrud';
import { categoryService } from '../services/categoryService';
import { Toolbar } from '@/shared/components/crud/Toolbar';
import { DataTable } from '@/shared/components/crud/DataTable';
import { Pagination } from '@/shared/components/crud/Pagination';
import { FormModal } from '@/shared/components/crud/FormModal';
import { ConfirmDeleteDialog } from '@/shared/components/crud/ConfirmDeleteDialog';
import { StatusBadge } from '@/shared/components/crud/StatusBadge';
import { CategoryForm } from '../components/CategoryForm';
import useAuthStore from '@/store/authStore';

export function CategoryPage() {
  const { role } = useAuthStore();
  const tableState = useTableState('name', 'ASC');
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [selectedCategory, setSelectedCategory] = useState(null);
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
    queryKey: 'categories',
    service: categoryService,
    ...tableState,
  });

  const columns = [
    { header: 'ID', accessor: 'id', sortable: true, className: 'w-16 text-slate-400 font-mono' },
    { header: 'Category Name', accessor: 'name', sortable: true, className: 'font-semibold text-[var(--text-primary)]' },
    { header: 'Description', accessor: 'description', sortable: false, cell: (row) => row.description || '—' },
    { header: 'Status', accessor: 'active', sortable: true, cell: (row) => <StatusBadge active={row.active} /> },
  ];

  const handleCreate = () => {
    setSelectedCategory(null);
    setIsModalOpen(true);
  };

  const handleEdit = (category) => {
    setSelectedCategory(category);
    setIsModalOpen(true);
  };

  const handleDeletePrompt = (category) => {
    setDeleteTarget(category);
  };

  const handleSubmitForm = async (values) => {
    if (selectedCategory) {
      await updateItem({ id: selectedCategory.id, data: values });
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
        title="Category Management"
        subtitle="Organize product categories and taxonomies"
        onSearch={(kw) => { tableState.setKeyword(kw); tableState.setPage(0); }}
        onRefresh={refetch}
        onAdd={handleCreate}
        addLabel="Add Category"
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
        emptyTitle="No categories found"
        emptyDescription="Create your first product category to start organizing stock."
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
        title={selectedCategory ? 'Edit Category' : 'Create Category'}
      >
        <CategoryForm
          initialValues={selectedCategory}
          onSubmit={handleSubmitForm}
          isLoading={isCreating || isUpdating}
          onCancel={() => setIsModalOpen(false)}
        />
      </FormModal>

      <ConfirmDeleteDialog
        isOpen={!!deleteTarget}
        onClose={() => setDeleteTarget(null)}
        onConfirm={handleConfirmDelete}
        title="Deactivate Category"
        message={`Are you sure you want to soft-delete category "${deleteTarget?.name}"?`}
        isLoading={isDeleting}
      />
    </div>
  );
}
