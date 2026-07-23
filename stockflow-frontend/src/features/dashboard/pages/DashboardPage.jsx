import {
  useDashboardSummary,
  useInventoryAnalytics,
  useSalesAnalytics,
  usePurchaseAnalytics,
  useRevenueChart,
  usePurchasesChart,
  useCategoryDistribution,
  useWarehouseDistribution,
} from '../hooks/useDashboard';
import { StatCard } from '@/shared/components/ui/StatCard';
import { RevenueChart } from '../charts/RevenueChart';
import { PurchaseChart } from '../charts/PurchaseChart';
import { CategoryChart } from '../charts/CategoryChart';
import { WarehouseChart } from '../charts/WarehouseChart';
import { LowStockWidget } from '../components/LowStockWidget';
import { TopProductsWidget } from '../components/TopProductsWidget';
import { TopSuppliersWidget } from '../components/TopSuppliersWidget';
import { RecentPurchasesWidget } from '../components/RecentPurchasesWidget';
import { RecentSalesWidget } from '../components/RecentSalesWidget';
import {
  DollarSign,
  TrendingUp,
  Package,
  Truck,
  Warehouse,
  ShoppingCart,
  Receipt,
  AlertTriangle,
  MinusCircle,
  BarChart3,
  Sparkles,
  Activity,
} from 'lucide-react';
import { formatCurrency, formatNumber } from '@/shared/utils/formatters';

