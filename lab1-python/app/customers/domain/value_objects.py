"""Value objects usados pelo domínio de customers."""

from dataclasses import dataclass

from app.customers.domain.exceptions import InvalidCustomerError


@dataclass(frozen=True, slots=True)
class CustomerName:
    """Nome validado de um customer."""

    value: str

    def __post_init__(self) -> None:
        """Valida o nome e normaliza espaços nas extremidades."""
        normalized = self.value.strip()
        if not 1 <= len(normalized) <= 120:
            raise InvalidCustomerError(
                "Customer name must contain between 1 and 120 characters"
            )
        object.__setattr__(self, "value", normalized)

    def __str__(self) -> str:
        """Retorna o valor textual do nome."""
        return self.value


@dataclass(frozen=True, slots=True)
class CustomerEmail:
    """Endereço de email validado e normalizado para lowercase."""

    value: str

    def __post_init__(self) -> None:
        """Valida tamanho e formato básico do email."""
        normalized = self.value.strip().lower()
        local, separator, domain = normalized.partition("@")
        if not 3 <= len(normalized) <= 254 or not separator or not local or not domain:
            raise InvalidCustomerError("Customer email must be a valid address")
        if "." not in domain or domain.startswith(".") or domain.endswith("."):
            raise InvalidCustomerError("Customer email must be a valid address")
        object.__setattr__(self, "value", normalized)

    def __str__(self) -> str:
        """Retorna o valor textual do email."""
        return self.value
