"""Implementações de persistência do bounded context de customers."""

from threading import RLock
from uuid import UUID

from app.customers.domain.entities import Customer
from app.customers.domain.exceptions import CustomerNotFoundError


class InMemoryCustomerRepository:
    """Repositório thread-safe que mantém customers na memória do processo."""

    def __init__(self) -> None:
        """Inicializa o armazenamento vazio e seu lock reentrante."""
        self._customers: dict[UUID, Customer] = {}
        self._lock = RLock()

    def save(self, customer: Customer) -> Customer:
        """Insere ou substitui um customer de forma atômica."""
        with self._lock:
            self._customers[customer.id] = customer
        return customer

    def list(self) -> list[Customer]:
        """Retorna uma fotografia consistente dos customers armazenados."""
        with self._lock:
            return list(self._customers.values())

    def get(self, customer_id: UUID) -> Customer:
        """Busca por ID ou lança erro de domínio quando não encontrado."""
        with self._lock:
            customer = self._customers.get(customer_id)
        if customer is None:
            raise CustomerNotFoundError
        return customer

    def delete(self, customer_id: UUID) -> None:
        """Remove por ID ou lança erro de domínio quando não encontrado."""
        with self._lock:
            if customer_id not in self._customers:
                raise CustomerNotFoundError
            del self._customers[customer_id]

    def clear(self) -> None:
        """Esvazia o armazenamento, principalmente para isolamento de testes."""
        with self._lock:
            self._customers.clear()


customer_repository = InMemoryCustomerRepository()
