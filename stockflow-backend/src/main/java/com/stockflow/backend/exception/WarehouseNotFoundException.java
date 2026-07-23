package com.stockflow.backend.exception;

/**
 * WarehouseNotFoundException — Thrown when a warehouse cannot be found by ID or code.
 *
 * Extends ResourceNotFoundException → mapped to HTTP 404 by GlobalExceptionHandler.
 */
public class WarehouseNotFoundException extends ResourceNotFoundException {

    public WarehouseNotFoundException(Long id) {
        super("Warehouse not found with id: " + id);
    }

    public WarehouseNotFoundException(String message) {
        super(message);
    }
}
