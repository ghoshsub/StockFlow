import { ResponsiveContainer, AreaChart, Area, XAxis, YAxis, Tooltip, CartesianGrid } from 'recharts';
import { Card } from '@/shared/components/ui/Card';
import { SkeletonChart } from '@/shared/components/skeleton/SkeletonChart';
import { formatCurrency, formatChartMonth } from '@/shared/utils/formatters';

/**
 * RevenueChart — AreaChart showcasing monthly revenue trends.
 */
export function RevenueChart({ data = [], isLoading }) {
  if (isLoading) return <SkeletonChart height="h-72" />;

  const formattedData = data.map((item) => ({
    ...item,
    formattedMonth: formatChartMonth(item.label),
  }));

  return (
    <Card title="Monthly Revenue" subtitle="Revenue trajectory over recent months">
      <div className="h-64 w-full">
        <ResponsiveContainer width="100%" height="100%">
          <AreaChart data={formattedData} margin={{ top: 10, right: 10, left: 0, bottom: 0 }}>
            <defs>
              <linearGradient id="colorRevenue" x1="0" y1="0" x2="0" y2="1">
                <stop offset="5%" stopColor="#6366f1" stopOpacity={0.4} />
                <stop offset="95%" stopColor="#6366f1" stopOpacity={0} />
              </linearGradient>
            </defs>
            <CartesianGrid strokeDasharray="3 3" stroke="var(--border-color)" vertical={false} />
            <XAxis dataKey="formattedMonth" stroke="var(--text-muted)" fontSize={11} tickLine={false} />
            <YAxis stroke="var(--text-muted)" fontSize={11} tickLine={false} tickFormatter={(v) => `$${v}`} />
            <Tooltip
              formatter={(value) => [formatCurrency(value), 'Revenue']}
              labelFormatter={(label) => `Month: ${label}`}
            />
            <Area type="monotone" dataKey="value" stroke="#6366f1" strokeWidth={2.5} fillOpacity={1} fill="url(#colorRevenue)" />
          </AreaChart>
        </ResponsiveContainer>
      </div>
    </Card>
  );
}
