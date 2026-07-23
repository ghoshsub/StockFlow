import { useState, useEffect } from 'react';
import { Eye, Filter, RefreshCw } from 'lucide-react';
import { useTableState } from '@/shared/hooks/crud/useTableState';
import { useProducts, useCreateProduct, useUpdateProduct, useDeleteProduct } from '../hooks/useProducts';
import { Toolbar } from '@/shared/components/crud/Toolbar';
import { DataTable } from '@/shared/components/crud/DataTable';
import { Pagination } from '@/shared/components/crud/Pagination';
import { FormModal } from '@/shared/components/crud/FormModal';
import { ConfirmDeleteDialog } from '@/shared/components/crud/ConfirmDeleteDialog';
import { Badge } from '@/shared/components/ui/Badge';
import { ProductForm } from '../components/ProductForm';
import { ProductDetailsModal } from '../components/ProductDetailsModal';
import { formatCurrency } from '@/shared/utils/formatters';
import { categoryService } from '@/features/category/services/categoryService';
import { brandService } from '@/features/brand/services/brandService';
import { warehouseService } from '@/features/warehouse/services/warehouseService';
import useAuthStore from '@/store/authStore';

export function ProductPage() {
  const { role } = useAuthStore();
  const tableState = useTableState('name', 'ASC');

  // Filter Bar state
  const [filters, setFilters] = useState({
    categoryId: '',
    brandId: '',
    warehouseId: '',
    lowStockOnly: false,
  });

  // Dropdowns state
  const [categories, setCategories] = useState([]);
  const [brands, setBrands] = useState([]);
  const [warehouses, setWarehouses] = useState([]);

  // Modals state
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [selectedProduct, setSelectedProduct] = useState(null);
  const [viewProduct, setViewProduct] = useState(null);
  const [deleteTarget, setDeleteTarget] = useState(null);

  const canEdit = ['ADMIN', 'STAFF'].includes(role);
  const canDelete = ['ADMIN'].includes(role);

  useEffect(() => {
    Promise.all([
      categoryService.getAllList(),
      brandService.getAllList(),
      warehouseService.getAllList(),
    ]).then(([cats, brs, whs]) => {
      setCategories(cats || []);
      setBrands(brs || []);
      setWarehouses(whs || []);
    });
  }, []);

  const { data: pageData, isLoading, refetch } = useProducts({
    ...tableState,
    filters,
  });

  const { mutateAsync: createProduct, isPending: isCreating } = useCreateProduct();
  const { mutateAsync: updateProduct, isPending: isUpdating } = useUpdateProduct();
  const { mutateAsync: deleteProduct, isPending: isDeleting } = useDeleteProduct();

  const columns = [
    {
      header: 'SKU / Barcode',
      accessor: 'sku',
      sortable: true,
      cell: (row) => (
        <div>
          <p className="font-mono font-semibold text-indigo-400">{row.sku}</p>
          <p className="text-[10px] text-[var(--text-muted)] font-mono">{row.barcode || 'No barcode'}</p>
        </div>
      ),
    },
    {
      header: 'Product Name',
      accessor: 'name',
      sortable: true,
      cell: (row) => (
        <div className="flex items-center space-x-2">
          <div className="w-8 h-8 rounded bg-[var(--bg-secondary)] border border-[var(--border-color)] overflow-hidden shrink-0 flex items-center justify-center text-[10px] font-bold text-slate-400">
            {row.imageUrl ? (
              <img src={row.imageUrl} alt="" className="w-full h-full object-cover" />
            ) : (
              row.name.substring(0, 2).toUpperCase()
            )}
          </div>
          <div>
            <p className="font-semibold text-[var(--text-primary)]">{row.name}</p>
            <p className="text-[10px] text-[var(--text-muted)]">{row.categoryName} • {row.brandName}</p>
          </div>
        </div>
      ),
    },
    {
      header: 'Buying Price',
      accessor: 'buyingPrice',
      sortable: true,
      cell: (row) => formatCurrency(row.buyingPrice),
    },
    {
      header: 'Selling Price',
      accessor: 'sellingPrice',
      sortable: true,
      cell: (row) => <span className="font-bold text-emerald-400">{formatCurrency(row.sellingPrice)}</span>,
    },
    {
      header: 'Quantity',
      accessor: 'quantity',
      sortable: true,
      cell: (row) => (
        <span className={`font-bold tabular-nums ${row.quantity <= (row.minimumStock || 5) ? 'text-rose-400' : 'text-[var(--text-primary)]'}`}>
          {row.quantity}
        </span>
      ),
    },
    {
      header: 'Stock Status',
      accessor: 'active',
      sortable: false,
      cell: (row) => {
        const isLow = row.quantity <= (row.minimumStock || 5);
        const isOut = row.quantity <= 0;
        return (
          <Badge variant={isOut ? 'danger' : isLow ? 'warning' : 'success'}>
            {isOut ? 'OUT OF STOCK' : isLow ? 'LOW STOCK' : 'IN STOCK'}
          </Badge>
        );
      },
    },
    {
      header: 'View',
      accessor: 'details',
      sortable: false,
      cell: (row) => (
        <button
          onClick={() => setViewProduct(row)}
          className="p-1 rounded text-[var(--text-muted)] hover:text-indigo-400 hover:bg-indigo-500/10"
          title="Quick Details"
        >
          <Eye size={15} />
        </button>
      ),
    },
  ];

  const handleCreate = () => {
    setSelectedProduct(null);
    setIsModalOpen(true);
  };

  const handleEdit = (prod) => {
    setSelectedProduct(prod);
    setIsModalOpen(true);
  };

  const handleSubmitForm = async (values) => {
    if (selectedProduct) {
      await updateProduct({ id: selectedProduct.id, data: values });
    } else {
      await createProduct(values);
    }
    setIsModalOpen(false);
  };

  const handleConfirmDelete = async () => {
    if (deleteTarget) {
      await deleteProduct(deleteTarget.id);
      setDeleteTarget(null);
    }
  };

  return (
    <div className="space-y-4">
      <Toolbar
        title="Product Catalog"
        subtitle="Manage inventory items, pricing, and stock thresholds"
        onSearch={(kw) => { tableState.setKeyword(kw); tableState.setPage(0); }}
        onRefresh={refetch}
        onAdd={handleCreate}
        addLabel="Add Product"
        allowedRoles={['ADMIN', 'STAFF']}
      />

      {/* Filter Bar */}
      <div className="flex flex-wrap items-center gap-3 p-3 rounded-xl border border-[var(--border-color)] bg-[var(--bg-card)] text-xs">
        <span className="font-semibold text-[var(--text-secondary)] flex items-center gap-1">
          <Filter size={14} /> Filter Catalog:
        </span>

        <select
          value={filters.categoryId}
          onChange={(e) => setFilters({ ...filters, categoryId: e.target.value })}
          className="rounded-lg border bg-[var(--bg-secondary)] border-[var(--border-color)] text-[var(--text-primary)] px-2.5 py-1.5 focus:outline-none focus:ring-1 focus:ring-indigo-500"
        >
          <option value="">All Categories</option>
          {categories.map((c) => (
            <option key={c.id} value={c.id}>{c.name}</option>
          ))}
        </select>

        <select
          value={filters.brandId}
          onChange={(e) => setFilters({ ...filters, brandId: e.target.value })}
          className="rounded-lg border bg-[var(--bg-secondary)] border-[var(--border-color)] text-[var(--text-primary)] px-2.5 py-1.5 focus:outline-none focus:ring-1 focus:ring-indigo-500"
        >
          <option value="">All Brands</option>
          {brands.map((b) => (
            <option key={b.id} value={b.id}>{b.name}</option>
          ))}
        </select>

        <select
          value={filters.warehouseId}
          onChange={(e) => setFilters({ ...filters, warehouseId: e.target.value })}
          className="rounded-lg border bg-[var(--bg-secondary)] border-[var(--border-color)] text-[var(--text-primary)] px-2.5 py-1.5 focus:outline-none focus:ring-1 focus:ring-indigo-500"
        >
          <option value="">All Warehouses</option>
          {warehouses.map((w) => (
            <option key={w.id} value={w.id}>{w.name}</option>
          ))}
        </select>

        {(filters.categoryId || filters.brandId || filters.warehouseId) && (
          <button
            onClick={() => setFilters({ categoryId: '', brandId: '', warehouseId: '', lowStockOnly: false })}
            className="text-xs text-rose-400 hover:underline ml-auto"
          >
            Clear Filters
          </button>
        )}
      </div>

      <DataTable
        columns={columns}
        data={pageData?.content || []}
        isLoading={isLoading}
        sortBy={tableState.sortBy}
        direction={tableState.direction}
        onSort={tableState.handleSort}
        onEdit={handleEdit}
        onDelete={(prod) => setDeleteTarget(prod)}
        canEdit={canEdit}
        canDelete={canDelete}
        emptyTitle="No products match your criteria"
        emptyDescription="Add products to your catalog or adjust search filters."
      />

      <Pagination
        page={tableState.page}
        size={tableState.size}
        totalPages={pageData?.totalPages || 0}
        totalElements={pageData?.totalElements || 0}
        onPageChange={tableState.setPage}
        onSizeChange={(newSize) => { tableState.setSize(newSize); tableState.setPage(0); }}
      />

      {/* Form Modal */}
      <FormModal
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        title={selectedProduct ? 'Edit Product Catalog Item' : 'Create New Product'}
      >
        <ProductForm
          initialValues={selectedProduct}
          onSubmit={handleSubmitForm}
          isLoading={isCreating || isUpdating}
          onCancel={() => setIsModalOpen(false)}
        />
      </FormModal>

      {/* Details Modal */}
      <ProductDetailsModal
        isOpen={!!viewProduct}
        onClose={() => setViewProduct(null)}
        product={viewProduct}
      />

      {/* Delete Confirmation */}
      <ConfirmDeleteDialog
        isOpen={!!deleteTarget}
        onClose={() => setDeleteTarget(null)}
        onConfirm={handleConfirmDelete}
        title="Deactivate Product"
        message={`Are you sure you want to soft-delete product "${deleteTarget?.name}"?`}
        isLoading={isDeleting}
      />
    </div>
  );
}
