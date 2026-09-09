"""DTOs de entrada e saída dos casos de uso de customers."""

from dataclasses import dataclass
from uuid import UUID


@dataclass(frozen=True, slots=True)
class CreateCustomerDTO:
    """Dados necessários para criar um customer."""

    name: str
    email: str


@dataclass(frozen=True, slots=True)
class UpdateCustomerDTO:
    """Dados necessários para substituir os dados de um customer."""

    name: str
    email: str


@dataclass(frozen=True, slots=True)
class CustomerDTO:
    """Representação agnóstica de transporte de um customer."""

    id: UUID
    name: str
    email: str
