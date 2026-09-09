"""Contratos de persistência do domínio de customers."""

from typing import Protocol
from uuid import UUID

from app.customers.domain.entities import Customer


class CustomerRepository(Protocol):
    """Porta que define as operações necessárias pelo agregado Customer."""

    def save(self, customer: Customer) -> Customer:
        """Persiste e retorna o customer informado."""
        ...

    def list(self) -> list[Customer]:
        """Retorna todos os customers persistidos."""
        ...

    def get(self, customer_id: UUID) -> Customer:
        """Retorna um customer ou lança CustomerNotFoundError."""
        ...

    def delete(self, customer_id: UUID) -> None:
        """Remove um customer ou lança CustomerNotFoundError."""
        ...
