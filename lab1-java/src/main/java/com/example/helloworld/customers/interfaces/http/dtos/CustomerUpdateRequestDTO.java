package com.example.helloworld.customers.interfaces.http.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record CustomerUpdateRequestDTO(
        @NotBlank @Size(max = 120) String name,
        @NotBlank @Size(min = 3, max = 254) String email,
        @NotNull @PositiveOrZero Long version) {
}