import { useState } from 'react';
import { useTableState } from '@/shared/hooks/crud/useTableState';
import { useUsers, useCreateUser, useUpdateUser, useDeleteUser } from '../hooks/useUsers';
import { Toolbar } from '@/shared/components/crud/Toolbar';
import { DataTable } from '@/shared/components/crud/DataTable';
import { Pagination } from '@/shared/components/crud/Pagination';
import { FormModal } from '@/shared/components/crud/FormModal';
import { ConfirmDeleteDialog } from '@/shared/components/crud/ConfirmDeleteDialog';
import { StatusBadge } from '@/shared/components/crud/StatusBadge';
import { Badge } from '@/shared/components/ui/Badge';
import { UserForm } from '../components/UserForm';
import useAuthStore from '@/store/authStore';

export function UserPage() {
  const { role } = useAuthStore();
  const tableState = useTableState('username', 'ASC');
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [selectedUser, setSelectedUser] = useState(null);
  const [deleteTarget, setDeleteTarget] = useState(null);

  const canManage = ['ADMIN'].includes(role);

  const { data: pageData, isLoading, refetch } = useUsers(tableState);
  const { mutateAsync: createUser, isPending: isCreating } = useCreateUser();
  const { mutateAsync: updateUser, isPending: isUpdating } = useUpdateUser();
  const { mutateAsync: deleteUser, isPending: isDeleting } = useDeleteUser();

  const roleVariantMap = {
    ADMIN: 'danger',
    STAFF: 'info',
  };

  const columns = [
    { header: 'ID', accessor: 'id', sortable: true, className: 'w-16 text-slate-400 font-mono' },
    {
      header: 'Username',
      accessor: 'username',
      sortable: true,
      cell: (row) => (
        <div>
          <p className="font-semibold text-[var(--text-primary)]">{row.username}</p>
          <p className="text-[10px] text-[var(--text-muted)]">{row.email}</p>
        </div>
      ),
    },
    {
      header: 'Full Name',
      accessor: 'name',
      sortable: false,
      cell: (row) => `${row.firstName || ''} ${row.lastName || ''}`.trim() || '—',
    },
    {
      header: 'Role',
      accessor: 'role',
      sortable: true,
      cell: (row) => (
        <Badge variant={roleVariantMap[row.role] || 'default'}>
          {row.role}
        </Badge>
      ),
    },
    { header: 'Status', accessor: 'active', sortable: true, cell: (row) => <StatusBadge active={row.active} /> },
  ];

  const handleCreate = () => {
    setSelectedUser(null);
    setIsModalOpen(true);
  };

  const handleEdit = (u) => {
    setSelectedUser(u);
    setIsModalOpen(true);
  };

  const handleSubmitForm = async (values) => {
    if (selectedUser) {
      await updateUser({ id: selectedUser.id, data: values });
    } else {
      await createUser(values);
    }
    setIsModalOpen(false);
  };

  const handleConfirmDelete = async () => {
    if (deleteTarget) {
      await deleteUser(deleteTarget.id);
      setDeleteTarget(null);
    }
  };

  return (
    <div className="space-y-4">
      <Toolbar
        title="User Management"
        subtitle="Manage system users, access credentials, and role assignments (ADMIN only)"
        onSearch={(kw) => { tableState.setKeyword(kw); tableState.setPage(0); }}
        onRefresh={refetch}
        onAdd={handleCreate}
        addLabel="Add User"
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
        onDelete={(u) => setDeleteTarget(u)}
        canEdit={canManage}
        canDelete={canManage}
        emptyTitle="No users found"
        emptyDescription="Add team members and assign appropriate system roles."
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
        title={selectedUser ? 'Edit User Account' : 'Create New User'}
      >
        <UserForm
          initialValues={selectedUser}
          onSubmit={handleSubmitForm}
          isLoading={isCreating || isUpdating}
          onCancel={() => setIsModalOpen(false)}
        />
      </FormModal>

      <ConfirmDeleteDialog
        isOpen={!!deleteTarget}
        onClose={() => setDeleteTarget(null)}
        onConfirm={handleConfirmDelete}
        title="Deactivate User Account"
        message={`Are you sure you want to soft-delete user account "${deleteTarget?.username}"?`}
        isLoading={isDeleting}
      />
    </div>
  );
}
