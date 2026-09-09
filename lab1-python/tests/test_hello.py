from fastapi.testclient import TestClient

from app.main import app

client = TestClient(app)


def test_hello_returns_expected_message() -> None:
    response = client.get("/test")

    assert response.status_code == 200
    assert response.headers["content-type"] == "application/json"
    assert response.json() == {"message": "Hello world!"}


def test_health_returns_ok() -> None:
    response = client.get("/health")

    assert response.status_code == 200
    assert response.headers["content-type"] == "application/json"
    assert response.json() == {"status": "ok"}
