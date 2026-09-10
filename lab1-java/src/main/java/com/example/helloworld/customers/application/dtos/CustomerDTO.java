package com.example.helloworld.customers.application.dtos;

import com.example.helloworld.customers.domain.entities.Customer;
import java.util.UUID;

public record CustomerDTO(UUID id, String name, String email, long version) {

    public static CustomerDTO from(Customer customer) {
        return new CustomerDTO(customer.id(), customer.name().value(), customer.email().value(),
                customer.version());
    }
}
