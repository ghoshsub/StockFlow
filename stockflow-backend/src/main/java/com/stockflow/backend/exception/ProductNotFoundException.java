package com.stockflow.backend.exception;

/**
 * ProductNotFoundException — Thrown when a product cannot be found by ID, SKU, or Barcode.
 * Extends ResourceNotFoundException → mapped to HTTP 404 by GlobalExceptionHandler.
 */
public class ProductNotFoundException extends ResourceNotFoundException {

    public ProductNotFoundException(Long id) {
        super("Product not found with id: " + id);
    }

    public ProductNotFoundException(String message) {
        super(message);
    }
}
