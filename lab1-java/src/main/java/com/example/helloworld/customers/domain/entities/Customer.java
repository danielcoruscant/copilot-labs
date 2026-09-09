package com.example.helloworld.customers.domain.entities;

import java.util.UUID;
import com.example.helloworld.customers.domain.exceptions.InvalidCustomerException;

public record Customer(UUID id, CustomerName name, CustomerEmail email) {

    public static Customer create(String name, String email) {
        return new Customer(UUID.randomUUID(), new CustomerName(name), new CustomerEmail(email));
    }

    public static Customer restore(UUID id, String name, String email) {
        return new Customer(id, new CustomerName(name), new CustomerEmail(email));
    }

    public Customer update(String name, String email) {
        return new Customer(id, new CustomerName(name), new CustomerEmail(email));
    }
}
