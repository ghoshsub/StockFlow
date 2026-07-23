package com.stockflow.backend.exception;

/**
 * DuplicateResourceException — Thrown when a uniqueness constraint is violated.
 *
 * Examples: registering with an already-taken username or email.
 * Handled globally by GlobalExceptionHandler → returns HTTP 409 Conflict.
 */
public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String message) {
        super(message);
    }
}
