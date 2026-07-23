package com.stockflow.backend.exception;

/**
 * ResourceNotFoundException — Thrown when a requested entity does not exist.
 *
 * Extends RuntimeException (unchecked) so it propagates naturally
 * without polluting method signatures with checked exceptions.
 *
 * Handled globally by GlobalExceptionHandler → returns HTTP 404.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
