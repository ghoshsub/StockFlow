package com.stockflow.backend.exception;

/**
 * CategoryNotFoundException — Thrown when a category cannot be found by ID or name.
 *
 * Extends ResourceNotFoundException so it is automatically handled by
 * GlobalExceptionHandler → HTTP 404 Not Found response.
 *
 * Using a dedicated subclass (rather than generic ResourceNotFoundException directly)
 * allows future handlers to treat category-specific 404s differently if needed.
 */
public class CategoryNotFoundException extends ResourceNotFoundException {

    /**
     * @param id the category ID that was not found
     */
    public CategoryNotFoundException(Long id) {
        super("Category not found with id: " + id);
    }

    /**
     * @param message custom message (e.g. when looking up by name)
     */
    public CategoryNotFoundException(String message) {
        super(message);
    }
}
