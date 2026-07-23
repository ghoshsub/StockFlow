import { useState, useRef, useEffect } from 'react';
import { Bell, AlertTriangle, Package, ShoppingBag, Receipt, Check } from 'lucide-react';
import { useInventoryAnalytics } from '@/features/dashboard/hooks/useDashboard';
import { Badge } from '@/shared/components/ui/Badge';

export function NotificationCenter() {
  const [isOpen, setIsOpen] = useState(false);
  const dropdownRef = useRef(null);

  const { data: inventoryData } = useInventoryAnalytics();
  const lowStockCount = inventoryData?.lowStockCount || 0;
  const outOfStockCount = inventoryData?.outOfStockCount || 0;

  // Build notifications array dynamically
  const notifications = [
    ...(outOfStockCount > 0
      ? [{ id: 1, type: 'out', title: 'Out of Stock Alert!', message: `${outOfStockCount} items have 0 quantity`, time: 'Just now' }]
      : []),
    ...(lowStockCount > 0
      ? [{ id: 2, type: 'low', title: 'Low Stock Threshold Warning', message: `${lowStockCount} items are running low`, time: '5m ago' }]
      : []),
    { id: 3, type: 'sale', title: 'Sales Sync Operational', message: 'Real-time sales integration active', time: '1h ago' },
  ];

  useEffect(() => {
    const handleClickOutside = (e) => {
      if (dropdownRef.current && !dropdownRef.current.contains(e.target)) {
        setIsOpen(false);
      }
    };
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  return (
    <div className="relative" ref={dropdownRef}>
      <button
        onClick={() => setIsOpen(!isOpen)}
        className="p-2 rounded-lg text-[var(--text-muted)] hover:bg-[var(--bg-secondary)] relative"
        title="Notification Center"
      >
        <Bell size={18} />
        {notifications.length > 0 && (
          <span className="absolute top-1.5 right-1.5 w-2 h-2 rounded-full bg-rose-500 animate-pulse" />
        )}
      </button>

      {isOpen && (
        <div className="absolute right-0 mt-2 w-80 rounded-xl bg-[var(--bg-card)] border border-[var(--border-color)] shadow-2xl z-50 overflow-hidden animate-in fade-in zoom-in duration-150 text-xs">
          <div className="flex items-center justify-between px-4 py-3 border-b border-[var(--border-color)]">
            <span className="font-bold text-[var(--text-primary)]">System Notifications</span>
            <Badge variant="primary">{notifications.length} New</Badge>
          </div>

          <div className="divide-y divide-[var(--border-color)] max-h-64 overflow-y-auto">
            {notifications.map((n) => (
              <div key={n.id} className="p-3 hover:bg-[var(--bg-secondary)] transition-colors flex items-start space-x-3">
                <div className={`p-1.5 rounded-lg shrink-0 ${n.type === 'out' ? 'bg-rose-500/10 text-rose-400' : n.type === 'low' ? 'bg-amber-500/10 text-amber-400' : 'bg-indigo-500/10 text-indigo-400'}`}>
                  {n.type === 'out' || n.type === 'low' ? <AlertTriangle size={16} /> : <Package size={16} />}
                </div>
                <div className="flex-1 space-y-0.5">
                  <p className="font-semibold text-[var(--text-primary)]">{n.title}</p>
                  <p className="text-[var(--text-muted)] text-[11px]">{n.message}</p>
                  <p className="text-[9px] text-slate-500">{n.time}</p>
                </div>
              </div>
            ))}
          </div>

          <div className="p-2 border-t border-[var(--border-color)] text-center">
            <button onClick={() => setIsOpen(false)} className="text-[10px] text-indigo-400 hover:underline">
              Close Notifications
            </button>
          </div>
        </div>
      )}
    </div>
  );
}
