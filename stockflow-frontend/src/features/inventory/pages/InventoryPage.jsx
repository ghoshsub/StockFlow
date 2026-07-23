import { useState, useEffect } from 'react';
import { Boxes, ArrowDownRight, ArrowUpRight, SlidersHorizontal, Filter } from 'lucide-react';
import { useStockMovements, useStockIn, useStockOut, useStockAdjust } from '../hooks/useInventory';
import { useDashboardSummary, useInventoryAnalytics, useCategoryDistribution } from '@/features/dashboard/hooks/useDashboard';
import { StatCard } from '@/shared/components/ui/StatCard';
import { DataTable } from '@/shared/components/crud/DataTable';
import { Pagination } from '@/shared/components/crud/Pagination';
import { Button } from '@/shared/components/ui/Button';
import { Badge } from '@/shared/components/ui/Badge';
import { StockMovementModal } from '../components/StockMovementModal';
import { CategoryChart } from '@/features/dashboard/charts/CategoryChart';
import { LowStockWidget } from '@/features/dashboard/components/LowStockWidget';
import { formatCurrency, formatDateTime } from '@/shared/utils/formatters';
import { warehouseService } from '@/features/warehouse/services/warehouseService';
import { productService } from '@/features/product/services/productService';
import useAuthStore from '@/store/authStore';

