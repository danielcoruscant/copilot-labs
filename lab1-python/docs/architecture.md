# Arquitetura DDD

## Objetivo

O serviço é organizado como um bounded context de `customers`, usando DDD
estratégico em escala de microserviço e separação em camadas inspirada em
Hexagonal/Clean Architecture.

## Dependências permitidas

```text
interfaces/http -> application -> domain
infrastructure -> domain
main (composition root) -> todas as implementações
```

O domínio não importa FastAPI, Pydantic ou detalhes de armazenamento. A API
converte payloads HTTP em DTOs; a infraestrutura implementa as portas exigidas
pelo domínio.

## Estrutura

```text
app/
├── api/routes/                         # endpoints técnicos: health e hello
├── customers/
│   ├── domain/
│   │   ├── entities.py                 # agregado Customer
│   │   ├── exceptions.py               # erros de negócio
│   │   ├── repositories.py             # porta de persistência (Protocol)
│   │   └── value_objects.py            # CustomerName e CustomerEmail
│   ├── application/
│   │   ├── dtos.py                     # DTOs de entrada e saída
│   │   └── use_cases.py                # operações da aplicação
│   ├── infrastructure/
│   │   └── repositories.py             # cache/repositório em memória
│   └── interfaces/http/
│       ├── routes.py                   # adaptador FastAPI
│       └── schemas.py                  # contratos Pydantic
└── main.py                             # composition root
```

## Modelo de domínio

`Customer` é o agregado raiz. Sua identidade é um `UUID`; `CustomerName` e
`CustomerEmail` são value objects imutáveis que concentram normalização e
invariantes. A entidade também é imutável: `update()` cria uma nova versão.

## Casos de uso

Cada operação possui uma classe com `execute()`:

- `CreateCustomer`: cria o agregado e o persiste.
- `ListCustomers`: consulta a coleção.
- `GetCustomer`: consulta por identidade.
- `UpdateCustomer`: carrega, atualiza pelo agregado e salva.
- `DeleteCustomer`: remove por identidade.

## Repositório em memória

`InMemoryCustomerRepository` implementa a porta `CustomerRepository` com um
dicionário protegido por `RLock`. É um cache local por processo: perde os dados
no restart e não compartilha dados entre workers. Para produção real, deve ser
substituído por uma implementação de banco/Redis no mesmo contrato.

## Contrato HTTP

- `POST /customers` — `201 Created` com JSON.
- `GET /customers` — `200 OK` com JSON.
- `GET /customers/{id}` — `200 OK` ou `404` em JSON.
- `PUT /customers/{id}` — `200 OK`, `404` ou `422` em JSON.
- `DELETE /customers/{id}` — `200 OK` com confirmação JSON ou `404`.

Todos os endpoints retornam `application/json`, inclusive erros de validação e
recursos não encontrados.
