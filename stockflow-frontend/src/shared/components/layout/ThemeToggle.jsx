import { Sun, Moon } from 'lucide-react';
import useThemeStore from '@/store/themeStore';
import { Button } from '@/shared/components/ui/Button';

/**
 * ThemeToggle — Switch between light and dark themes.
 */
export function ThemeToggle() {
  const { theme, toggleTheme } = useThemeStore();

  return (
    <Button
      variant="ghost"
      size="sm"
      onClick={toggleTheme}
      title={`Switch to ${theme === 'dark' ? 'light' : 'dark'} mode`}
    >
      {theme === 'dark' ? <Sun size={18} className="text-amber-400" /> : <Moon size={18} className="text-indigo-600" />}
    </Button>
  );
}