export function InventoryPage() {
  const { role } = useAuthStore();
  const [page, setPage] = useState(0);
  const [size, setSize] = useState(10);
  const [activeTab, setActiveTab] = useState('movements'); // 'movements' | 'dashboard'

  // Filters state for movements search
  const [filters, setFilters] = useState({
    productId: '',
    movementType: '',
    warehouseId: '',
  });

  const [products, setProducts] = useState([]);
  const [warehouses, setWarehouses] = useState([]);
  const [movementModal, setMovementModal] = useState({ isOpen: false, type: 'IN' });

  const canAction = ['ADMIN'].includes(role);
  const canAdjust = ['ADMIN'].includes(role);

  useEffect(() => {
    Promise.all([productService.getAllList(), warehouseService.getAllList()]).then(([prods, whs]) => {
      setProducts(prods || []);
      setWarehouses(whs || []);
    });
  }, []);

  // Dashboard queries
  const { data: summary, isLoading: loadingSummary } = useDashboardSummary();
  const { data: inventoryAnalytics, isLoading: loadingInventory } = useInventoryAnalytics();
  const { data: categoryDist, isLoading: loadingCatChart } = useCategoryDistribution();

  // Movement history query
  const { data: movementsData, isLoading: loadingMovements, refetch: refetchMovements } = useStockMovements({
    ...filters,
    page,
    size,
  });

  const { mutateAsync: stockIn, isPending: isStockingIn } = useStockIn();
  const { mutateAsync: stockOut, isPending: isStockingOut } = useStockOut();
  const { mutateAsync: adjustStock, isPending: isAdjusting } = useStockAdjust();

  const movementColumns = [
    {
      header: 'Ref Number',
      accessor: 'referenceNumber',
      sortable: false,
      cell: (row) => <span className="font-mono text-indigo-400 font-semibold">{row.referenceNumber || 'TRX-N/A'}</span>,
    },
    {
      header: 'Product Name',
      accessor: 'productName',
      sortable: false,
      cell: (row) => (
        <div>
          <p className="font-semibold text-[var(--text-primary)]">{row.productName}</p>
          <p className="text-[10px] text-[var(--text-muted)]">SKU: {row.sku || 'N/A'}</p>
        </div>
      ),
    },
    { header: 'Warehouse', accessor: 'warehouseName', sortable: false, cell: (r) => r.warehouseName || '—' },
    {
      header: 'Movement Type',
      accessor: 'movementType',
      sortable: false,
      cell: (row) => {
        const isInc = row.movementType === 'STOCK_IN' || row.movementType === 'PURCHASE';
        return (
          <Badge variant={isInc ? 'success' : row.movementType === 'ADJUSTMENT' ? 'info' : 'danger'}>
            {row.movementType}
          </Badge>
        );
      },
    },
    {
      header: 'Qty Change',
      accessor: 'quantityChanged',
      sortable: false,
      cell: (row) => (
        <span className={`font-bold tabular-nums ${row.quantityChanged > 0 ? 'text-emerald-400' : 'text-rose-400'}`}>
          {row.quantityChanged > 0 ? `+${row.quantityChanged}` : row.quantityChanged}
        </span>
      ),
    },
    {
      header: 'Before → After',
      accessor: 'previousQuantity',
      sortable: false,
      cell: (row) => (
        <span className="font-mono text-[10px] text-[var(--text-secondary)]">
          {row.previousQuantity} → <strong className="text-[var(--text-primary)]">{row.newQuantity}</strong>
        </span>
      ),
    },
    {
      header: 'Date & Time',
      accessor: 'movementDate',
      sortable: true,
      cell: (row) => formatDateTime(row.movementDate),
    },
    { header: 'Performed By', accessor: 'createdByName', sortable: false, cell: (r) => r.createdByName || 'System' },
  ];

  const handleMovementSubmit = async (values) => {
    if (movementModal.type === 'IN') {
      await stockIn(values);
    } else if (movementModal.type === 'OUT') {
      await stockOut(values);
    } else if (movementModal.type === 'ADJUSTMENT') {
      await adjustStock(values);
    }
    setMovementModal({ isOpen: false, type: 'IN' });
    refetchMovements();
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-xl font-bold text-[var(--text-primary)]">Inventory & Stock Control</h1>
          <p className="text-xs text-[var(--text-muted)] mt-0.5">Real-time stock movement audit logs and stock adjustment workflow.</p>
        </div>

        {canAction && (
          <div className="flex items-center space-x-2">
            <Button
              variant="primary"
              size="sm"
              icon={ArrowDownRight}
              onClick={() => setMovementModal({ isOpen: true, type: 'IN' })}
            >
              Stock In
            </Button>
            <Button
              variant="danger"
              size="sm"
              icon={ArrowUpRight}
              onClick={() => setMovementModal({ isOpen: true, type: 'OUT' })}
            >
              Stock Out
            </Button>
            {canAdjust && (
              <Button
                variant="secondary"
                size="sm"
                icon={SlidersHorizontal}
                onClick={() => setMovementModal({ isOpen: true, type: 'ADJUSTMENT' })}
              >
                Adjust Stock
              </Button>
            )}
          </div>
        )}
      </div>

      {/* KPI Cards Overview */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <StatCard
          label="Total Inventory Value"
          value={formatCurrency(summary?.currentInventoryValue)}
          icon={Boxes}
          colorClass="text-indigo-400"
          bgClass="bg-indigo-500/10"
          loading={loadingSummary}
        />
        <StatCard
          label="Low Stock Items"
          value={inventoryAnalytics?.lowStockCount ?? 0}
          icon={ArrowUpRight}
          colorClass="text-amber-400"
          bgClass="bg-amber-500/10"
          loading={loadingInventory}
        />
        <StatCard
          label="Out of Stock Items"
          value={inventoryAnalytics?.outOfStockCount ?? 0}
          icon={ArrowDownRight}
          colorClass="text-rose-400"
          bgClass="bg-rose-500/10"
          loading={loadingInventory}
        />
        <StatCard
          label="Active Warehouses"
          value={summary?.totalWarehouses ?? 0}
          icon={Boxes}
          colorClass="text-emerald-400"
          bgClass="bg-emerald-500/10"
          loading={loadingSummary}
        />
      </div>

      {/* Tab Controls */}
      <div className="flex items-center space-x-4 border-b border-[var(--border-color)] text-xs font-semibold">
        <button
          onClick={() => setActiveTab('movements')}
          className={`pb-2.5 transition-colors border-b-2 ${
            activeTab === 'movements'
              ? 'border-indigo-500 text-indigo-400'
              : 'border-transparent text-[var(--text-muted)] hover:text-[var(--text-primary)]'
          }`}
        >
          Stock Movement History Logs
        </button>
        <button
          onClick={() => setActiveTab('dashboard')}
          className={`pb-2.5 transition-colors border-b-2 ${
            activeTab === 'dashboard'
              ? 'border-indigo-500 text-indigo-400'
              : 'border-transparent text-[var(--text-muted)] hover:text-[var(--text-primary)]'
          }`}
        >
          Analytics & Valuation Charts
        </button>
      </div>

      {/* TAB 1: Stock Movement History Logs */}
      {activeTab === 'movements' && (
        <div className="space-y-4">
          {/* Movement Filters Bar */}
          <div className="flex flex-wrap items-center gap-3 p-3 rounded-xl border border-[var(--border-color)] bg-[var(--bg-card)] text-xs">
            <span className="font-semibold text-[var(--text-secondary)] flex items-center gap-1">
              <Filter size={14} /> Filter Movement Logs:
            </span>

            <select
              value={filters.productId}
              onChange={(e) => setFilters({ ...filters, productId: e.target.value })}
              className="rounded-lg border bg-[var(--bg-secondary)] border-[var(--border-color)] text-[var(--text-primary)] px-2.5 py-1.5 focus:outline-none focus:ring-1 focus:ring-indigo-500"
            >
              <option value="">All Products</option>
              {products.map((p) => (
                <option key={p.id} value={p.id}>{p.name}</option>
              ))}
            </select>

            <select
              value={filters.movementType}
              onChange={(e) => setFilters({ ...filters, movementType: e.target.value })}
              className="rounded-lg border bg-[var(--bg-secondary)] border-[var(--border-color)] text-[var(--text-primary)] px-2.5 py-1.5 focus:outline-none focus:ring-1 focus:ring-indigo-500"
            >
              <option value="">All Movement Types</option>
              <option value="STOCK_IN">STOCK_IN</option>
              <option value="STOCK_OUT">STOCK_OUT</option>
              <option value="ADJUSTMENT">ADJUSTMENT</option>
              <option value="PURCHASE">PURCHASE</option>
              <option value="SALE">SALE</option>
            </select>

            {(filters.productId || filters.movementType) && (
              <button
                onClick={() => setFilters({ productId: '', movementType: '', warehouseId: '' })}
                className="text-xs text-rose-400 hover:underline ml-auto"
              >
                Clear Filters
              </button>
            )}
          </div>

          <DataTable
            columns={movementColumns}
            data={movementsData?.content || []}
            isLoading={loadingMovements}
            canEdit={false}
            canDelete={false}
            emptyTitle="No stock movements recorded"
            emptyDescription="Perform a Stock In or Stock Out operation to create movement logs."
          />

          <Pagination
            page={page}
            size={size}
            totalPages={movementsData?.totalPages || 0}
            totalElements={movementsData?.totalElements || 0}
            onPageChange={setPage}
            onSizeChange={(s) => { setSize(s); setPage(0); }}
          />
        </div>
      )}

      {/* TAB 2: Analytics & Valuation Charts */}
      {activeTab === 'dashboard' && (
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
          <CategoryChart data={categoryDist || []} isLoading={loadingCatChart} />
          <LowStockWidget products={inventoryAnalytics?.lowStockProducts || []} isLoading={loadingInventory} />
        </div>
      )}

      {/* Stock Action Modal */}
      <StockMovementModal
        isOpen={movementModal.isOpen}
        type={movementModal.type}
        onClose={() => setMovementModal({ isOpen: false, type: 'IN' })}
        onSubmit={handleMovementSubmit}
        isLoading={isStockingIn || isStockingOut || isAdjusting}
      />
    </div>
  );
}
