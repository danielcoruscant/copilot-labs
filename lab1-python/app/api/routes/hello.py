"""Endpoint de demonstração da API."""

from fastapi import APIRouter
from pydantic import BaseModel

router = APIRouter()


class HelloResponse(BaseModel):
    """Resposta JSON do endpoint de teste."""

    message: str


@router.get("/test", response_model=HelloResponse, tags=["hello"])
def hello() -> HelloResponse:
    """Retorna a mensagem de teste do microserviço."""
    return HelloResponse(message="Hello world!")
