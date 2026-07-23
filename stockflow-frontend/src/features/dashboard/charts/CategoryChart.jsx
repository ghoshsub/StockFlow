import { ResponsiveContainer, PieChart, Pie, Cell, Tooltip, Legend } from 'recharts';
import { Card } from '@/shared/components/ui/Card';
import { SkeletonChart } from '@/shared/components/skeleton/SkeletonChart';
import { formatCurrency } from '@/shared/utils/formatters';

const COLORS = ['#6366f1', '#10b981', '#f59e0b', '#ec4899', '#8b5cf6', '#06b6d4'];

export function CategoryChart({ data = [], isLoading }) {
  if (isLoading) return <SkeletonChart height="h-72" />;

  return (
    <Card title="Category Distribution" subtitle="Valuation split across product categories">
      <div className="h-64 w-full">
        <ResponsiveContainer width="100%" height="100%">
          <PieChart>
            <Pie
              data={data}
              cx="50%"
              cy="50%"
              innerRadius={55}
              outerRadius={80}
              paddingAngle={4}
              dataKey="value"
              nameKey="label"
            >
              {data.map((_, index) => (
                <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
              ))}
            </Pie>
            <Tooltip formatter={(val) => [formatCurrency(val), 'Category Value']} />
            <Legend iconType="circle" wrapperStyle={{ fontSize: '11px', color: 'var(--text-secondary)' }} />
          </PieChart>
        </ResponsiveContainer>
      </div>
    </Card>
  );
}
