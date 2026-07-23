import { useState } from 'react';

/**
 * useTableState — Hook for managing table pagination, search, and sorting.
 */
export function useTableState(initialSortBy = 'name', initialDirection = 'ASC') {
  const [page, setPage] = useState(0);
  const [size, setSize] = useState(10);
  const [sortBy, setSortBy] = useState(initialSortBy);
  const [direction, setDirection] = useState(initialDirection);
  const [keyword, setKeyword] = useState('');

  const handleSort = (field) => {
    if (sortBy === field) {
      setDirection((prev) => (prev === 'ASC' ? 'DESC' : 'ASC'));
    } else {
      setSortBy(field);
      setDirection('ASC');
    }
  };

  const resetFilters = () => {
    setPage(0);
    setKeyword('');
    setSortBy(initialSortBy);
    setDirection(initialDirection);
  };

  return {
    page,
    setPage,
    size,
    setSize,
    sortBy,
    setSortBy,
    direction,
    setDirection,
    keyword,
    setKeyword,
    handleSort,
    resetFilters,
  };
}
