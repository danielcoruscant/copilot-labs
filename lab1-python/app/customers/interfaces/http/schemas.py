"""Schemas Pydantic exclusivos do adaptador HTTP de customers."""

from uuid import UUID

from pydantic import BaseModel, ConfigDict, Field


class CustomerCreate(BaseModel):
    """Payload HTTP usado para criar um customer."""

    name: str = Field(min_length=1, max_length=120)
    email: str = Field(min_length=3, max_length=254)


class CustomerUpdate(BaseModel):
    """Payload HTTP usado para substituir um customer."""

    name: str = Field(min_length=1, max_length=120)
    email: str = Field(min_length=3, max_length=254)


class CustomerResponse(BaseModel):
    """Resposta HTTP pública de um customer."""

    model_config = ConfigDict(from_attributes=True)

    id: UUID
    name: str
    email: str


class CustomerDeleteResponse(BaseModel):
    """Resposta JSON retornada após excluir um customer."""

    message: str
