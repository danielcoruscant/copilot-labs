"""Endpoint técnico de healthcheck."""

from fastapi import APIRouter
from pydantic import BaseModel

router = APIRouter()


class HealthResponse(BaseModel):
    """Resposta JSON do healthcheck."""

    status: str


@router.get("/health", response_model=HealthResponse, tags=["health"])
def health() -> HealthResponse:
    """Retorna o estado mínimo de disponibilidade do serviço."""
    return HealthResponse(status="ok")
