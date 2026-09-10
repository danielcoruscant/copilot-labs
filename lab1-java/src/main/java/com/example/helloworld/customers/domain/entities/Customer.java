package com.example.helloworld.customers.domain.entities;

import java.util.UUID;

public record Customer(UUID id, CustomerName name, CustomerEmail email, long version) {

    public static Customer create(String name, String email) {
        return new Customer(UUID.randomUUID(), new CustomerName(name), new CustomerEmail(email), 0);
    }

    public static Customer restore(UUID id, String name, String email) {
        return restore(id, name, email, 0);
    }

    public static Customer restore(UUID id, String name, String email, long version) {
        return new Customer(id, new CustomerName(name), new CustomerEmail(email), version);
    }

    public Customer update(String name, String email) {
        return new Customer(id, new CustomerName(name), new CustomerEmail(email), version + 1);
    }
}
