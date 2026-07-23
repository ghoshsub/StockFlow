import { ResponsiveContainer, BarChart, Bar, XAxis, YAxis, Tooltip, CartesianGrid } from 'recharts';
import { Card } from '@/shared/components/ui/Card';
import { SkeletonChart } from '@/shared/components/skeleton/SkeletonChart';
import { formatCurrency, formatChartMonth } from '@/shared/utils/formatters';

export function PurchaseChart({ data = [], isLoading }) {
  if (isLoading) return <SkeletonChart height="h-72" />;

  const formattedData = data.map((item) => ({
    ...item,
    formattedMonth: formatChartMonth(item.label),
  }));

  return (
    <Card title="Monthly Purchases" subtitle="Procurement expenditure overview">
      <div className="h-64 w-full">
        <ResponsiveContainer width="100%" height="100%">
          <BarChart data={formattedData} margin={{ top: 10, right: 10, left: 0, bottom: 0 }}>
            <CartesianGrid strokeDasharray="3 3" stroke="var(--border-color)" vertical={false} />
            <XAxis dataKey="formattedMonth" stroke="var(--text-muted)" fontSize={11} tickLine={false} />
            <YAxis stroke="var(--text-muted)" fontSize={11} tickLine={false} tickFormatter={(v) => `$${v}`} />
            <Tooltip formatter={(val) => [formatCurrency(val), 'Procurement Cost']} />
            <Bar dataKey="value" fill="#38bdf8" radius={[4, 4, 0, 0]} />
          </BarChart>
        </ResponsiveContainer>
      </div>
    </Card>
  );
}
