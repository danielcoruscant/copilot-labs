package com.example.helloworld.customers.domain.entities;

import com.example.helloworld.customers.domain.exceptions.InvalidCustomerException;
import java.util.regex.Pattern;

public record CustomerEmail(String value) {

    private static final Pattern BASIC_EMAIL =
            Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    public CustomerEmail {
        value = value == null ? "" : value.strip().toLowerCase();
        if (value.length() < 3 || value.length() > 254 || !BASIC_EMAIL.matcher(value).matches()) {
            throw new InvalidCustomerException("Customer email must be a valid address");
        }
    }

    @Override
    public String toString() {
        return value;
    }
}
