import { clsx } from 'clsx';

/**
 * cn — Class name merger utility.
 * Combines class strings, filtering falsy values.
 * Usage: cn('base', condition && 'extra', 'always')
 */
export function cn(...inputs) {
  return clsx(inputs);
}
