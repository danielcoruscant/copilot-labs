package com.example.helloworld.customers.application.usecases;

import com.example.helloworld.customers.application.dtos.CreateCustomerDTO;
import com.example.helloworld.customers.application.dtos.CustomerDTO;
import com.example.helloworld.customers.application.dtos.UpdateCustomerDTO;
import com.example.helloworld.customers.domain.entities.Customer;
import com.example.helloworld.customers.domain.repositories.CustomerRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class CustomerUseCases {

    private final CustomerRepository repository;

    public CustomerUseCases(CustomerRepository repository) {
        this.repository = repository;
    }

    public CustomerDTO create(CreateCustomerDTO command) {
        return CustomerDTO.from(repository.save(Customer.create(command.name(), command.email())));
    }

    public List<CustomerDTO> list() {
        return repository.findAll().stream().map(CustomerDTO::from).toList();
    }

    public CustomerDTO get(UUID customerId) {
        return CustomerDTO.from(repository.findById(customerId));
    }

    public CustomerDTO update(UUID customerId, UpdateCustomerDTO command) {
        Customer current = repository.findById(customerId);
        return CustomerDTO.from(repository.save(current.update(command.name(), command.email())));
    }

    public void delete(UUID customerId) {
        repository.deleteById(customerId);
    }
}
