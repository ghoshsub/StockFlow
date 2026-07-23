/**
 * SkeletonCard — Animated loading skeleton for KPI cards.
 */
export function SkeletonCard() {
  return (
    <div className="rounded-xl border border-[var(--border-color)] bg-[var(--bg-card)] p-5">
      <div className="flex items-start justify-between mb-4">
        <div className="skeleton h-10 w-10 rounded-lg" />
        <div className="skeleton h-4 w-12 rounded" />
      </div>
      <div className="space-y-2">
        <div className="skeleton h-8 w-28 rounded" />
        <div className="skeleton h-3 w-20 rounded" />
      </div>
    </div>
  );
}
