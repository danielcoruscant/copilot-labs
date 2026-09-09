package com.example.helloworld.customers.domain.repositories;

import com.example.helloworld.customers.domain.entities.Customer;
import java.util.List;
import java.util.UUID;

public interface CustomerRepository {

    Customer save(Customer customer);

    List<Customer> findAll();

    Customer findById(UUID customerId);

    void deleteById(UUID customerId);
}
