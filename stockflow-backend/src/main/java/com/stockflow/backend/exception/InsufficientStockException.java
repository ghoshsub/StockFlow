package com.stockflow.backend.exception;

/**
 * InsufficientStockException — Thrown when a Stock Out request exceeds available product quantity.
 * Maps to HTTP 422 Unprocessable Entity via GlobalExceptionHandler.
 */
public class InsufficientStockException extends RuntimeException {

    public InsufficientStockException(String productName, int available, int requested) {
        super(String.format(
                "Insufficient stock for product '%s'. Available: %d, Requested: %d",
                productName, available, requested));
    }

    public InsufficientStockException(String message) {
        super(message);
    }
}
