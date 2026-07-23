import { useState, useEffect, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import { Search, Package, Tag, Truck, Warehouse, ShoppingCart, Receipt } from 'lucide-react';
import { useDebounce } from '@/shared/hooks/useDebounce';
import { productService } from '@/features/product/services/productService';
import { categoryService } from '@/features/category/services/categoryService';
import { supplierService } from '@/features/supplier/services/supplierService';
import { warehouseService } from '@/features/warehouse/services/warehouseService';
import { purchaseService } from '@/features/purchase/services/purchaseService';
import { saleService } from '@/features/sales/services/saleService';
import { ROUTES } from '@/constants/routes';

export function GlobalSearch() {
  const navigate = useNavigate();
  const [searchTerm, setSearchTerm] = useState('');
  const [isOpen, setIsOpen] = useState(false);
  const [results, setResults] = useState(null);
  const [isSearching, setIsSearching] = useState(false);
  const inputRef = useRef(null);
  const dropdownRef = useRef(null);

  const debouncedTerm = useDebounce(searchTerm, 300);

  // Keyboard shortcut (Ctrl+K or Cmd+K)
  useEffect(() => {
    const handleKeyDown = (e) => {
      if ((e.ctrlKey || e.metaKey) && e.key === 'k') {
        e.preventDefault();
        inputRef.current?.focus();
      }
    };
    window.addEventListener('keydown', handleKeyDown);
    return () => window.removeEventListener('keydown', handleKeyDown);
  }, []);

  // Application-wide multi-entity search effect
  useEffect(() => {
    if (!debouncedTerm || debouncedTerm.trim().length < 2) {
      setResults(null);
      setIsOpen(false);
      return;
    }

    setIsSearching(true);
    setIsOpen(true);

    Promise.allSettled([
      productService.search(debouncedTerm, 0, 3),
      categoryService.search(debouncedTerm, 0, 3),
      supplierService.search(debouncedTerm, 0, 3),
      warehouseService.search(debouncedTerm, 0, 3),
      purchaseService.search({ purchaseNumber: debouncedTerm }, 0, 3),
      saleService.search({ saleNumber: debouncedTerm }, 0, 3),
    ]).then(([prods, cats, sups, whs, purch, sales]) => {
      setResults({
        products: prods.status === 'fulfilled' ? prods.value?.content || [] : [],
        categories: cats.status === 'fulfilled' ? cats.value?.content || [] : [],
        suppliers: sups.status === 'fulfilled' ? sups.value?.content || [] : [],
        warehouses: whs.status === 'fulfilled' ? whs.value?.content || [] : [],
        purchases: purch.status === 'fulfilled' ? purch.value?.content || [] : [],
        sales: sales.status === 'fulfilled' ? sales.value?.content || [] : [],
      });
      setIsSearching(false);
    });
  }, [debouncedTerm]);

  useEffect(() => {
    const handleClickOutside = (e) => {
      if (dropdownRef.current && !dropdownRef.current.contains(e.target)) {
        setIsOpen(false);
      }
    };
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  const handleSelect = (path) => {
    setIsOpen(false);
    setSearchTerm('');
    navigate(path);
  };

  return (
    <div className="relative w-64 md:w-80" ref={dropdownRef}>
      <div className="absolute inset-y-0 left-0 flex items-center pl-3 pointer-events-none text-[var(--text-muted)]">
        <Search size={16} />
      </div>
      <input
        ref={inputRef}
        type="text"
        value={searchTerm}
        onChange={(e) => setSearchTerm(e.target.value)}
        onFocus={() => searchTerm && setIsOpen(true)}
        placeholder="Search app... (Ctrl+K)"
        className="w-full pl-9 pr-8 py-1.5 text-xs rounded-lg border bg-[var(--bg-secondary)] border-[var(--border-color)] text-[var(--text-primary)] placeholder-[var(--text-muted)] focus:outline-none focus:ring-1 focus:ring-indigo-500"
      />
      <kbd className="hidden sm:inline-block absolute right-2.5 top-2 text-[10px] px-1.5 py-0.5 rounded bg-[var(--bg-primary)] text-[var(--text-muted)] border border-[var(--border-color)] font-mono">
        ⌘K
      </kbd>

      {/* Global Search Results Dropdown */}
      {isOpen && results && (
        <div className="absolute top-full left-0 mt-2 w-96 rounded-xl bg-[var(--bg-card)] border border-[var(--border-color)] shadow-2xl z-50 overflow-hidden text-xs max-h-96 overflow-y-auto">
          {isSearching ? (
            <div className="p-4 text-center text-[var(--text-muted)]">Searching across entities...</div>
          ) : (
            <div className="p-2 space-y-2 divide-y divide-[var(--border-color)]">
              {/* Products */}
              {results.products.length > 0 && (
                <div>
                  <p className="text-[10px] font-bold text-indigo-400 uppercase tracking-wider px-2 py-1 flex items-center gap-1">
                    <Package size={12} /> Products
                  </p>
                  {results.products.map((p) => (
                    <button key={p.id} onClick={() => handleSelect(ROUTES.PRODUCTS)} className="w-full text-left px-2 py-1.5 hover:bg-[var(--bg-secondary)] rounded flex justify-between">
                      <span className="font-semibold text-[var(--text-primary)]">{p.name}</span>
                      <span className="font-mono text-indigo-400">{p.sku}</span>
                    </button>
                  ))}
                </div>
              )}

              {/* Categories */}
              {results.categories.length > 0 && (
                <div className="pt-1">
                  <p className="text-[10px] font-bold text-emerald-400 uppercase tracking-wider px-2 py-1 flex items-center gap-1">
                    <Tag size={12} /> Categories
                  </p>
                  {results.categories.map((c) => (
                    <button key={c.id} onClick={() => handleSelect(ROUTES.CATEGORIES)} className="w-full text-left px-2 py-1 hover:bg-[var(--bg-secondary)] rounded">
                      <span className="font-semibold text-[var(--text-primary)]">{c.name}</span>
                    </button>
                  ))}
                </div>
              )}

              {/* Suppliers */}
              {results.suppliers.length > 0 && (
                <div className="pt-1">
                  <p className="text-[10px] font-bold text-amber-400 uppercase tracking-wider px-2 py-1 flex items-center gap-1">
                    <Truck size={12} /> Suppliers
                  </p>
                  {results.suppliers.map((s) => (
                    <button key={s.id} onClick={() => handleSelect(ROUTES.SUPPLIERS)} className="w-full text-left px-2 py-1 hover:bg-[var(--bg-secondary)] rounded">
                      <span className="font-semibold text-[var(--text-primary)]">{s.name}</span>
                    </button>
                  ))}
                </div>
              )}

              {/* Warehouses */}
              {results.warehouses.length > 0 && (
                <div className="pt-1">
                  <p className="text-[10px] font-bold text-sky-400 uppercase tracking-wider px-2 py-1 flex items-center gap-1">
                    <Warehouse size={12} /> Warehouses
                  </p>
                  {results.warehouses.map((w) => (
                    <button key={w.id} onClick={() => handleSelect(ROUTES.WAREHOUSES)} className="w-full text-left px-2 py-1 hover:bg-[var(--bg-secondary)] rounded">
                      <span className="font-semibold text-[var(--text-primary)]">{w.name} ({w.code})</span>
                    </button>
                  ))}
                </div>
              )}

              {/* Purchases */}
              {results.purchases.length > 0 && (
                <div className="pt-1">
                  <p className="text-[10px] font-bold text-purple-400 uppercase tracking-wider px-2 py-1 flex items-center gap-1">
                    <ShoppingCart size={12} /> Purchases
                  </p>
                  {results.purchases.map((p) => (
                    <button key={p.id} onClick={() => handleSelect(ROUTES.PURCHASES)} className="w-full text-left px-2 py-1 hover:bg-[var(--bg-secondary)] rounded flex justify-between">
                      <span className="font-mono font-bold text-[var(--text-primary)]">{p.purchaseNumber}</span>
                      <span>{p.supplierName}</span>
                    </button>
                  ))}
                </div>
              )}

              {/* Sales */}
              {results.sales.length > 0 && (
                <div className="pt-1">
                  <p className="text-[10px] font-bold text-rose-400 uppercase tracking-wider px-2 py-1 flex items-center gap-1">
                    <Receipt size={12} /> Sales
                  </p>
                  {results.sales.map((s) => (
                    <button key={s.id} onClick={() => handleSelect(ROUTES.SALES)} className="w-full text-left px-2 py-1 hover:bg-[var(--bg-secondary)] rounded flex justify-between">
                      <span className="font-mono font-bold text-[var(--text-primary)]">{s.saleNumber}</span>
                      <span>{s.customerName}</span>
                    </button>
                  ))}
                </div>
              )}

              {Object.values(results).every((arr) => arr.length === 0) && (
                <p className="p-3 text-center text-[var(--text-muted)]">No matching results found across StockFlow.</p>
              )}
            </div>
          )}
        </div>
      )}
    </div>
  );
}
