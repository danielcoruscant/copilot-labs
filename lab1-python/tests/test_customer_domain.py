"""Testes unitários das regras e casos de uso do bounded context."""

from uuid import uuid4

import pytest

from app.customers.application.dtos import CreateCustomerDTO, UpdateCustomerDTO
from app.customers.application.use_cases import CreateCustomer, UpdateCustomer
from app.customers.domain.entities import Customer
from app.customers.domain.exceptions import CustomerNotFoundError, InvalidCustomerError
from app.customers.infrastructure.repositories import InMemoryCustomerRepository


def test_customer_normalizes_name_and_email() -> None:
    """Value objects devem normalizar dados antes de expô-los."""
    customer = Customer.create(" Ada Lovelace ", "ADA@EXAMPLE.COM")

    assert str(customer.name) == "Ada Lovelace"
    assert str(customer.email) == "ada@example.com"


def test_customer_rejects_invalid_email() -> None:
    """O agregado não pode ser criado fora das invariantes de email."""
    with pytest.raises(InvalidCustomerError):
        Customer.create("Ada Lovelace", "invalid-email")


def test_update_requires_existing_customer() -> None:
    """O caso de uso não deve criar um registro durante uma atualização."""
    repository = InMemoryCustomerRepository()
    use_case = UpdateCustomer(repository)

    with pytest.raises(CustomerNotFoundError):
        use_case.execute(
            uuid4(),
            command=UpdateCustomerDTO("Ada", "ada@example.com"),
        )


def test_create_use_case_persists_domain_entity() -> None:
    """O caso de uso deve criar e persistir o agregado."""
    repository = InMemoryCustomerRepository()
    output = CreateCustomer(repository).execute(
        CreateCustomerDTO("Ada Lovelace", "ada@example.com")
    )

    assert output.name == "Ada Lovelace"
    assert repository.get(output.id).id == output.id
