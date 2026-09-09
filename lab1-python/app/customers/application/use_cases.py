"""Casos de uso da aplicação para o agregado Customer."""

from uuid import UUID

from app.customers.application.dtos import (
    CreateCustomerDTO,
    CustomerDTO,
    UpdateCustomerDTO,
)
from app.customers.domain.entities import Customer
from app.customers.domain.repositories import CustomerRepository


class CreateCustomer:
    """Caso de uso responsável pela criação de customers."""

    def __init__(self, repository: CustomerRepository) -> None:
        """Inicializa o caso de uso com a porta de persistência."""
        self._repository = repository

    def execute(self, command: CreateCustomerDTO) -> CustomerDTO:
        """Cria, persiste e converte um customer para o DTO de saída."""
        customer = Customer.create(name=command.name, email=command.email)
        return _to_output(self._repository.save(customer))


class ListCustomers:
    """Caso de uso responsável pela consulta da coleção de customers."""

    def __init__(self, repository: CustomerRepository) -> None:
        """Inicializa o caso de uso com a porta de persistência."""
        self._repository = repository

    def execute(self) -> list[CustomerDTO]:
        """Lista customers e os converte para DTOs."""
        return [_to_output(customer) for customer in self._repository.list()]


class GetCustomer:
    """Caso de uso responsável pela consulta de um customer."""

    def __init__(self, repository: CustomerRepository) -> None:
        """Inicializa o caso de uso com a porta de persistência."""
        self._repository = repository

    def execute(self, customer_id: UUID) -> CustomerDTO:
        """Obtém o customer pelo ID e propaga o erro de ausência."""
        return _to_output(self._repository.get(customer_id))


class UpdateCustomer:
    """Caso de uso responsável pela substituição de um customer."""

    def __init__(self, repository: CustomerRepository) -> None:
        """Inicializa o caso de uso com a porta de persistência."""
        self._repository = repository

    def execute(self, customer_id: UUID, command: UpdateCustomerDTO) -> CustomerDTO:
        """Atualiza um customer reaplicando as regras do agregado.

        A entidade é carregada primeiro para garantir que o ID exista. O
        agregado então cria uma versão imutável validada, que só depois é salva.
        """
        current = self._repository.get(customer_id)
        updated = current.update(name=command.name, email=command.email)
        return _to_output(self._repository.save(updated))


class DeleteCustomer:
    """Caso de uso responsável pela remoção de um customer."""

    def __init__(self, repository: CustomerRepository) -> None:
        """Inicializa o caso de uso com a porta de persistência."""
        self._repository = repository

    def execute(self, customer_id: UUID) -> None:
        """Remove o customer e propaga erro caso ele não exista."""
        self._repository.delete(customer_id)


def _to_output(customer: Customer) -> CustomerDTO:
    """Converte a entidade em DTO sem expor value objects."""
    return CustomerDTO(
        id=customer.id,
        name=str(customer.name),
        email=str(customer.email),
    )
