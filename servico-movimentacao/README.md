# Serviço de Movimentação

## Responsabilidade principal

Registrar e consultar o **histórico de movimentações de estoque** (ENTRADA/SAIDA).

## Funcionalidade separada

Na aplicação da Etapa 1, a funcionalidade era implementada dentro de `MovimentacaoService`, no mesmo processo da aplicação principal. A extração separa:
- entidade de movimentação;
- itens da movimentação;
- repositório;
- regra de registro;
- API REST.

## Motivo da separação

A própria documentação da Etapa 1 identificou `movimentacao` como fronteira de negócio clara: é um módulo predominantemente transacional/de escrita e representa o histórico de alterações de estoque. Ele pode ter ciclo de evolução e persistência independente do catálogo.

## Contrato HTTP

### POST `/api/movimentacoes`
Registra uma movimentação.

Exemplo:

```json
{
  "tipo": "ENTRADA",
  "idUsuarioResponsavel": 1,
  "nomeUsuario": "Administrador",
  "observacao": "Reposição",
  "itens": [
    { "idProduto": 1, "quantidade": 5 }
  ]
}
```

Resposta: `201 Created`.

### GET `/api/movimentacoes`
Lista o histórico.

### GET `/api/movimentacoes/{id}`
Consulta uma movimentação.

Resposta `404` quando o ID não existe.

Swagger: `http://localhost:8081/swagger-ui.html`
OpenAPI: `http://localhost:8081/api-docs`
