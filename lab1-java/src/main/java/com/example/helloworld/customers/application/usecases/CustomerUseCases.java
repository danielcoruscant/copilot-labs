package com.example.helloworld.customers.application.usecases;

import com.example.helloworld.customers.application.dtos.CreateCustomerDTO;
import com.example.helloworld.customers.application.dtos.CustomerDTO;
import com.example.helloworld.customers.application.dtos.UpdateCustomerDTO;
import com.example.helloworld.customers.application.idempotency.IdempotencyKeyStore;
import com.example.helloworld.customers.domain.entities.Customer;
import com.example.helloworld.customers.domain.exceptions.DuplicateCustomerException;
import com.example.helloworld.customers.domain.exceptions.InvalidCustomerException;
import com.example.helloworld.customers.domain.repositories.CustomerRepository;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class CustomerUseCases {

    private final CustomerRepository repository;
    private final IdempotencyKeyStore idempotencyKeys;

    public CustomerUseCases(CustomerRepository repository, IdempotencyKeyStore idempotencyKeys) {
        this.repository = repository;
        this.idempotencyKeys = idempotencyKeys;
    }

    public synchronized CustomerDTO create(CreateCustomerDTO command, String idempotencyKey) {
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            throw new InvalidCustomerException("Idempotency-Key header is required");
        }
        String fingerprint = command.name().strip() + "\n"
                + command.email().strip().toLowerCase(Locale.ROOT);
        Optional<CustomerDTO> previous = idempotencyKeys.find(idempotencyKey, fingerprint);
        if (previous.isPresent()) {
            return previous.get();
        }

        Customer existing = repository.findByEmail(command.email()).orElse(null);
        if (existing != null) {
            CustomerDTO response = CustomerDTO.from(existing);
            idempotencyKeys.save(idempotencyKey, fingerprint, response);
            return response;
        }
        Customer customer = Customer.create(command.name(), command.email());
        CustomerDTO response;
        try {
            response = CustomerDTO.from(repository.save(customer));
        } catch (DuplicateCustomerException exception) {
            response = CustomerDTO.from(repository.findByEmail(command.email()).orElseThrow());
        }
        idempotencyKeys.save(idempotencyKey, fingerprint, response);
        return response;
    }

    public List<CustomerDTO> list() {
        return repository.findAll().stream().map(CustomerDTO::from).toList();
    }

    public CustomerDTO get(UUID customerId) {
        return CustomerDTO.from(repository.findById(customerId));
    }

    public CustomerDTO update(UUID customerId, UpdateCustomerDTO command) {
        return CustomerDTO.from(repository.update(
                customerId, command.name(), command.email(), command.version()));
    }

    public void delete(UUID customerId) {
        repository.deleteById(customerId);
    }
}
