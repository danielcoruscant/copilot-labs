"""Exceções e regras de erro pertencentes ao domínio de customers."""


class CustomerNotFoundError(Exception):
    """Indica que o customer solicitado não existe no repositório."""


class InvalidCustomerError(ValueError):
    """Indica que uma regra de negócio do customer foi violada."""
