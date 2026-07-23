/**
 * SkeletonChart — Animated pulse skeleton loader for charts.
 */
export function SkeletonChart({ height = 'h-64' }) {
  return (
    <div className={`w-full ${height} flex items-end space-x-3 p-4 border border-[var(--border-color)] rounded-xl bg-[var(--bg-card)]`}>
      <div className="skeleton w-full h-1/3 rounded" />
      <div className="skeleton w-full h-2/3 rounded" />
      <div className="skeleton w-full h-1/2 rounded" />
      <div className="skeleton w-full h-5/6 rounded" />
      <div className="skeleton w-full h-3/4 rounded" />
      <div className="skeleton w-full h-2/5 rounded" />
    </div>
  );
}
