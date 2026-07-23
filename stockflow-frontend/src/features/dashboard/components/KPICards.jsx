import { Package, DollarSign, ShoppingCart, TrendingUp } from 'lucide-react';
import { StatCard } from '@/shared/components/ui/StatCard';
import { SkeletonCard } from '@/shared/components/skeleton/SkeletonCard';
import { formatCurrency, formatNumber } from '@/shared/utils/formatters';

/**
 * KPICards — Display executive metrics.
 */
export function KPICards({ data, isLoading }) {
  if (isLoading) {
    return (
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 mb-6">
        <SkeletonCard />
        <SkeletonCard />
        <SkeletonCard />
        <SkeletonCard />
      </div>
    );
  }

  return (
    <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 mb-6">
      <StatCard
        label="Total Products"
        value={formatNumber(data?.totalProducts)}
        icon={Package}
        colorClass="text-sky-400"
        bgClass="bg-sky-500/10"
      />

      <StatCard
        label="Total Revenue"
        value={formatCurrency(data?.totalRevenue)}
        icon={DollarSign}
        colorClass="text-emerald-400"
        bgClass="bg-emerald-500/10"
        trend={12.5}
        trendLabel="vs last month"
      />

      <StatCard
        label="Total Sales Orders"
        value={formatNumber(data?.totalSales)}
        icon={ShoppingCart}
        colorClass="text-indigo-400"
        bgClass="bg-indigo-500/10"
      />

      <StatCard
        label="Inventory Valuation"
        value={formatCurrency(data?.currentInventoryValue)}
        icon={TrendingUp}
        colorClass="text-amber-400"
        bgClass="bg-amber-500/10"
      />
    </div>
  );
}
