package com.example.helloworld.customers.domain.exceptions;

public class IdempotencyKeyConflictException extends RuntimeException {

    public IdempotencyKeyConflictException(String key) {
        super("Idempotency-Key was already used with a different request: " + key);
    }
}