import { Menu, LogOut } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import { GlobalSearch } from '@/shared/components/GlobalSearch';
import { ThemeToggle } from './ThemeToggle';
import { NotificationCenter } from './NotificationCenter';
import useAuthStore from '@/store/authStore';
import { ROUTES } from '@/constants/routes';

export function Navbar({ onMenuToggle }) {
  const navigate = useNavigate();
  const { user, role, logout } = useAuthStore();

  const handleLogout = () => {
    logout();
    navigate(ROUTES.LANDING, { replace: true });
  };

  const roleBadgeClass =
    role === 'ADMIN'
      ? 'bg-indigo-500/20 text-indigo-300 border-indigo-500/30'
      : 'bg-emerald-500/20 text-emerald-300 border-emerald-500/30';

  return (
    <header className="h-20 glass-header px-4 lg:px-8 flex items-center justify-between sticky top-0 z-30 transition-all duration-300">
      <div className="flex items-center space-x-4">
        <button
          onClick={onMenuToggle}
          className="lg:hidden p-2.5 rounded-xl text-[var(--text-secondary)] hover:bg-indigo-500/10 active:scale-95 transition-all"
        >
          <Menu size={22} />
        </button>
        <GlobalSearch />
      </div>

      <div className="flex items-center space-x-3">
        {/* Theme + Notifications */}
        <div className="flex items-center space-x-2 bg-slate-500/10 p-1.5 rounded-2xl border border-[var(--border-color)]">
          <ThemeToggle />
          <NotificationCenter />
        </div>

        <div className="h-8 w-px bg-[var(--border-color)]" />

        {/* User identity */}
        <div className="flex items-center space-x-3">
          <div className="w-10 h-10 rounded-xl bg-gradient-to-tr from-indigo-600 via-purple-600 to-pink-500 p-0.5 shadow-md shadow-indigo-500/20">
            <div className="w-full h-full bg-[var(--bg-card)] rounded-[10px] flex items-center justify-center text-xs font-bold text-indigo-500 dark:text-indigo-300 font-heading">
              {user?.username?.substring(0, 2)?.toUpperCase() || 'US'}
            </div>
          </div>
          <div className="hidden md:block text-left">
            <p className="text-xs font-bold text-[var(--text-primary)] font-heading leading-none">
              {user?.username || 'User'}
            </p>
            <span
              className={`inline-block mt-1 text-[9px] font-extrabold px-2 py-0.5 rounded-full border uppercase tracking-wider font-mono ${roleBadgeClass}`}
            >
              {role || 'STAFF'}
            </span>
          </div>
        </div>

        {/* Logout button */}
        <button
          id="navbar-logout-btn"
          onClick={handleLogout}
          title="Sign Out"
          className="p-2.5 rounded-xl text-slate-400 hover:text-rose-400 hover:bg-rose-500/10 border border-transparent hover:border-rose-500/20 transition-all duration-150 active:scale-95"
        >
          <LogOut size={18} />
        </button>
      </div>
    </header>
  );
}
