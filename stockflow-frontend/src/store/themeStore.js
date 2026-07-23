import { create } from 'zustand';

const THEME_KEY = 'sf_theme';

/**
 * themeStore — Zustand store for light/dark theme.
 * Persists preference to localStorage via tokenService pattern.
 */
const useThemeStore = create((set) => ({
  theme: localStorage.getItem(THEME_KEY) ?? 'dark',

  toggleTheme: () =>
    set((state) => {
      const next = state.theme === 'dark' ? 'light' : 'dark';
      localStorage.setItem(THEME_KEY, next);
      // Apply to <html> for Tailwind v4 data-theme strategy
      document.documentElement.setAttribute('data-theme', next);
      return { theme: next };
    }),

  setTheme: (theme) => {
    localStorage.setItem(THEME_KEY, theme);
    document.documentElement.setAttribute('data-theme', theme);
    set({ theme });
  },
}));

export default useThemeStore;
