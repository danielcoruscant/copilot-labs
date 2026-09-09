# Hello World Service

Microserviço HTTP em Python com FastAPI, organizado em DDD e pronto para desenvolvimento local e execução em produção com Docker.

A implementação de customers segue as camadas `domain`, `application`,
`infrastructure` e `interfaces/http`. Consulte
[`docs/architecture.md`](docs/architecture.md) para o detalhamento.

## Requisitos

- Docker com Compose
- Ou Python 3.12+ para execução fora do container

## Desenvolvimento com Docker

```bash
docker compose up --build
```

A API fica disponível em `http://localhost:8000`. A documentação interativa fica em `http://localhost:8000/docs`.

## Desenvolvimento sem Docker

```bash
python3 -m venv .venv
. .venv/bin/activate
pip install -e ".[dev]"
python -m uvicorn app.main:app --host 0.0.0.0 --port 8000 --reload
```

Para depurar no VS Code, selecione `.venv/bin/python`, abra **Run and Debug** e execute `FastAPI Debug`.

## Produção

```bash
docker compose -f compose.prod.yaml up --build -d
```

O container executa com usuário sem privilégios, dois workers Uvicorn e healthcheck em `/health`.

## Endpoints

- `GET /test` retorna `{"message": "Hello world!"}`
- `GET /health` retorna `{"status": "ok"}`
- `POST /customers` cria um customer
- `GET /customers` lista os customers em memória
- `GET /customers/{id}` busca um customer
- `PUT /customers/{id}` atualiza um customer
- `DELETE /customers/{id}` remove um customer e retorna confirmação JSON

Os customers ficam armazenados somente na memória do processo. Eles são perdidos ao reiniciar a API e não são compartilhados entre réplicas.

## Testando com cURL

```bash
curl http://localhost:8000/test
curl http://localhost:8000/health

curl -X POST http://localhost:8000/customers \
  -H "Content-Type: application/json" \
  -d '{"name":"Ada Lovelace","email":"ada@example.com"}'

curl http://localhost:8000/customers
curl http://localhost:8000/customers/CUSTOMER_ID

curl -X PUT http://localhost:8000/customers/CUSTOMER_ID \
  -H "Content-Type: application/json" \
  -d '{"name":"Ada Byron","email":"ada.byron@example.com"}'

curl -X DELETE http://localhost:8000/customers/CUSTOMER_ID
```

## Testes e qualidade

```bash
pytest
ruff check .
```
