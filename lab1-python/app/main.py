"""Composition root da API."""

from fastapi import FastAPI

from app.api.routes.health import router as health_router
from app.api.routes.hello import router as hello_router
from app.customers.interfaces.http.routes import router as customers_router


def create_app() -> FastAPI:
    """Cria a aplicação e registra os adaptadores HTTP disponíveis."""
    application = FastAPI(
        title="Hello World Service",
        version="0.1.0",
        description="A production-ready DDD microservice.",
    )
    application.include_router(customers_router)
    application.include_router(hello_router)
    application.include_router(health_router)
    return application


app = create_app()
