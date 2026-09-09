"""Entidades e agregados do bounded context de customers."""

from dataclasses import dataclass
from uuid import UUID, uuid4

from app.customers.domain.value_objects import CustomerEmail, CustomerName


@dataclass(frozen=True, slots=True)
class Customer:
    """Agregado raiz que representa um customer."""

    id: UUID
    name: CustomerName
    email: CustomerEmail

    @classmethod
    def create(cls, name: str, email: str) -> "Customer":
        """Cria um customer novo com um identificador UUID único."""
        return cls(uuid4(), CustomerName(name), CustomerEmail(email))

    @classmethod
    def restore(cls, customer_id: UUID, name: str, email: str) -> "Customer":
        """Reconstrói um customer persistido sem gerar outro identificador."""
        return cls(customer_id, CustomerName(name), CustomerEmail(email))

    def update(self, name: str, email: str) -> "Customer":
        """Retorna uma cópia atualizada, reaplicando as invariantes."""
        return Customer(self.id, CustomerName(name), CustomerEmail(email))
