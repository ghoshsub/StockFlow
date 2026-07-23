package com.stockflow.backend.exception;

/**
 * SupplierNotFoundException — Thrown when a supplier cannot be found by ID or query.
 *
 * Extends ResourceNotFoundException so it is automatically handled by
 * GlobalExceptionHandler → HTTP 404 Not Found response.
 */
public class SupplierNotFoundException extends ResourceNotFoundException {

    /**
     * @param id the supplier ID that was not found
     */
    public SupplierNotFoundException(Long id) {
        super("Supplier not found with id: " + id);
    }

    /**
     * @param message custom message
     */
    public SupplierNotFoundException(String message) {
        super(message);
    }
}
