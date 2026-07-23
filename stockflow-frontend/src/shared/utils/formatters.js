/**
 * formatters.js — Shared formatting utilities for currency, dates, and numbers.
 */

/**
 * Format a number as currency.
 * @param {number|string} value
 * @param {string} currency — ISO 4217 code, default 'USD'
 */
export function formatCurrency(value, currency = 'USD') {
  const num = parseFloat(value ?? 0);
  if (isNaN(num)) return '$0.00';
  return new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency,
    minimumFractionDigits: 2,
    maximumFractionDigits: 2,
  }).format(num);
}

/**
 * Format a LocalDateTime string from the backend.
 * Backend sends ISO-like: "2024-01-15T14:30:00"
 * @param {string} dateStr
 * @param {object} options — Intl.DateTimeFormat options
 */
export function formatDate(dateStr, options = {}) {
  if (!dateStr) return '—';
  const defaultOpts = {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
    ...options,
  };
  return new Intl.DateTimeFormat('en-US', defaultOpts).format(new Date(dateStr));
}

/**
 * Format a LocalDateTime string with time.
 */
export function formatDateTime(dateStr) {
  return formatDate(dateStr, {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  });
}

/**
 * Format a large number with K/M abbreviations.
 * @param {number} value
 */
export function formatCompact(value) {
  const num = parseFloat(value ?? 0);
  if (isNaN(num)) return '0';
  return new Intl.NumberFormat('en-US', {
    notation: 'compact',
    maximumFractionDigits: 1,
  }).format(num);
}

/**
 * Format a plain integer with comma separators.
 */
export function formatNumber(value) {
  const num = parseFloat(value ?? 0);
  if (isNaN(num)) return '0';
  return new Intl.NumberFormat('en-US').format(num);
}

/**
 * Format a chart label "YYYY-MM" → "Jan 2024"
 */
export function formatChartMonth(label) {
  if (!label || !label.includes('-')) return label;
  const [year, month] = label.split('-');
  const date = new Date(parseInt(year), parseInt(month) - 1, 1);
  return new Intl.DateTimeFormat('en-US', { month: 'short', year: 'numeric' }).format(date);
}
