package com.example.helloworld.customers.application.usecases;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.helloworld.customers.application.dtos.CreateCustomerDTO;
import com.example.helloworld.customers.application.dtos.CustomerDTO;
import com.example.helloworld.customers.application.dtos.UpdateCustomerDTO;
import com.example.helloworld.customers.application.idempotency.IdempotencyKeyStore;
import com.example.helloworld.customers.domain.exceptions.CustomerNotFoundException;
import com.example.helloworld.customers.domain.exceptions.IdempotencyKeyConflictException;
import com.example.helloworld.customers.domain.exceptions.StaleCustomerVersionException;
import com.example.helloworld.customers.infrastructure.repositories.InMemoryCustomerRepository;
import java.util.UUID;
import org.junit.jupiter.api.Test;

@SuppressWarnings("null")
class CustomerUseCasesTest {

    @Test
    void updateRequiresExistingCustomer() {
        CustomerUseCases useCases = new CustomerUseCases(
            new InMemoryCustomerRepository(), new IdempotencyKeyStore());

        assertThatThrownBy(() -> useCases.update(UUID.randomUUID(),
            new UpdateCustomerDTO("Ada", "ada@example.com", 0)))
                .isInstanceOf(CustomerNotFoundException.class);
    }

    @Test
    void createPersistsDomainEntity() {
        InMemoryCustomerRepository repository = new InMemoryCustomerRepository();
        CustomerUseCases useCases = new CustomerUseCases(repository, new IdempotencyKeyStore());
        CustomerDTO output = useCases.create(
            new CreateCustomerDTO("Ada Lovelace", "ada@example.com"), "create-1");

        assertThat(output.name()).isEqualTo("Ada Lovelace");
        assertThat(repository.findById(output.id()).id()).isEqualTo(output.id());
    }

    @Test
    void replaysCreateForSameIdempotencyKey() {
        CustomerUseCases useCases = new CustomerUseCases(
            new InMemoryCustomerRepository(), new IdempotencyKeyStore());

        CreateCustomerDTO command = new CreateCustomerDTO("Ada", "ada@example.com");
        CustomerDTO first = useCases.create(command, "same-key");
        CustomerDTO repeated = useCases.create(command, "same-key");

        assertThat(repeated).isEqualTo(first);
        assertThatThrownBy(() -> useCases.create(
            new CreateCustomerDTO("Different", "different@example.com"), "same-key"))
            .isInstanceOf(IdempotencyKeyConflictException.class);
        }

        @Test
        void rejectsUpdateWithStaleVersion() {
        InMemoryCustomerRepository repository = new InMemoryCustomerRepository();
        CustomerUseCases useCases = new CustomerUseCases(repository, new IdempotencyKeyStore());
        CustomerDTO created = useCases.create(
            new CreateCustomerDTO("Ada", "ada.version@example.com"), "version-key");

        CustomerDTO updated = useCases.update(created.id(),
            new UpdateCustomerDTO("Ada Byron", "ada.version@example.com", created.version()));

        assertThat(updated.version()).isEqualTo(1);
        assertThatThrownBy(() -> useCases.update(created.id(),
            new UpdateCustomerDTO("Ada Again", "ada.version@example.com", created.version())))
            .isInstanceOf(StaleCustomerVersionException.class);
        }

        @Test
        void repeatedCreateWithSameEmailReturnsExistingCustomer() {
        CustomerUseCases useCases = new CustomerUseCases(
            new InMemoryCustomerRepository(), new IdempotencyKeyStore());

        CustomerDTO first = useCases.create(new CreateCustomerDTO("Ada", "ada@example.com"), "email-1");
        CustomerDTO repeated = useCases.create(
            new CreateCustomerDTO("Different Name", "ADA@EXAMPLE.COM"), "email-2");

        assertThat(repeated).isEqualTo(first);
    }

    @Test
    void listsGetsUpdatesAndDeletesCustomers() {
        InMemoryCustomerRepository repository = new InMemoryCustomerRepository();
        CustomerUseCases useCases = new CustomerUseCases(repository, new IdempotencyKeyStore());
        CustomerDTO created = useCases.create(
            new CreateCustomerDTO("Ada", "ada.lifecycle@example.com"), "lifecycle-1");

        assertThat(useCases.list()).extracting(CustomerDTO::id).containsExactly(created.id());
        assertThat(useCases.get(created.id()).email()).isEqualTo("ada.lifecycle@example.com");
        CustomerDTO updated = useCases.update(created.id(),
            new UpdateCustomerDTO("Ada Byron", "ADA.BYRON@EXAMPLE.COM", created.version()));
        assertThat(updated.name()).isEqualTo("Ada Byron");
        assertThat(updated.email()).isEqualTo("ada.byron@example.com");

        useCases.delete(created.id());
        assertThat(useCases.list()).isEmpty();
        assertThatThrownBy(() -> useCases.delete(created.id()))
                .isInstanceOf(CustomerNotFoundException.class);
    }
}
