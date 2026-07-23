import { useState } from 'react';
import { BarChart3, TrendingUp, DollarSign, ShoppingBag, Truck, Boxes } from 'lucide-react';
import {
  useFinancialReport,
  useInventoryReport,
  useSalesReport,
  usePurchaseReport,
  useExportReport,
} from '../hooks/useReports';
import { ExportBar } from '../components/ExportBar';
import { StatCard } from '@/shared/components/ui/StatCard';
import { Card } from '@/shared/components/ui/Card';
import { DataTable } from '@/shared/components/crud/DataTable';
import { formatCurrency, formatDateTime } from '@/shared/utils/formatters';
import useAuthStore from '@/store/authStore';

export function ReportsPage() {
  const { role } = useAuthStore();
  const [activeTab, setActiveTab] = useState('financial'); // 'financial' | 'inventory' | 'sales' | 'purchases'

  const canExport = ['ADMIN'].includes(role);

  const { data: financial, isLoading: loadingFinancial } = useFinancialReport();
  const { data: inventory, isLoading: loadingInventory } = useInventoryReport();
  const { data: sales, isLoading: loadingSales } = useSalesReport();
  const { data: purchases, isLoading: loadingPurchases } = usePurchaseReport();

  const { mutate: exportFile, isPending: isExporting } = useExportReport();

  const handleExport = (endpoint, defaultFilename) => (format) => {
    exportFile({ endpoint, params: { format }, filename: defaultFilename });
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-xl font-bold text-[var(--text-primary)]">Enterprise Reports & Analytics</h1>
          <p className="text-xs text-[var(--text-muted)] mt-0.5">Generate financial summaries and export downloadable audit documents.</p>
        </div>
      </div>

      {/* Tabs Row */}
      <div className="flex items-center space-x-4 border-b border-[var(--border-color)] text-xs font-semibold">
        <button
          onClick={() => setActiveTab('financial')}
          className={`pb-2.5 transition-colors border-b-2 ${
            activeTab === 'financial' ? 'border-indigo-500 text-indigo-400' : 'border-transparent text-[var(--text-muted)] hover:text-[var(--text-primary)]'
          }`}
        >
          Financial Summary
        </button>
        <button
          onClick={() => setActiveTab('inventory')}
          className={`pb-2.5 transition-colors border-b-2 ${
            activeTab === 'inventory' ? 'border-indigo-500 text-indigo-400' : 'border-transparent text-[var(--text-muted)] hover:text-[var(--text-primary)]'
          }`}
        >
          Inventory Valuation
        </button>
        <button
          onClick={() => setActiveTab('sales')}
          className={`pb-2.5 transition-colors border-b-2 ${
            activeTab === 'sales' ? 'border-indigo-500 text-indigo-400' : 'border-transparent text-[var(--text-muted)] hover:text-[var(--text-primary)]'
          }`}
        >
          Sales Performance
        </button>
        <button
          onClick={() => setActiveTab('purchases')}
          className={`pb-2.5 transition-colors border-b-2 ${
            activeTab === 'purchases' ? 'border-indigo-500 text-indigo-400' : 'border-transparent text-[var(--text-muted)] hover:text-[var(--text-primary)]'
          }`}
        >
          Procurement Purchases
        </button>
      </div>

      {/* TAB 1: Financial Summary */}
      {activeTab === 'financial' && (
        <div className="space-y-4">
          <div className="flex justify-between items-center">
            <h3 className="text-sm font-bold text-[var(--text-primary)]">Executive Financial Audit Statement</h3>
            {canExport && (
              <ExportBar onExport={handleExport('financial', 'financial_summary_report')} isLoading={isExporting} />
            )}
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
            <StatCard label="Total Revenue" value={formatCurrency(financial?.totalRevenue)} icon={DollarSign} colorClass="text-emerald-400" bgClass="bg-emerald-500/10" loading={loadingFinancial} />
            <StatCard label="Total Purchase Cost" value={formatCurrency(financial?.totalPurchaseCost)} icon={Truck} colorClass="text-sky-400" bgClass="bg-sky-500/10" loading={loadingFinancial} />
            <StatCard label="Gross Profit" value={formatCurrency(financial?.grossProfit)} icon={TrendingUp} colorClass="text-indigo-400" bgClass="bg-indigo-500/10" loading={loadingFinancial} />
            <StatCard label="Inventory Valuation" value={formatCurrency(financial?.inventoryValuation)} icon={Boxes} colorClass="text-amber-400" bgClass="bg-amber-500/10" loading={loadingFinancial} />
          </div>
        </div>
      )}

      {/* TAB 2: Inventory Report */}
      {activeTab === 'inventory' && (
        <div className="space-y-4">
          <div className="flex justify-between items-center">
            <h3 className="text-sm font-bold text-[var(--text-primary)]">Product Inventory & Stock Valuation Audit</h3>
            {canExport && (
              <ExportBar onExport={handleExport('inventory', 'inventory_report')} isLoading={isExporting} />
            )}
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
            <StatCard label="Total Active Products" value={inventory?.totalProducts ?? 0} icon={Boxes} colorClass="text-indigo-400" bgClass="bg-indigo-500/10" loading={loadingInventory} />
            <StatCard label="Low Stock Items" value={inventory?.lowStockCount ?? 0} icon={BarChart3} colorClass="text-amber-400" bgClass="bg-amber-500/10" loading={loadingInventory} />
            <StatCard label="Out of Stock Items" value={inventory?.outOfStockCount ?? 0} icon={BarChart3} colorClass="text-rose-400" bgClass="bg-rose-500/10" loading={loadingInventory} />
          </div>
        </div>
      )}

      {/* TAB 3: Sales Report */}
      {activeTab === 'sales' && (
        <div className="space-y-4">
          <div className="flex justify-between items-center">
            <h3 className="text-sm font-bold text-[var(--text-primary)]">Sales Transaction History</h3>
            {canExport && (
              <ExportBar onExport={handleExport('sales', 'sales_report')} isLoading={isExporting} />
            )}
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <StatCard label="Total Sales Transactions" value={sales?.totalSalesOrders ?? 0} icon={ShoppingBag} colorClass="text-emerald-400" bgClass="bg-emerald-500/10" loading={loadingSales} />
            <StatCard label="Total Revenue Generated" value={formatCurrency(sales?.totalRevenue)} icon={DollarSign} colorClass="text-emerald-400" bgClass="bg-emerald-500/10" loading={loadingSales} />
          </div>
        </div>
      )}

      {/* TAB 4: Purchase Report */}
      {activeTab === 'purchases' && (
        <div className="space-y-4">
          <div className="flex justify-between items-center">
            <h3 className="text-sm font-bold text-[var(--text-primary)]">Procurement Purchase Order Audit</h3>
            {canExport && (
              <ExportBar onExport={handleExport('purchases', 'purchase_report')} isLoading={isExporting} />
            )}
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <StatCard label="Total Purchase Orders" value={purchases?.totalPurchaseOrders ?? 0} icon={Truck} colorClass="text-sky-400" bgClass="bg-sky-500/10" loading={loadingPurchases} />
            <StatCard label="Total Expenditure" value={formatCurrency(purchases?.totalExpenditure)} icon={DollarSign} colorClass="text-sky-400" bgClass="bg-sky-500/10" loading={loadingPurchases} />
          </div>
        </div>
      )}
    </div>
  );
}
