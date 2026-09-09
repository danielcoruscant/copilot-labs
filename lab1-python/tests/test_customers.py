from fastapi.testclient import TestClient

from app.customers.infrastructure.repositories import customer_repository
from app.main import app

client = TestClient(app)


def setup_function() -> None:
    customer_repository.clear()


def test_customer_crud_lifecycle() -> None:
    created = client.post(
        "/customers", json={"name": "Ada Lovelace", "email": "ada@example.com"}
    )
    customer_id = created.json()["id"]

    assert created.status_code == 201
    assert created.headers["content-type"] == "application/json"
    assert client.get("/customers").json() == [created.json()]
    assert client.get(f"/customers/{customer_id}").json() == created.json()

    updated = client.put(
        f"/customers/{customer_id}",
        json={"name": "Ada Byron", "email": "ada.byron@example.com"},
    )
    assert updated.status_code == 200
    assert updated.json()["name"] == "Ada Byron"

    deleted = client.delete(f"/customers/{customer_id}")
    assert deleted.status_code == 200
    assert deleted.headers["content-type"] == "application/json"
    assert deleted.json() == {"message": "Customer deleted successfully"}
    assert client.get(f"/customers/{customer_id}").status_code == 404


def test_customer_validation_and_missing_customer() -> None:
    assert client.post("/customers", json={"name": "", "email": "x"}).status_code == 422
    missing_id = "00000000-0000-0000-0000-000000000000"
    assert client.get(f"/customers/{missing_id}").status_code == 404
