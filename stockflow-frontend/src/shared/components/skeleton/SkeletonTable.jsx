/**
 * SkeletonTable — Animated pulse skeleton loader for tables.
 */
export function SkeletonTable({ rows = 5, cols = 4 }) {
  return (
    <div className="w-full space-y-3">
      {/* Header skeleton */}
      <div className="flex items-center space-x-4 border-b border-[var(--border-color)] pb-3">
        {Array.from({ length: cols }).map((_, i) => (
          <div key={i} className="skeleton h-4 flex-1 rounded" />
        ))}
      </div>
      {/* Row skeletons */}
      {Array.from({ length: rows }).map((_, rowIndex) => (
        <div key={rowIndex} className="flex items-center space-x-4 py-2">
          {Array.from({ length: cols }).map((_, colIndex) => (
            <div key={colIndex} className="skeleton h-5 flex-1 rounded" />
          ))}
        </div>
      ))}
    </div>
  );
}
