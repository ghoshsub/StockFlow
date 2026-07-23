package com.stockflow.backend.exception;

/**
 * BrandNotFoundException — Thrown when a brand cannot be found by ID or name.
 *
 * Extends ResourceNotFoundException so it is automatically handled by
 * GlobalExceptionHandler → HTTP 404 Not Found response.
 */
public class BrandNotFoundException extends ResourceNotFoundException {

    /**
     * @param id the brand ID that was not found
     */
    public BrandNotFoundException(Long id) {
        super("Brand not found with id: " + id);
    }

    /**
     * @param message custom message (e.g. when looking up by name)
     */
    public BrandNotFoundException(String message) {
        super(message);
    }
}
