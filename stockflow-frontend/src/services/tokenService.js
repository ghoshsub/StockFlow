/**
 * tokenService.js — Abstracted token/user storage layer.
 *
 * WHY: All storage logic lives here. To switch from localStorage to
 * HTTP-only cookies (or sessionStorage), only this file changes.
 * The rest of the application never directly touches storage.
 */

const TOKEN_KEY = 'sf_token';
const USER_KEY  = 'sf_user';

export const tokenService = {
  // ── Token ──────────────────────────────────────────────────────────────
  getToken:    ()        => localStorage.getItem(TOKEN_KEY),
  setToken:    (token)   => localStorage.setItem(TOKEN_KEY, token),
  removeToken: ()        => localStorage.removeItem(TOKEN_KEY),

  // ── User ───────────────────────────────────────────────────────────────
  getUser: () => {
    try {
      const raw = localStorage.getItem(USER_KEY);
      return raw ? JSON.parse(raw) : null;
    } catch {
      return null;
    }
  },
  setUser:    (user)     => localStorage.setItem(USER_KEY, JSON.stringify(user)),
  removeUser: ()         => localStorage.removeItem(USER_KEY),

  // ── Clear all ──────────────────────────────────────────────────────────
  clear: () => {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
  },
};
