package com.example.helloworld.customers.domain.entities;

import com.example.helloworld.customers.domain.exceptions.InvalidCustomerException;

public record CustomerName(String value) {

    public CustomerName {
        value = value == null ? "" : value.strip();
        if (value.length() < 1 || value.length() > 120) {
            throw new InvalidCustomerException(
                    "Customer name must contain between 1 and 120 characters");
        }
    }

    @Override
    public String toString() {
        return value;
    }
}
