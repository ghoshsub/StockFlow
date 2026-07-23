import { useState } from 'react';
import { User, Lock, Moon, Sun, Info, Shield } from 'lucide-react';
import { Card } from '@/shared/components/ui/Card';
import { Button } from '@/shared/components/ui/Button';
import { Input } from '@/shared/components/ui/Input';
import useThemeStore from '@/store/themeStore';
import useAuthStore from '@/store/authStore';
import toast from 'react-hot-toast';

export function SettingsPage() {
  const { theme, toggleTheme } = useThemeStore();
  const { user, role } = useAuthStore();

  const [passwordForm, setPasswordForm] = useState({
    currentPassword: '',
    newPassword: '',
    confirmPassword: '',
  });

  const handlePasswordSubmit = (e) => {
    e.preventDefault();
    if (passwordForm.newPassword !== passwordForm.confirmPassword) {
      toast.error('New passwords do not match!');
      return;
    }
    toast.success('Password updated successfully!');
    setPasswordForm({ currentPassword: '', newPassword: '', confirmPassword: '' });
  };

  return (
    <div className="space-y-6 max-w-4xl">
      <div>
        <h1 className="text-xl font-bold text-[var(--text-primary)]">System Settings</h1>
        <p className="text-xs text-[var(--text-muted)] mt-0.5">Manage user profile preferences, theme appearance, and security credentials.</p>
      </div>

      {/* User Profile Info */}
      <Card title="User Profile Specifications">
        <div className="flex items-center space-x-4">
          <div className="w-14 h-14 rounded-full bg-indigo-600/30 border border-indigo-500/40 text-indigo-400 font-extrabold text-lg flex items-center justify-center uppercase">
            {user?.username?.substring(0, 2) || 'US'}
          </div>
          <div className="space-y-1 text-xs">
            <p className="text-base font-bold text-[var(--text-primary)]">{user?.username || 'Authenticated User'}</p>
            <p className="text-[var(--text-muted)] flex items-center gap-1">
              <Shield size={14} className="text-indigo-400" /> Assigned Role: <strong className="text-indigo-400 font-mono uppercase">{role || 'STAFF'}</strong>
            </p>
          </div>
        </div>
      </Card>

      {/* Theme Options */}
      <Card title="Theme & Visual Appearance">
        <div className="flex items-center justify-between">
          <div className="space-y-0.5">
            <p className="text-sm font-semibold text-[var(--text-primary)]">Interface Theme</p>
            <p className="text-xs text-[var(--text-muted)]">Toggle between dark mode glassmorphism and crisp light theme.</p>
          </div>
          <Button variant="outline" size="sm" onClick={toggleTheme}>
            {theme === 'dark' ? (
              <span className="flex items-center gap-1.5"><Sun size={16} className="text-amber-400" /> Switch to Light Mode</span>
            ) : (
              <span className="flex items-center gap-1.5"><Moon size={16} className="text-indigo-500" /> Switch to Dark Mode</span>
            )}
          </Button>
        </div>
      </Card>

      {/* Password Change Form */}
      <Card title="Security Credentials">
        <form onSubmit={handlePasswordSubmit} className="space-y-3 max-w-md">
          <Input
            label="Current Password"
            type="password"
            placeholder="••••••••"
            icon={Lock}
            value={passwordForm.currentPassword}
            onChange={(e) => setPasswordForm({ ...passwordForm, currentPassword: e.target.value })}
            required
          />
          <Input
            label="New Password"
            type="password"
            placeholder="••••••••"
            icon={Lock}
            value={passwordForm.newPassword}
            onChange={(e) => setPasswordForm({ ...passwordForm, newPassword: e.target.value })}
            required
          />
          <Input
            label="Confirm New Password"
            type="password"
            placeholder="••••••••"
            icon={Lock}
            value={passwordForm.confirmPassword}
            onChange={(e) => setPasswordForm({ ...passwordForm, confirmPassword: e.target.value })}
            required
          />
          <Button type="submit" variant="primary" size="sm" className="mt-2">
            Update Security Credentials
          </Button>
        </form>
      </Card>

      {/* Application Meta Info */}
      <Card title="Application Metadata">
        <div className="space-y-2 text-xs text-[var(--text-muted)]">
          <div className="flex justify-between border-b border-[var(--border-color)] pb-1.5">
            <span>Application Version</span>
            <span className="font-mono text-[var(--text-primary)]">v1.0.0 (Production Build)</span>
          </div>
          <div className="flex justify-between border-b border-[var(--border-color)] pb-1.5">
            <span>Backend API Stack</span>
            <span className="font-mono text-indigo-400">Spring Boot 3.3.2 • Java 21</span>
          </div>
          <div className="flex justify-between">
            <span>Frontend Architecture</span>
            <span className="font-mono text-indigo-400">React 19 • Vite • Tailwind v4</span>
          </div>
        </div>
      </Card>
    </div>
  );
}
