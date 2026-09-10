package com.example.helloworld.customers.infrastructure.repositories;

import com.example.helloworld.customers.domain.entities.Customer;
import com.example.helloworld.customers.domain.exceptions.CustomerNotFoundException;
import com.example.helloworld.customers.domain.exceptions.DuplicateCustomerException;
import com.example.helloworld.customers.domain.exceptions.StaleCustomerVersionException;
import com.example.helloworld.customers.domain.repositories.CustomerRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryCustomerRepository implements CustomerRepository {

    private final ConcurrentMap<UUID, Customer> customers = new ConcurrentHashMap<>();

    @Override
    public synchronized Customer save(Customer customer) {
        Optional<Customer> duplicate = findByEmail(customer.email().value());
        if (duplicate.isPresent() && !duplicate.get().id().equals(customer.id())) {
            throw new DuplicateCustomerException(customer.email().value());
        }
        customers.put(customer.id(), customer);
        return customer;
    }

    @Override
    public Optional<Customer> findByEmail(String email) {
        String normalizedEmail = email == null ? "" : email.strip().toLowerCase();
        return customers.values().stream()
                .filter(customer -> customer.email().value().equals(normalizedEmail))
                .findFirst();
    }

    @Override
    public synchronized Customer update(
            UUID customerId, String name, String email, long expectedVersion) {
        Customer current = findById(customerId);
        if (current.version() != expectedVersion) {
            throw new StaleCustomerVersionException(expectedVersion, current.version());
        }

        Customer updated = current.update(name, email);
        Optional<Customer> duplicate = findByEmail(updated.email().value());
        if (duplicate.isPresent() && !duplicate.get().id().equals(customerId)) {
            throw new DuplicateCustomerException(updated.email().value());
        }
        customers.put(customerId, updated);
        return updated;
    }

    @Override
    public List<Customer> findAll() {
        return new ArrayList<>(customers.values());
    }

    @Override
    public Customer findById(UUID customerId) {
        Customer customer = customers.get(customerId);
        if (customer == null) {
            throw new CustomerNotFoundException(customerId);
        }
        return customer;
    }

    @Override
    public void deleteById(UUID customerId) {
        if (customers.remove(customerId) == null) {
            throw new CustomerNotFoundException(customerId);
        }
    }
}
