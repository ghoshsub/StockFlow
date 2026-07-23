import { NavLink, useNavigate } from 'react-router-dom';
import { LogOut, Layers } from 'lucide-react';
import useAuthStore from '@/store/authStore';
import { getNavItemsForRole } from '@/constants/navigation';
import { ROUTES } from '@/constants/routes';

/**
 * Sidebar — Glassmorphic role-based navigation sidebar with gradient branding.
 */
export function Sidebar({ isOpen, onClose }) {
  const navigate = useNavigate();
  const { role, user, logout } = useAuthStore();
  const navItems = getNavItemsForRole(role || 'STAFF');

  const handleLogout = () => {
    logout();
    navigate(ROUTES.LANDING, { replace: true });
  };

  return (
    <aside
      className={`fixed inset-y-0 left-0 z-40 w-64 glass-sidebar flex flex-col justify-between transition-all duration-300 lg:translate-x-0 ${
        isOpen ? 'translate-x-0 shadow-2xl' : '-translate-x-full'
      }`}
    >
      <div>
        {/* Logo Branding */}
        <div className="h-20 flex items-center justify-between px-6 border-b border-indigo-900/40 bg-gradient-to-r from-indigo-950/60 to-purple-950/30">
          <div className="flex items-center space-x-3">
            <div className="h-10 w-10 rounded-xl bg-gradient-to-tr from-indigo-600 via-purple-600 to-pink-500 flex items-center justify-center shadow-lg shadow-indigo-500/30">
              <Layers className="text-white" size={22} />
            </div>
            <div>
              <span className="text-lg font-extrabold tracking-tight text-white font-heading bg-gradient-to-r from-white via-slate-100 to-indigo-200 bg-clip-text text-transparent">
                StockFlow
              </span>
              <div className="flex items-center space-x-1.5 mt-0.5">
                <span className="h-1.5 w-1.5 rounded-full bg-emerald-400 animate-ping" />
                <span className="text-[10px] font-bold text-indigo-300 tracking-wider font-mono">ENTERPRISE</span>
              </div>
            </div>
          </div>
        </div>

        {/* Navigation Items */}
        <nav className="p-4 space-y-1.5">
          {navItems.map((item) => {
            const Icon = item.icon;
            return (
              <NavLink
                key={item.path}
                to={item.path}
                onClick={onClose}
                className={({ isActive }) =>
                  `flex items-center px-4 py-3 text-sm font-semibold rounded-xl transition-all duration-200 group font-heading ${
                    isActive
                      ? 'nav-link-active'
                      : 'text-slate-400 hover:bg-indigo-500/10 hover:text-slate-100'
                  }`
                }
              >
                <Icon size={19} className="mr-3 transition-transform duration-200 group-hover:scale-110" />
                <span>{item.label}</span>
              </NavLink>
            );
          })}
        </nav>
      </div>

      {/* User Profile Card */}
      <div className="p-4 border-t border-indigo-900/40 bg-indigo-950/40">
        <div className="flex items-center justify-between p-2 rounded-xl bg-slate-900/50 border border-indigo-500/20">
          <div className="flex items-center space-x-3 overflow-hidden">
            <div className="h-9 w-9 rounded-lg bg-gradient-to-tr from-indigo-500 to-purple-600 flex items-center justify-center font-bold text-white text-xs font-heading">
              {user?.username?.substring(0, 2)?.toUpperCase() || 'US'}
            </div>
            <div className="overflow-hidden">
              <p className="text-xs font-bold text-white truncate font-heading">{user?.username || 'User'}</p>
              <span className="inline-block text-[9px] font-extrabold text-indigo-300 bg-indigo-500/20 px-2 py-0.5 rounded-full uppercase tracking-wider font-mono">
                {role || 'STAFF'}
              </span>
            </div>
          </div>
          <button
            id="sidebar-logout-btn"
            onClick={handleLogout}
            title="Sign Out"
            className="p-2 text-slate-400 hover:text-rose-400 hover:bg-rose-500/15 rounded-lg transition-all duration-150 active:scale-95"
          >
            <LogOut size={18} />
          </button>
        </div>
      </div>
    </aside>
  );
}

