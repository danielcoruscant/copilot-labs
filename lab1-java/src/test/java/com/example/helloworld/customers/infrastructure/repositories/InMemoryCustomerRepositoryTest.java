package com.example.helloworld.customers.infrastructure.repositories;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.helloworld.customers.domain.entities.Customer;
import com.example.helloworld.customers.domain.exceptions.CustomerNotFoundException;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class InMemoryCustomerRepositoryTest {

    private final InMemoryCustomerRepository repository = new InMemoryCustomerRepository();

    @Test
    void savesReplacesListsAndDeletesCustomers() {
        Customer customer = Customer.create("Ada", "ada@example.com");
        repository.save(customer);

        assertThat(repository.findAll()).containsExactly(customer);
        Customer updated = customer.update("Ada Byron", "ada.byron@example.com");
        assertThat(repository.save(updated)).isEqualTo(updated);
        assertThat(repository.findAll()).containsExactly(updated);

        repository.deleteById(customer.id());
        assertThat(repository.findAll()).isEmpty();
    }

    @Test
    void raisesNotFoundForUnknownIds() {
        UUID customerId = UUID.randomUUID();

        assertThatThrownBy(() -> repository.findById(customerId))
                .isInstanceOf(CustomerNotFoundException.class);
        assertThatThrownBy(() -> repository.deleteById(customerId))
                .isInstanceOf(CustomerNotFoundException.class);
    }
}