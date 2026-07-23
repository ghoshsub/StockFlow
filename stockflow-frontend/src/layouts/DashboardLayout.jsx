import { useState } from 'react';
import { Outlet } from 'react-router-dom';
import { Sidebar } from '@/shared/components/layout/Sidebar';
import { Navbar } from '@/shared/components/layout/Navbar';
import { ErrorBoundary } from '@/shared/components/ErrorBoundary';

/**
 * DashboardLayout — Main application container shell with Sidebar, Navbar, and ambient mesh gradient wallpaper.
 */
export function DashboardLayout() {
  const [sidebarOpen, setSidebarOpen] = useState(false);

  return (
    <div className="min-h-screen bg-[var(--bg-primary)] bg-mesh-gradient transition-all duration-300">
      <Sidebar isOpen={sidebarOpen} onClose={() => setSidebarOpen(false)} />
      <div className="lg:pl-64 flex flex-col min-h-screen">
        <Navbar onMenuToggle={() => setSidebarOpen(!sidebarOpen)} />
        <main className="flex-1 p-4 lg:p-8 overflow-y-auto max-w-7xl w-full mx-auto space-y-8">
          <ErrorBoundary>
            <Outlet />
          </ErrorBoundary>
        </main>
      </div>
    </div>
  );
}

