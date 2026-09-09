# Hello World Service - Java

Versao Java 25 do servico DDD originalmente implementado em `lab1-python`.

## Requisitos

- Java 25
- Maven 3.9+

## Executar

```bash
mvn spring-boot:run
```

A API fica disponivel em `http://localhost:8080`.

## Testar

```bash
mvn verify
```

O build executa os testes, gera o relatorio JaCoCo em `target/site/jacoco` e
falha automaticamente se a cobertura de instrucoes ficar abaixo de 90%.

## Estrutura

- `customers/domain/entities`: agregado `Customer` e value objects.
- `customers/domain/repositories`: portas de persistencia do dominio.
- `customers/domain/exceptions`: excecoes de negocio.
- `customers/application/dtos`: contratos de entrada e saida da aplicacao, todos com sufixo `DTO`.
- `customers/application/usecases`: casos de uso do bounded context.
- `customers/infrastructure/repositories`: implementacoes de persistencia.
- `customers/interfaces/http/controllers`: controllers REST de customers.
- `customers/interfaces/http/dtos`: contratos HTTP, todos com sufixo `DTO`.
- `customers/interfaces/http/handlers`: tratamento de excecoes HTTP.
- `interfaces/http/controllers`: endpoints tecnicos de health e hello.

O repositorio em memoria segue o comportamento do projeto Python: os dados sao perdidos quando a aplicacao reinicia.
