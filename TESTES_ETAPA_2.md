# Roteiro de testes e evidências — Etapa 2

## 1. API do serviço independente

1. Inicie `servico-movimentacao`.
2. Abra `http://localhost:8081/swagger-ui.html`.
3. Execute `POST /api/movimentacoes`.
4. Evidência esperada: `201 Created`.
5. Execute `GET /api/movimentacoes`.
6. Evidência esperada: o registro criado aparece no histórico.

## 2. Comunicação pela aplicação principal

1. Inicie também `aplicacao-principal`.
2. Abra `http://localhost:8080/swagger-ui.html`.
3. Execute `POST /api/movimentacoes`.
4. Evidência esperada: `201 Created`.
5. Execute `GET /api/produtos/1`.
6. Evidência esperada: estoque alterado após a confirmação do serviço remoto.

## 3. Serviço externo indisponível

1. Mantenha apenas a aplicação principal ligada.
2. Pare `servico-movimentacao`.
3. Execute `POST /api/movimentacoes` na porta 8080.
4. Evidência esperada:
   - HTTP `503 Service Unavailable`;
   - mensagem: `Serviço de movimentação indisponível. Tente novamente mais tarde.`;
   - nenhum stack trace ou detalhe interno do Feign é devolvido ao cliente.

## 4. Contrato de DTO

O POST da principal não recebe entidades JPA. O contrato usa `MovimentacaoRequest`, enquanto a chamada Feign usa DTOs próprios do cliente. O microsserviço também possui seus próprios DTOs e não importa classes de domínio da aplicação principal.
