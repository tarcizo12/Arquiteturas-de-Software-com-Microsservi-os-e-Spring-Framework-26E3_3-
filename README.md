# Sistema de Estoque — Microsserviços com Spring Boot e MySQL

Sistema de controle de estoque composto por **duas aplicações Spring Boot independentes**, cada uma com seu **próprio banco de dados MySQL**, orquestrado por **Docker Compose** com ambientes separados de **desenvolvimento (dev)** e **produção (prod)**.

Tecnologias: Spring Boot, Spring Data JPA, OpenFeign, OpenAPI/Swagger, MySQL 8.4, Docker Compose.

---

## Sumário

1. [Visão geral da arquitetura](#1-visão-geral-da-arquitetura)
2. [Estrutura do repositório](#2-estrutura-do-repositório)
3. [Orquestração dos bancos de dados](#3-orquestração-dos-bancos-de-dados)
4. [Divisão dos bancos e tabelas](#4-divisão-dos-bancos-e-tabelas)
5. [Scripts de gerenciamento (.bat)](#5-scripts-de-gerenciamento-bat)
6. [Como executar o sistema](#6-como-executar-o-sistema)
7. [Configuração das aplicações](#7-configuração-das-aplicações)
8. [Comunicação entre os serviços](#8-comunicação-entre-os-serviços)
9. [Documentação da API e testes](#9-documentação-da-api-e-testes)
10. [Acesso manual aos bancos](#10-acesso-manual-aos-bancos)
11. [Solução de problemas](#11-solução-de-problemas)

---

## 1. Visão geral da arquitetura

```text
                          Cliente HTTP
                               |
                               v
                  +------------------------+
                  | Aplicação Principal    |
                  |        :8080           |
                  | Produtos, categorias,  |
                  | fornecedores, usuários |
                  +-----------+------------+
                       |             |
          JDBC         |             | HTTP / OpenFeign
                       v             v
        +----------------+     +------------------------+
        | MySQL          |     | Serviço de Movimentação|
        | estoque_       |     |        :8081           |
        | principal      |     +-----------+------------+
        +----------------+                 |
                                           | JDBC
                                           v
                                  +----------------+
                                  | MySQL          |
                                  | movimentacao_db|
                                  +----------------+
```

| Componente | Responsabilidade | Porta | Banco |
| --- | --- | --- | --- |
| **aplicacao-principal** | Cadastro de produtos, categorias, fornecedores e usuários; controle do estoque atual | `8080` | `estoque_principal` |
| **servico-movimentacao** | Registro e consulta do histórico de movimentações (entradas e saídas) | `8081` | `movimentacao_db` |

Princípios adotados:

* **Banco por serviço:** cada aplicação acessa somente o seu banco, com um usuário próprio e sem acesso ao banco do outro serviço.
* **Sem entidades compartilhadas:** a comunicação entre os serviços usa DTOs via HTTP/JSON.
* **Sem chaves estrangeiras entre bancos:** referências entre os dois bancos são validadas pela aplicação (veja a [seção 4](#4-divisão-dos-bancos-e-tabelas)).

---

## 2. Estrutura do repositório

```text
.
├── aplicacao-principal/         # Aplicação Spring Boot (porta 8080)
├── servico-movimentacao/        # Microsserviço de movimentação (porta 8081)
├── mysql/
│   ├── init-dev.sql             # Bancos, usuários e tabelas do ambiente DEV
│   └── init-prod.sql            # Bancos, usuários e tabelas do ambiente PROD
├── postman/                     # Coleção Postman para testes manuais
├── etapa-1/                     # Versão original (referência)
├── docker-compose.yml           # Orquestração dos dois containers MySQL
├── subir-banco.bat              # Sobe os bancos (idempotente)
├── resetar-banco.bat            # Remove containers, volumes e imagens
├── resumo-bancos.bat            # Lista tabelas e campos de cada banco
├── diagrama-entidades.png       # Diagrama do modelo de entidades
├── CONTEXTO_DO_PROJETO.md
└── README.md
```

---

## 3. Orquestração dos bancos de dados

O arquivo `docker-compose.yml` define **dois containers MySQL 8.4 independentes**, um para cada ambiente.

| Propriedade | DEV | PROD |
| --- | --- | --- |
| Serviço no compose | `mysql-dev` | `mysql-prod` |
| Container | `estoque-mysql-dev` | `estoque-mysql-prod` |
| Imagem | `mysql:8.4` | `mysql:8.4` |
| Porta no host | **`3306`** | **`3307`** |
| Porta no container | `3306` | `3306` |
| Senha do `root` | `root_dev` | `root_prod` |
| Volume de dados | `mysql-dev-data` | `mysql-prod-data` |
| Script de inicialização | `mysql/init-dev.sql` | `mysql/init-prod.sql` |
| Política de reinício | `unless-stopped` | `unless-stopped` |

### Volumes e persistência

Cada ambiente grava os dados em um **volume nomeado** (`mysql-dev-data` e `mysql-prod-data`). Isso significa que:

* os dados **sobrevivem** a `docker compose stop`, reinicializações do Docker e recriação dos containers;
* os dados só são apagados quando o volume é removido (o que o `resetar-banco.bat` faz).

### Inicialização automática

Os arquivos `init-*.sql` são montados somente-leitura em `/docker-entrypoint-initdb.d/init.sql`. O MySQL os executa **apenas na primeira inicialização**, quando o volume está vazio.

Para cobrir o caso de o volume já existir, o `subir-banco.bat` **reexecuta** o mesmo script a cada subida. Como todos os comandos usam `IF NOT EXISTS`, a reexecução é segura e **nada é duplicado**.

### Healthcheck

Cada container possui um healthcheck que executa `mysqladmin ping` a cada 5 segundos (timeout de 5 s e 10 tentativas). Os scripts usam esse estado (`healthy`) para saber quando o banco está pronto.

### Usuários e permissões

| Ambiente | Usuário | Senha | Acesso |
| --- | --- | --- | --- |
| DEV | `estoque_dev` | `estoque_dev` | Todos os privilégios em `estoque_principal` |
| DEV | `movimentacao_dev` | `movimentacao_dev` | Todos os privilégios em `movimentacao_db` |
| PROD | `estoque_prod` | `estoque_prod` | Todos os privilégios em `estoque_principal` |
| PROD | `movimentacao_prod` | `movimentacao_prod` | Todos os privilégios em `movimentacao_db` |

> **Atenção:** as credenciais acima são destinadas a **desenvolvimento e testes locais**. O ambiente "prod" aqui simula um segundo ambiente isolado. Em uma implantação real, use senhas fortes e gerencie-as fora do repositório (variáveis de ambiente ou gerenciador de segredos).

---

## 4. Divisão dos bancos e tabelas

Os dois ambientes (DEV e PROD) têm **a mesma estrutura**; mudam apenas os usuários, as portas e os dados. O modelo completo está em `diagrama-entidades.png`.

### Banco `estoque_principal` (aplicacao-principal)

| Tabela | Campos | Observações |
| --- | --- | --- |
| `categoria` | `id`, `nome`, `descricao` | `nome` é único |
| `fornecedor` | `id`, `nome`, `cnpj`, `telefone`, `email`, `endereco` | `cnpj` é único |
| `usuario` | `id`, `nome`, `login`, `senha`, `perfil` | `login` é único; `senha` comporta hash (255) |
| `produto` | `id`, `tipo_produto`, `nome`, `descricao`, `preco`, `quantidade_estoque`, `categoria_id`, `fornecedor_id`, `data_validade`, `lote`, `garantia_meses` | FKs para `categoria` e `fornecedor` |

**Herança de produto:** `Produto` é abstrato e possui duas especializações. Elas são armazenadas em **uma única tabela** (estratégia `SINGLE_TABLE`), diferenciadas pela coluna `tipo_produto`:

| `tipo_produto` | Campos específicos utilizados |
| --- | --- |
| `PERECIVEL` | `data_validade`, `lote` |
| `NAO_PERECIVEL` | `garantia_meses` |

Os campos de um tipo ficam `NULL` nos produtos do outro tipo.

### Banco `movimentacao_db` (servico-movimentacao)

| Tabela | Campos | Observações |
| --- | --- | --- |
| `movimentacao` | `id`, `data_hora`, `tipo`, `observacao`, `usuario_id` | `tipo` aceita `ENTRADA` ou `SAIDA` |
| `item_movimentacao` | `id`, `quantidade`, `movimentacao_id`, `produto_id` | FK para `movimentacao`; `quantidade` deve ser maior que zero |

### Relacionamentos

| Relação | Implementação |
| --- | --- |
| Categoria 1 — * Produto | FK `produto.categoria_id` |
| Fornecedor 1 — * Produto | FK `produto.fornecedor_id` |
| Movimentação 1 — * ItemMovimentação | FK `item_movimentacao.movimentacao_id` |
| Usuário 1 — * Movimentação | **Referência lógica** `movimentacao.usuario_id` (sem FK) |
| Produto 1 — * ItemMovimentação | **Referência lógica** `item_movimentacao.produto_id` (sem FK) |

> **Por que algumas referências não têm FK?** O MySQL não permite chaves estrangeiras entre bancos de dados distintos, e a arquitetura exige que cada serviço seja dono do seu banco. As colunas `usuario_id` e `produto_id` possuem índice, mas a verificação de que o registro existe é responsabilidade da aplicação.

### Convenções

* Nomes de tabelas e colunas em `snake_case`, singular (padrão do Spring: `quantidadeEstoque` → `quantidade_estoque`).
* Charset `utf8mb4` e collation `utf8mb4_unicode_ci`.
* Valores monetários (`preco`) em `DECIMAL(12,2)`.
* Enums gravados como texto (`@Enumerated(EnumType.STRING)`), com `CHECK` no banco.

---

## 5. Scripts de gerenciamento (.bat)

Os três scripts ficam na **raiz do projeto**, ao lado do `docker-compose.yml`, e podem ser executados com **duplo clique** ou pelo terminal. Requisito: **Docker Desktop em execução**.

### `subir-banco.bat` — subir os bancos

O que faz, em ordem:

1. Verifica se o Docker está rodando.
2. Executa `docker compose up -d`. Containers já existentes **não são recriados**.
3. Aguarda os dois containers ficarem `healthy` (até 40 tentativas de 3 s por container, cerca de 2 minutos).
4. Reexecuta `mysql/init-dev.sql` e `mysql/init-prod.sql` dentro dos containers, garantindo bancos, usuários e tabelas.

Pode ser executado quantas vezes quiser: se tudo já existe, nada é criado novamente.

### `resumo-bancos.bat` — conferir o estado dos bancos

Entra em cada container (DEV e PROD) e, para cada banco, mostra:

* se o container está rodando e se o banco existe;
* quantidade de tabelas, com engine e número aproximado de linhas;
* campos de cada tabela (tipo, aceita nulo, chave, valor padrão, extras);
* chaves estrangeiras.

Ao final, oferece salvar o resultado em `resumo-bancos.txt`. É útil para comparar o estado antes e depois de um reset ou de uma alteração de schema.

> O número de linhas vem de `information_schema` e é uma **estimativa**.

### `resetar-banco.bat` — limpar tudo para testes

Executa `docker compose down --volumes --rmi all --remove-orphans`, removendo:

* os containers `estoque-mysql-dev` e `estoque-mysql-prod`;
* os volumes `mysql-dev-data` e `mysql-prod-data` (**todos os dados são apagados**);
* as imagens usadas pelo compose (`mysql:8.4`);
* containers órfãos.

Pede confirmação (`S/N`) antes de executar. Para recriar do zero, rode `subir-banco.bat` em seguida (a imagem será baixada novamente).

> **Cuidado:** o reset é irreversível. Destina-se a testes. Se quiser preservar a imagem para ganhar tempo, troque `--rmi all` por `--rmi local` dentro do script.

### Fluxo de trabalho típico

```text
subir-banco.bat      →  ambiente pronto
(executar e testar as aplicações)
resumo-bancos.bat    →  conferir tabelas e campos criados
resetar-banco.bat    →  voltar ao estado zero
subir-banco.bat      →  recomeçar os testes
```

### Alterando o schema

Edite `mysql/init-dev.sql` e `mysql/init-prod.sql` mantendo a estrutura idêntica nos dois. Para **tabelas novas**, basta rodar `subir-banco.bat` (o `CREATE TABLE IF NOT EXISTS` as cria). Para **alterar tabelas já existentes**, o `IF NOT EXISTS` não as modifica: rode `resetar-banco.bat` e depois `subir-banco.bat`, ou aplique um `ALTER TABLE` manualmente.

---

## 6. Como executar o sistema

Ordem recomendada:

**1. Subir os bancos** (na raiz do projeto)

```text
subir-banco.bat
```

**2. Subir o serviço de movimentação** (terminal 1)

```bash
cd servico-movimentacao
mvn spring-boot:run
```

**3. Subir a aplicação principal** (terminal 2)

```bash
cd aplicacao-principal
mvn spring-boot:run
```

| Serviço | URL | Swagger |
| --- | --- | --- |
| Aplicação principal | `http://localhost:8080` | `http://localhost:8080/swagger-ui.html` |
| Serviço de movimentação | `http://localhost:8081` | `http://localhost:8081/swagger-ui.html` |

Para encerrar: pare as aplicações (`Ctrl+C`). Os containers continuam ativos; para limpar completamente, use `resetar-banco.bat`.

---

## 7. Configuração das aplicações

Pré-requisitos nos dois projetos: dependência do driver **`mysql-connector-j`** no `pom.xml`.

Como o schema é criado pelos scripts SQL, recomenda-se configurar o Hibernate para **não alterar o banco** (`none`) ou apenas **validar** (`validate`).

### Aplicação principal

| Ambiente | URL JDBC | Usuário | Senha |
| --- | --- | --- | --- |
| DEV | `jdbc:mysql://localhost:3306/estoque_principal` | `estoque_dev` | `estoque_dev` |
| PROD | `jdbc:mysql://localhost:3307/estoque_principal` | `estoque_prod` | `estoque_prod` |

### Serviço de movimentação

| Ambiente | URL JDBC | Usuário | Senha |
| --- | --- | --- | --- |
| DEV | `jdbc:mysql://localhost:3306/movimentacao_db` | `movimentacao_dev` | `movimentacao_dev` |
| PROD | `jdbc:mysql://localhost:3307/movimentacao_db` | `movimentacao_prod` | `movimentacao_prod` |

Exemplo (`application.properties`, ambiente DEV, aplicação principal):

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/estoque_principal?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=estoque_dev
spring.datasource.password=estoque_dev
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.open-in-view=false

servico.movimentacao.url=http://localhost:8081
```

> Se o Hibernate acusar divergência de tipo ao validar `preco` (`DECIMAL` no banco x `Double` na entidade), use `BigDecimal` na entidade ou ajuste a coluna para `DOUBLE`.

---

## 8. Comunicação entre os serviços

A aplicação principal consome o serviço de movimentação via **OpenFeign**:

```text
MovimentacaoController
        |
        v
MovimentacaoGatewayService
        |
        v
MovimentacaoClient (OpenFeign)
        |
        | HTTP / JSON
        v
Serviço de Movimentação  →  Controller → Service → Repository → MySQL (movimentacao_db)
```

* O Controller não faz chamadas HTTP; isso fica isolado no gateway e no cliente Feign.
* A URL do serviço remoto é externa ao código: `servico.movimentacao.url`.
* As aplicações trocam **DTOs**, nunca entidades JPA.

Endpoints do serviço de movimentação:

```text
POST /api/movimentacoes
GET  /api/movimentacoes
GET  /api/movimentacoes/{id}
```

### Tratamento de indisponibilidade

Se o serviço de movimentação estiver fora do ar, a aplicação principal responde:

```text
HTTP 503 Service Unavailable
"Serviço de movimentação indisponível. Tente novamente mais tarde."
```

Detalhes internos da exceção Feign e stack traces **não** são expostos ao cliente. As demais funcionalidades da aplicação principal (produtos, categorias etc.) continuam funcionando.

---

## 9. Documentação da API e testes

* **Swagger/OpenAPI:** disponível nas duas aplicações (URLs na [seção 6](#6-como-executar-o-sistema)).
* **Postman:** a coleção está em `postman/`.

### Roteiro de validação

1. Rodar `subir-banco.bat` e depois `resumo-bancos.bat` para confirmar que os bancos e tabelas existem.
2. Subir as duas aplicações.
3. Registrar uma movimentação pela aplicação principal (`POST`), esperando `HTTP 201 Created`.
4. Consultar `GET /api/movimentacoes` no serviço de movimentação e verificar o registro.
5. Verificar o estoque do produto na aplicação principal.
6. Conferir os dados diretamente no banco (veja a [seção 10](#10-acesso-manual-aos-bancos)).
7. Parar o serviço de movimentação e repetir o `POST`, esperando `HTTP 503`.

---

## 10. Acesso manual aos bancos

### Pelo terminal (dentro do container)

```bash
# DEV
docker exec -it estoque-mysql-dev mysql -uroot -proot_dev

# PROD
docker exec -it estoque-mysql-prod mysql -uroot -proot_prod
```

Exemplos de consulta:

```sql
SHOW DATABASES;
USE estoque_principal;
SHOW TABLES;
DESCRIBE produto;
SELECT * FROM movimentacao_db.movimentacao;
```

### Por um cliente gráfico (DBeaver, MySQL Workbench, DataGrip)

| Ambiente | Host | Porta | Usuário | Senha |
| --- | --- | --- | --- | --- |
| DEV | `localhost` | `3306` | `root` | `root_dev` |
| PROD | `localhost` | `3307` | `root` | `root_prod` |

Se o cliente reclamar de chave pública, ative `allowPublicKeyRetrieval=true` nas propriedades da conexão.

### Comandos úteis do Docker Compose

```bash
docker compose ps                       # status e saúde dos containers
docker compose logs -f mysql-dev        # logs do MySQL de DEV
docker compose stop                     # pausa os containers (mantém os dados)
docker compose start                    # retoma os containers
docker volume ls                        # lista os volumes
```

---

## 11. Solução de problemas

| Sintoma | Causa provável | Solução |
| --- | --- | --- |
| `Docker nao esta rodando` | Docker Desktop fechado | Abra o Docker Desktop e aguarde iniciar |
| `port is already allocated` (3306 ou 3307) | Outro MySQL usando a porta | Pare o MySQL local ou altere a porta à esquerda em `ports:` no compose |
| `nao ficou saudavel a tempo` | Máquina lenta ou primeira inicialização | Veja `docker compose logs`; aumente as tentativas no `.bat` ou adicione `start_period` ao healthcheck |
| Banco existe mas sem tabelas | Init não executou | Rode `subir-banco.bat` novamente |
| Alterei o SQL e nada mudou em tabela existente | `IF NOT EXISTS` não altera tabelas | Rode `resetar-banco.bat` e `subir-banco.bat` |
| `Access denied for user` | Usuário/senha ou ambiente incorretos | Confira a [seção 3](#usuários-e-permissões) e a porta (3306 = DEV, 3307 = PROD) |
| `Communications link failure` | Banco não está no ar | Rode `subir-banco.bat` e confira com `docker compose ps` |
| `HTTP 503` na aplicação principal | Serviço de movimentação parado | Suba o `servico-movimentacao` (porta 8081) |