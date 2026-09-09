"""Adaptador HTTP do bounded context de customers."""

from uuid import UUID

from fastapi import APIRouter, HTTPException

from app.customers.application.dtos import CreateCustomerDTO, UpdateCustomerDTO
from app.customers.application.use_cases import (
    CreateCustomer,
    DeleteCustomer,
    GetCustomer,
    ListCustomers,
    UpdateCustomer,
)
from app.customers.domain.exceptions import CustomerNotFoundError, InvalidCustomerError
from app.customers.infrastructure.repositories import customer_repository
from app.customers.interfaces.http.schemas import (
    CustomerCreate,
    CustomerDeleteResponse,
    CustomerResponse,
    CustomerUpdate,
)

router = APIRouter(prefix="/customers", tags=["customers"])

_create_customer = CreateCustomer(customer_repository)
_list_customers = ListCustomers(customer_repository)
_get_customer = GetCustomer(customer_repository)
_update_customer = UpdateCustomer(customer_repository)
_delete_customer = DeleteCustomer(customer_repository)


def _response(output: object) -> CustomerResponse:
    """Converte um DTO de aplicação na resposta pública do adaptador."""
    return CustomerResponse.model_validate(output, from_attributes=True)


@router.post("", response_model=CustomerResponse, status_code=201)
def create_customer(customer: CustomerCreate) -> CustomerResponse:
    """Cria um customer a partir do payload HTTP recebido."""
    try:
        output = _create_customer.execute(
            CreateCustomerDTO(name=customer.name, email=customer.email)
        )
    except InvalidCustomerError as error:
        raise HTTPException(status_code=422, detail=str(error)) from error
    return _response(output)


@router.get("", response_model=list[CustomerResponse])
def list_customers() -> list[CustomerResponse]:
    """Retorna todos os customers mantidos pelo repositório configurado."""
    return [_response(output) for output in _list_customers.execute()]


@router.get("/{customer_id}", response_model=CustomerResponse)
def get_customer(customer_id: UUID) -> CustomerResponse:
    """Busca um customer por UUID e converte ausência para HTTP 404."""
    try:
        return _response(_get_customer.execute(customer_id))
    except CustomerNotFoundError as error:
        raise HTTPException(status_code=404, detail="Customer not found") from error


@router.put("/{customer_id}", response_model=CustomerResponse)
def update_customer(
    customer_id: UUID, customer: CustomerUpdate
) -> CustomerResponse:
    """Substitui os dados de um customer existente."""
    try:
        output = _update_customer.execute(
            customer_id,
            UpdateCustomerDTO(name=customer.name, email=customer.email),
        )
    except CustomerNotFoundError as error:
        raise HTTPException(status_code=404, detail="Customer not found") from error
    except InvalidCustomerError as error:
        raise HTTPException(status_code=422, detail=str(error)) from error
    return _response(output)


@router.delete("/{customer_id}", response_model=CustomerDeleteResponse)
def delete_customer(customer_id: UUID) -> CustomerDeleteResponse:
    """Exclui um customer e retorna uma confirmação JSON."""
    try:
        _delete_customer.execute(customer_id)
    except CustomerNotFoundError as error:
        raise HTTPException(status_code=404, detail="Customer not found") from error
    return CustomerDeleteResponse(message="Customer deleted successfully")
