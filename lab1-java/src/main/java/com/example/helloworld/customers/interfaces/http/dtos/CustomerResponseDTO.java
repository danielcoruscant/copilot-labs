package com.example.helloworld.customers.interfaces.http.dtos;

import com.example.helloworld.customers.application.dtos.CustomerDTO;
import java.util.UUID;

public record CustomerResponseDTO(UUID id, String name, String email) {

    public static CustomerResponseDTO from(CustomerDTO output) {
        return new CustomerResponseDTO(output.id(), output.name(), output.email());
    }
}
