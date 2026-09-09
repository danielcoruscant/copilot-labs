package com.example.helloworld.customers.domain.entities;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.helloworld.customers.domain.exceptions.InvalidCustomerException;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class CustomerDomainTest {

    @Test
    void normalizesNameAndEmail() {
        Customer customer = Customer.create(" Ada Lovelace ", "ADA@EXAMPLE.COM");

        assertThat(customer.name().value()).isEqualTo("Ada Lovelace");
        assertThat(customer.email().value()).isEqualTo("ada@example.com");
    }

    @Test
    void rejectsInvalidEmail() {
        assertThatThrownBy(() -> Customer.create("Ada Lovelace", "invalid-email"))
                .isInstanceOf(InvalidCustomerException.class);
    }

    @Test
    void restoresAndUpdatesCustomerKeepingItsIdentity() {
        UUID customerId = UUID.randomUUID();
        Customer customer = Customer.restore(customerId, "Ada", "ADA@EXAMPLE.COM");

        Customer updated = customer.update(" Ada Byron ", "ada.byron@example.com");

        assertThat(updated.id()).isEqualTo(customerId);
        assertThat(updated.name().toString()).isEqualTo("Ada Byron");
        assertThat(updated.email().toString()).isEqualTo("ada.byron@example.com");
    }

    @Test
    void rejectsInvalidNameAndEmailBoundaries() {
        assertThatThrownBy(() -> Customer.create("", "ada@example.com"))
                .isInstanceOf(InvalidCustomerException.class);
        assertThatThrownBy(() -> Customer.create("Ada", "a@b"))
                .isInstanceOf(InvalidCustomerException.class);
        assertThatThrownBy(() -> Customer.create("Ada", "@example.com"))
                .isInstanceOf(InvalidCustomerException.class);
    }
}
