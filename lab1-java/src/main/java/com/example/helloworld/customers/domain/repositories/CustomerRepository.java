package com.example.helloworld.customers.domain.repositories;

import com.example.helloworld.customers.domain.entities.Customer;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository {

    Customer save(Customer customer);

    Optional<Customer> findByEmail(String email);

    Customer update(UUID customerId, String name, String email, long expectedVersion);

    List<Customer> findAll();

    Customer findById(UUID customerId);

    void deleteById(UUID customerId);
}
