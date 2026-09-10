package com.example.helloworld.customers.interfaces.http.controllers;

import com.example.helloworld.customers.application.dtos.CreateCustomerDTO;
import com.example.helloworld.customers.application.dtos.UpdateCustomerDTO;
import com.example.helloworld.customers.application.usecases.CustomerUseCases;
import com.example.helloworld.customers.interfaces.http.dtos.CustomerRequestDTO;
import com.example.helloworld.customers.interfaces.http.dtos.CustomerResponseDTO;
import com.example.helloworld.customers.interfaces.http.dtos.DeleteCustomerResponseDTO;
import com.example.helloworld.customers.interfaces.http.dtos.CustomerUpdateRequestDTO;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerUseCases useCases;

    public CustomerController(CustomerUseCases useCases) {
        this.useCases = useCases;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerResponseDTO create(
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @Valid @RequestBody CustomerRequestDTO request) {
        return CustomerResponseDTO.from(useCases.create(
                new CreateCustomerDTO(request.name(), request.email()), idempotencyKey));
    }

    @GetMapping
    public List<CustomerResponseDTO> list() {
        return useCases.list().stream().map(CustomerResponseDTO::from).toList();
    }

    @GetMapping("/{customerId}")
    public CustomerResponseDTO get(@PathVariable UUID customerId) {
        return CustomerResponseDTO.from(useCases.get(customerId));
    }

    @PutMapping("/{customerId}")
    public CustomerResponseDTO update(
            @PathVariable UUID customerId,
            @Valid @RequestBody CustomerUpdateRequestDTO request) {
        return CustomerResponseDTO.from(useCases.update(customerId,
                new UpdateCustomerDTO(request.name(), request.email(), request.version())));
    }

    @DeleteMapping("/{customerId}")
    public DeleteCustomerResponseDTO delete(@PathVariable UUID customerId) {
        useCases.delete(customerId);
        return new DeleteCustomerResponseDTO("Customer deleted successfully");
    }
}
