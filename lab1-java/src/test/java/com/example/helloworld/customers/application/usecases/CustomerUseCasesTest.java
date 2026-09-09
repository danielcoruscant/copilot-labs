package com.example.helloworld.customers.application.usecases;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.helloworld.customers.application.dtos.CreateCustomerDTO;
import com.example.helloworld.customers.application.dtos.CustomerDTO;
import com.example.helloworld.customers.application.dtos.UpdateCustomerDTO;
import com.example.helloworld.customers.domain.exceptions.CustomerNotFoundException;
import com.example.helloworld.customers.infrastructure.repositories.InMemoryCustomerRepository;
import java.util.UUID;
import org.junit.jupiter.api.Test;

@SuppressWarnings("null")
class CustomerUseCasesTest {

    @Test
    void updateRequiresExistingCustomer() {
        CustomerUseCases useCases = new CustomerUseCases(new InMemoryCustomerRepository());

        assertThatThrownBy(() -> useCases.update(UUID.randomUUID(),
                new UpdateCustomerDTO("Ada", "ada@example.com")))
                .isInstanceOf(CustomerNotFoundException.class);
    }

    @Test
    void createPersistsDomainEntity() {
        InMemoryCustomerRepository repository = new InMemoryCustomerRepository();
        CustomerDTO output = new CustomerUseCases(repository).create(
            new CreateCustomerDTO("Ada Lovelace", "ada@example.com"));

        assertThat(output.name()).isEqualTo("Ada Lovelace");
        assertThat(repository.findById(output.id()).id()).isEqualTo(output.id());
    }

    @Test
    void listsGetsUpdatesAndDeletesCustomers() {
        InMemoryCustomerRepository repository = new InMemoryCustomerRepository();
        CustomerUseCases useCases = new CustomerUseCases(repository);
        CustomerDTO created = useCases.create(new CreateCustomerDTO("Ada", "ada@example.com"));

        assertThat(useCases.list()).extracting(CustomerDTO::id).containsExactly(created.id());
        assertThat(useCases.get(created.id()).email()).isEqualTo("ada@example.com");
        CustomerDTO updated = useCases.update(created.id(),
                new UpdateCustomerDTO("Ada Byron", "ADA.BYRON@EXAMPLE.COM"));
        assertThat(updated.name()).isEqualTo("Ada Byron");
        assertThat(updated.email()).isEqualTo("ada.byron@example.com");

        useCases.delete(created.id());
        assertThat(useCases.list()).isEmpty();
        assertThatThrownBy(() -> useCases.delete(created.id()))
                .isInstanceOf(CustomerNotFoundException.class);
    }
}
