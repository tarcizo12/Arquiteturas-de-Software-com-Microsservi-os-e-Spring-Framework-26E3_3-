# Etapa 2 — Microsserviço de Movimentação

Projeto preparado a partir da análise do repositório da Etapa 1:

`tarcizo12/Arquiteturas-de-Software-com-Microsservi-os-e-Spring-Framework-26E3_3-`

## Decisão arquitetural

A Etapa 1 já identificava `movimentacao` como principal candidata a uma extração. Nesta entrega, somente essa responsabilidade foi separada.

```text
Cliente HTTP
    |
    v
Aplicação Principal :8080
    |
    +--> Produto / Usuário / Catálogo
    |
    +--> MovimentacaoGatewayService
              |
              v
        OpenFeign / HTTP
              |
              v
Serviço de Movimentação :8081
    |
    +--> Controller
    +--> Service
    +--> Repository
    +--> H2 próprio
```

## Requisitos atendidos

| Requisito | Implementação |
|---|---|
| Aplicação principal + serviço independente | `aplicacao-principal` e `servico-movimentacao` |
| Responsabilidade clara | Registro/consulta do histórico de movimentações |
| API REST | `POST`, `GET`, `GET /{id}` |
| DTOs | Requests/responses próprios em cada aplicação |
| OpenAPI/Swagger | SpringDoc nos dois projetos |
| OpenFeign | `MovimentacaoClient` na aplicação principal |
| URL externa configurável | `servico.movimentacao.url` |
| Comunicação fora do Controller | `MovimentacaoGatewayService` |
| Tratamento de indisponibilidade | HTTP 503, sem expor exceção Feign |
| Banco independente | H2 separado por aplicação |
| Testes manuais | Coleção Postman em `postman/` |
| Execução separada | portas 8080 e 8081 |

## Execução

Abra dois terminais.

### Terminal 1

```bash
cd servico-movimentacao
mvn spring-boot:run
```

### Terminal 2

```bash
cd aplicacao-principal
mvn spring-boot:run
```

Acesse:
- Principal Swagger: `http://localhost:8080/swagger-ui.html`
- Serviço Swagger: `http://localhost:8081/swagger-ui.html`

## Demonstração recomendada

1. Com os dois serviços ativos, execute `POST /api/movimentacoes` pela aplicação principal.
2. Confirme `201 Created`.
3. Consulte `GET /api/movimentacoes` no serviço de movimentação e confirme que o registro existe.
4. Consulte `GET /api/produtos/1` na principal e observe o estoque alterado.
5. Pare o serviço de movimentação.
6. Repita o POST na principal.
7. A aplicação principal deve responder `503 Service Unavailable` com mensagem amigável.

## Relação com a Etapa 1

O repositório original documentava `movimentacao` como módulo de responsabilidade própria e explicitava que a comunicação com produto/usuário passaria por services. A Etapa 2 muda a fronteira de processo: a chamada que era interna passa a ser uma chamada HTTP por OpenFeign.

## Observação

A regra de saldo permanece na aplicação principal nesta etapa. Assim, o microsserviço extraído é responsável pelo histórico de movimentações, enquanto o serviço principal continua sendo a fonte do estoque atual. Isso evita transformar toda a aplicação em microsserviços e mantém o escopo pedido na atividade.