export function DashboardPage() {
  const { data: summary, isLoading: loadingSummary } = useDashboardSummary();
  const { data: inventory, isLoading: loadingInventory } = useInventoryAnalytics();
  const { data: sales, isLoading: loadingSales } = useSalesAnalytics();
  const { data: purchases, isLoading: loadingPurchases } = usePurchaseAnalytics();
  const { data: revenueChart, isLoading: loadingRevenueChart } = useRevenueChart();
  const { data: purchasesChart, isLoading: loadingPurchasesChart } = usePurchasesChart();
  const { data: categoryChart, isLoading: loadingCategoryChart } = useCategoryDistribution();
  const { data: warehouseChart, isLoading: loadingWarehouseChart } = useWarehouseDistribution();

  // Frontend calculation: Gross Profit = Revenue - Purchase Cost
  const grossProfit = (summary?.totalRevenue || 0) - (summary?.totalPurchaseCost || 0);

  return (
    <div className="space-y-8 animate-in fade-in duration-500">
      {/* Page Header Banner */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 p-6 glass-card rounded-2xl border border-[var(--glass-border)] bg-gradient-to-r from-indigo-900/20 via-purple-900/10 to-transparent">
        <div>
          <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-indigo-500/15 text-indigo-400 text-xs font-bold font-heading uppercase tracking-wider mb-2">
            <Activity size={14} className="text-emerald-400" /> Live Operational Intelligence
          </div>
          <h1 className="text-2xl sm:text-3xl font-extrabold text-[var(--text-primary)] font-heading tracking-tight">
            Executive Dashboard
          </h1>
          <p className="text-xs sm:text-sm text-[var(--text-secondary)] mt-1 font-medium">
            Real-time financial summary, procurement trends, inventory valuations, &amp; order metrics.
          </p>
        </div>

        <div className="flex items-center gap-2 self-start md:self-auto">
          <div className="px-4 py-2 rounded-xl bg-emerald-500/10 border border-emerald-500/20 text-emerald-400 text-xs font-bold font-heading flex items-center gap-2">
            <span className="h-2 w-2 rounded-full bg-emerald-400 animate-ping" />
            <span>Database Synced</span>
          </div>
        </div>
      </div>

      {/* Row 1 — Financial KPIs (4 cards) */}
      <div className="space-y-3">
        <div className="flex items-center justify-between">
          <h2 className="text-xs font-extrabold uppercase tracking-wider text-[var(--text-muted)] font-heading">
            Financial Analytics
          </h2>
        </div>
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-5">
          <StatCard
            label="Total Revenue"
            value={formatCurrency(summary?.totalRevenue)}
            icon={DollarSign}
            colorClass="text-emerald-500 dark:text-emerald-400"
            bgClass="bg-gradient-to-br from-emerald-500/20 to-teal-500/10 border border-emerald-500/30"
            loading={loadingSummary}
          />
          <StatCard
            label="Purchase Cost"
            value={formatCurrency(summary?.totalPurchaseCost)}
            icon={Truck}
            colorClass="text-sky-500 dark:text-sky-400"
            bgClass="bg-gradient-to-br from-sky-500/20 to-blue-500/10 border border-sky-500/30"
            loading={loadingSummary}
          />
          <StatCard
            label="Inventory Valuation"
            value={formatCurrency(summary?.currentInventoryValue)}
            icon={TrendingUp}
            colorClass="text-amber-500 dark:text-amber-400"
            bgClass="bg-gradient-to-br from-amber-500/20 to-orange-500/10 border border-amber-500/30"
            loading={loadingSummary}
          />
          <StatCard
            label="Gross Profit"
            value={formatCurrency(grossProfit)}
            icon={BarChart3}
            colorClass="text-indigo-500 dark:text-indigo-400"
            bgClass="bg-gradient-to-br from-indigo-500/20 to-purple-500/10 border border-indigo-500/30"
            loading={loadingSummary}
          />
        </div>
      </div>

      {/* Row 2 — Operational KPIs (4 cards) */}
      <div className="space-y-3">
        <div className="flex items-center justify-between">
          <h2 className="text-xs font-extrabold uppercase tracking-wider text-[var(--text-muted)] font-heading">
            Operations & Master Data
          </h2>
        </div>
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-5">
          <StatCard
            label="Total Products"
            value={formatNumber(summary?.totalProducts)}
            icon={Package}
            colorClass="text-indigo-500 dark:text-indigo-400"
            bgClass="bg-gradient-to-br from-indigo-500/20 to-violet-500/10 border border-indigo-500/30"
            loading={loadingSummary}
          />
          <StatCard
            label="Active Suppliers"
            value={formatNumber(summary?.totalSuppliers)}
            icon={Truck}
            colorClass="text-cyan-500 dark:text-cyan-400"
            bgClass="bg-gradient-to-br from-cyan-500/20 to-teal-500/10 border border-cyan-500/30"
            loading={loadingSummary}
          />
          <StatCard
            label="Active Warehouses"
            value={formatNumber(summary?.totalWarehouses)}
            icon={Warehouse}
            colorClass="text-purple-500 dark:text-purple-400"
            bgClass="bg-gradient-to-br from-purple-500/20 to-pink-500/10 border border-purple-500/30"
            loading={loadingSummary}
          />
          <StatCard
            label="Total Sales Orders"
            value={formatNumber(summary?.totalSales)}
            icon={Receipt}
            colorClass="text-emerald-500 dark:text-emerald-400"
            bgClass="bg-gradient-to-br from-emerald-500/20 to-teal-500/10 border border-emerald-500/30"
            loading={loadingSummary}
          />
        </div>
      </div>

      {/* Row 3 — Stock Health KPIs */}
      <div className="space-y-3">
        <div className="flex items-center justify-between">
          <h2 className="text-xs font-extrabold uppercase tracking-wider text-[var(--text-muted)] font-heading">
            Procurement & Inventory Health
          </h2>
        </div>
        <div className="grid grid-cols-1 sm:grid-cols-3 gap-5">
          <StatCard
            label="Total Purchases"
            value={formatNumber(summary?.totalPurchases)}
            icon={ShoppingCart}
            colorClass="text-blue-500 dark:text-blue-400"
            bgClass="bg-gradient-to-br from-blue-500/20 to-indigo-500/10 border border-blue-500/30"
            loading={loadingSummary}
          />
          <StatCard
            label="Low Stock Alert"
            value={formatNumber(inventory?.lowStockCount)}
            icon={AlertTriangle}
            colorClass="text-amber-500 dark:text-amber-400"
            bgClass="bg-gradient-to-br from-amber-500/20 to-yellow-500/10 border border-amber-500/30"
            loading={loadingInventory}
          />
          <StatCard
            label="Out of Stock"
            value={formatNumber(inventory?.outOfStockCount)}
            icon={MinusCircle}
            colorClass="text-rose-500 dark:text-rose-400"
            bgClass="bg-gradient-to-br from-rose-500/20 to-pink-500/10 border border-rose-500/30"
            loading={loadingInventory}
          />
        </div>
      </div>

      {/* Charts Row 1 — Revenue & Purchases Timeline */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <RevenueChart data={revenueChart || []} isLoading={loadingRevenueChart} />
        <PurchaseChart data={purchasesChart || []} isLoading={loadingPurchasesChart} />
      </div>

      {/* Charts Row 2 — Category & Warehouse Distribution */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <CategoryChart data={categoryChart || []} isLoading={loadingCategoryChart} />
        <WarehouseChart data={warehouseChart || []} isLoading={loadingWarehouseChart} />
      </div>

      {/* Widgets Row — Top Products / Suppliers / Low Stock */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <TopProductsWidget items={sales?.topSellingProducts || []} isLoading={loadingSales} />
        <TopSuppliersWidget suppliers={purchases?.topSuppliers || []} isLoading={loadingPurchases} />
        <LowStockWidget products={inventory?.lowStockProducts || []} isLoading={loadingInventory} />
      </div>

      {/* Activity Widgets Row */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <RecentPurchasesWidget purchases={purchases?.recentPurchases || []} isLoading={loadingPurchases} />
        <RecentSalesWidget sales={sales?.recentSales || []} isLoading={loadingSales} />
      </div>
    </div>
  );
}

