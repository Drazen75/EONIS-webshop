package com.furniture.store.exception;

/**
 * Thrown when initialization of an external payment provider (Stripe) fails.
 * Mapped to HTTP 502 Bad Gateway by GlobalExceptionHandler.
 */
public class PaymentInitializationException extends RuntimeException {
    public PaymentInitializationException(String message) {
        super(message);
    }

    public PaymentInitializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
