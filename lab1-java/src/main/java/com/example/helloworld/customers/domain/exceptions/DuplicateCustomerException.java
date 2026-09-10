package com.example.helloworld.customers.domain.exceptions;

public class DuplicateCustomerException extends RuntimeException {

    public DuplicateCustomerException(String email) {
        super("Customer email is already registered: " + email);
    }
}