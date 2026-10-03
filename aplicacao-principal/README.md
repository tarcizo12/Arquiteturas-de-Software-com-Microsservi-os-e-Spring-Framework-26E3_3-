# Aplicação Principal — Etapa 2

Evolução da aplicação da Etapa 1 do repositório `tarcizo12/Arquiteturas-de-Software-com-Microsservi-os-e-Spring-Framework-26E3_3-`.

## O que foi extraído

A responsabilidade escolhida foi **registro e consulta do histórico de movimentações de estoque**.

Na Etapa 1, `MovimentacaoService` era executado dentro da mesma JVM e dependia diretamente de `ProdutoService`, `UsuarioService` e `MovimentacaoRepository`.

Nesta etapa:
- a aplicação principal continua dona de produtos, categorias, fornecedores e usuários;
- o histórico de movimentações foi retirado para `servico-movimentacao`;
- a aplicação principal chama esse serviço por HTTP;
- a comunicação HTTP está isolada em `MovimentacaoClient`, usando OpenFeign;
- o endereço do serviço vem de `servico.movimentacao.url`;
- falhas de comunicação resultam em HTTP 503 com mensagem apropriada.

## Observação arquitetural

A regra de estoque continua no domínio principal: antes da chamada remota, a aplicação valida produto, usuário e disponibilidade. Depois que o serviço remoto confirma o registro, o saldo local é atualizado.

Isso mantém o escopo da atividade em **um único serviço extraído**, sem transformar todo o sistema em microsserviços.

## Executar

```bash
mvn spring-boot:run
```

Porta: `8080`

Swagger: `http://localhost:8080/swagger-ui.html`

Produtos de exemplo:
- produto `1`: Teclado USB, estoque inicial 20
- usuário `1`: Administrador

O serviço remoto precisa estar em `http://localhost:8081`, conforme `application.properties`.
