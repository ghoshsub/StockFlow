import { useState, useEffect } from 'react';

/**
 * useDebounce — Returns a debounced version of the input value.
 * @param {any} value — The value to debounce
 * @param {number} delay — Delay in ms (default 300)
 */
export function useDebounce(value, delay = 300) {
  const [debouncedValue, setDebouncedValue] = useState(value);

  useEffect(() => {
    const timer = setTimeout(() => setDebouncedValue(value), delay);
    return () => clearTimeout(timer);
  }, [value, delay]);

  return debouncedValue;
}
