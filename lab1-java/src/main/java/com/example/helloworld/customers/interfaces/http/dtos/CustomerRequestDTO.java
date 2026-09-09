package com.example.helloworld.customers.interfaces.http.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CustomerRequestDTO(
        @NotBlank @Size(max = 120) String name,
        @NotBlank @Size(min = 3, max = 254) String email) {
}
