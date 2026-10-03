# Etapa 2 — Microsserviço de Movimentação

Projeto desenvolvido a partir da evolução da aplicação apresentada na **Etapa 1**, utilizando arquitetura de microsserviços com **Spring Boot, Spring Data JPA, OpenFeign e OpenAPI/Swagger**.

Repositório de origem analisado:

`tarcizo12/Arquiteturas-de-Software-com-Microsservi-os-e-Spring-Framework-26E3_3-`

---

## 1. Objetivo da Etapa 2

O objetivo desta etapa é evoluir a aplicação monolítica da Etapa 1, identificando uma responsabilidade que possa ser executada de forma independente e extraindo-a para um novo serviço Spring Boot.

Para esta entrega, foi escolhida a responsabilidade relacionada ao **registro e consulta do histórico de movimentações de estoque**.

A solução passou a possuir duas aplicações independentes:

* **Aplicação Principal** — porta `8080`
* **Serviço de Movimentação** — porta `8081`

A comunicação entre as aplicações ocorre por meio de uma API REST consumida pela aplicação principal utilizando **OpenFeign**.

---

# 2. Decisão arquitetural

Na Etapa 1, a funcionalidade de movimentação fazia parte da própria aplicação principal.

Nesta etapa, essa responsabilidade foi extraída para um serviço independente.

A arquitetura resultante é:

```text
                         Cliente HTTP
                              |
                              v
                 +------------------------+
                 | Aplicação Principal    |
                 |       :8080            |
                 |                        |
                 | Controller             |
                 |      |                 |
                 |      v                 |
                 | MovimentacaoGateway    |
                 |      |                 |
                 |      v                 |
                 | MovimentacaoClient     |
                 |      |                 |
                 +------+-----------------+
                        |
                        | HTTP / OpenFeign
                        v
                 +------------------------+
                 | Serviço de             |
                 | Movimentação           |
                 |       :8081            |
                 |                        |
                 | Controller             |
                 |      |                 |
                 |      v                 |
                 | Service                |
                 |      |                 |
                 |      v                 |
                 | Repository             |
                 |      |                 |
                 |      v                 |
                 | Banco H2 próprio       |
                 +------------------------+
```

A aplicação principal continua sendo responsável pelo **estoque atual dos produtos**, enquanto o novo serviço é responsável pelo **registro e consulta do histórico de movimentações**.

Essa divisão mantém uma única responsabilidade claramente extraída, evitando transformar toda a aplicação em diversos microsserviços sem necessidade.

---

# 3. Responsabilidade extraída

A responsabilidade escolhida foi:

> **Registro e consulta do histórico de movimentações de estoque.**

O novo serviço possui sua própria:

* API REST;
* camada Controller;
* camada Service;
* Repository;
* entidades JPA;
* DTOs;
* banco de dados H2;
* documentação OpenAPI/Swagger.

Os principais endpoints são:

```text
POST /api/movimentacoes
GET  /api/movimentacoes
GET  /api/movimentacoes/{id}
```

---

# 4. Estrutura dos projetos

O projeto está dividido em duas aplicações independentes:

```text
etapa2/
│
├── aplicacao-principal/
│   ├── pom.xml
│   └── src/
│
├── servico-movimentacao/
│   ├── pom.xml
│   └── src/
│
└── postman/
    └── Etapa2-Microsservicos-Estoque.postman_collection.json
```

## Aplicação Principal

A aplicação principal continua contendo as funcionalidades relacionadas ao gerenciamento dos produtos, usuários e estoque.

Para consumir o novo serviço, foram adicionados:

```text
MovimentacaoController
        |
        v
MovimentacaoGatewayService
        |
        v
MovimentacaoClient
        |
        v
OpenFeign
```

O Controller não realiza diretamente a comunicação HTTP. Essa responsabilidade fica isolada no serviço de gateway e no cliente Feign.

---

## Serviço de Movimentação

O serviço independente possui sua própria estrutura:

```text
MovimentacaoController
        |
        v
MovimentacaoService
        |
        v
MovimentacaoRepository
        |
        v
Banco H2
```

O serviço não depende das entidades JPA da aplicação principal.

---

# 5. Comunicação entre os serviços

A comunicação entre as aplicações utiliza **OpenFeign**.

Na aplicação principal existe um cliente responsável por representar a API remota:

```text
Aplicação Principal
        |
        | OpenFeign
        v
HTTP
        |
        v
Serviço de Movimentação
```

A URL do serviço não fica diretamente no código-fonte. Ela é configurada externamente por meio da propriedade:

```properties
servico.movimentacao.url=http://localhost:8081
```

Dessa forma, a URL pode ser alterada sem modificar o código da aplicação.

---

# 6. Uso de DTOs

As aplicações não compartilham entidades JPA diretamente.

A comunicação utiliza objetos de transferência de dados (**DTOs**).

A estrutura segue o princípio:

```text
Aplicação Principal
       |
       | DTO
       v
    OpenFeign
       |
       | HTTP / JSON
       v
Serviço de Movimentação
       |
       | DTO
       v
    Service
       |
       v
   Entidade JPA
```

Isso reduz o acoplamento entre as aplicações e permite que cada serviço mantenha seu próprio modelo interno.

O serviço de movimentação, por exemplo, possui seus próprios DTOs de requisição e resposta.

---

# 7. Documentação da API

As duas aplicações possuem documentação utilizando **OpenAPI/Swagger**.

## Aplicação principal

```text
http://localhost:8080/swagger-ui.html
```

## Serviço de movimentação

```text
http://localhost:8081/swagger-ui.html
```

A documentação permite visualizar e executar os endpoints diretamente pelo navegador.

---

# 8. Tratamento de falhas

A comunicação entre aplicações introduz uma possibilidade que não existia da mesma forma quando a funcionalidade era interna: o serviço remoto pode estar indisponível.

Por isso, a aplicação principal possui tratamento para falhas de comunicação com o serviço de movimentação.

Quando o serviço remoto não está disponível, a aplicação principal retorna:

```text
HTTP 503 Service Unavailable
```

com uma mensagem controlada:

```text
Serviço de movimentação indisponível. Tente novamente mais tarde.
```

Detalhes internos da exceção Feign ou stack traces não são enviados ao cliente.

---

# 9. Execução

É necessário executar as duas aplicações separadamente.

## 9.1 Serviço de Movimentação

Abra um terminal:

```bash
cd servico-movimentacao
mvn spring-boot:run
```

O serviço ficará disponível em:

```text
http://localhost:8081
```

Swagger:

```text
http://localhost:8081/swagger-ui.html
```

---

## 9.2 Aplicação Principal

Abra outro terminal:

```bash
cd aplicacao-principal
mvn spring-boot:run
```

A aplicação ficará disponível em:

```text
http://localhost:8080
```

Swagger:

```text
http://localhost:8080/swagger-ui.html
```

---

# 10. Demonstração recomendada

Para demonstrar a integração entre as aplicações:

### 1. Iniciar o serviço de movimentação

Executar:

```bash
mvn spring-boot:run
```

na pasta:

```text
servico-movimentacao
```

### 2. Iniciar a aplicação principal

Executar:

```bash
mvn spring-boot:run
```

na pasta:

```text
aplicacao-principal
```

### 3. Registrar uma movimentação

Utilizar o Swagger da aplicação principal e executar:

```text
POST /api/movimentacoes
```

ou, caso o Controller esteja utilizando o prefixo de produtos:

```text
POST /com/api/produtos/movimentacoes
```

O resultado esperado é:

```text
HTTP 201 Created
```

### 4. Consultar as movimentações

No serviço de movimentação:

```text
GET /api/movimentacoes
```

O registro criado deverá aparecer no histórico.

### 5. Verificar o estoque

Na aplicação principal:

```text
GET /api/produtos/1
```

O estoque deverá refletir a movimentação realizada.

### 6. Testar indisponibilidade

Parar o serviço de movimentação e executar novamente o POST pela aplicação principal.

O resultado esperado é:

```text
HTTP 503 Service Unavailable
```

com a mensagem:

```text
Serviço de movimentação indisponível. Tente novamente mais tarde.
```

---

# 11. Requisitos atendidos

| Requisito                                  | Implementação                                       |
| ------------------------------------------ | --------------------------------------------------- |
| Aplicação principal + serviço independente | `aplicacao-principal` e `servico-movimentacao`      |
| Responsabilidade clara                     | Registro e consulta do histórico de movimentações   |
| Serviço independente                       | Aplicação Spring Boot executada na porta `8081`     |
| API REST                                   | `POST`, `GET` e `GET /{id}`                         |
| DTOs                                       | DTOs próprios em cada aplicação                     |
| OpenAPI/Swagger                            | SpringDoc nos dois projetos                         |
| OpenFeign                                  | `MovimentacaoClient`                                |
| URL externa configurável                   | `servico.movimentacao.url`                          |
| Cliente separado do Controller             | `MovimentacaoGatewayService` + `MovimentacaoClient` |
| Tratamento de indisponibilidade            | HTTP `503` com mensagem controlada                  |
| Banco independente                         | H2 próprio do serviço de movimentação               |
| Testes manuais                             | Swagger e coleção Postman                           |
| Execução independente                      | Portas `8080` e `8081`                              |

---

# 12. Respostas às questões da Etapa 2

## 1. Qual funcionalidade foi separada da aplicação principal?

Foi separado o **registro e a consulta do histórico de movimentações de estoque**.

O novo serviço passou a ser responsável por armazenar e disponibilizar as movimentações realizadas, expondo uma API REST própria.

A aplicação principal continua responsável pelo cadastro dos produtos e pelo controle do estoque atual.

---

## 2. Por que ela foi escolhida?

A funcionalidade de movimentação foi escolhida porque já apresentava uma **responsabilidade bem definida dentro da aplicação original** e possuía regras e dados próprios.

Além disso, a movimentação possui um fluxo relativamente independente do restante do sistema: recebe uma solicitação, registra os itens movimentados e disponibiliza posteriormente o histórico dessas operações.

Essa característica tornou a funcionalidade uma candidata adequada para demonstrar a extração de uma responsabilidade para um microsserviço sem precisar dividir toda a aplicação.

---

## 3. O que ficou mais complexo depois da separação?

A principal complexidade introduzida foi a **comunicação entre processos diferentes**.

Antes da separação, a aplicação poderia chamar diretamente uma classe ou service dentro do mesmo processo. Depois da extração, a aplicação principal precisa realizar uma chamada HTTP para outro serviço.

Isso introduziu novas preocupações, como:

* disponibilidade do serviço remoto;
* tempo de resposta;
* erros de comunicação;
* configuração da URL do serviço;
* contratos de API;
* DTOs;
* tratamento de respostas HTTP;
* necessidade de testar os dois serviços separadamente.

Por esse motivo, a arquitetura de microsserviços traz benefícios de independência, mas também aumenta a complexidade operacional e de comunicação.

---

## 4. O que aconteceria com a funcionalidade principal caso o novo serviço ficasse indisponível?

Quando o serviço de movimentação estiver indisponível, a aplicação principal não conseguirá registrar uma nova movimentação, pois essa operação depende da confirmação do serviço remoto.

Nesse cenário, a aplicação principal captura a falha de comunicação e retorna:

```text
HTTP 503 Service Unavailable
```

com uma mensagem amigável:

```text
Serviço de movimentação indisponível. Tente novamente mais tarde.
```

O restante da aplicação, como as funcionalidades relacionadas ao cadastro e consulta dos produtos, pode continuar funcionando, pois permanece no processo principal.

Essa decisão evita que uma falha de comunicação seja apresentada ao usuário como um erro interno da aplicação.

---

## 5. A funcionalidade realmente precisa permanecer como um serviço independente ou poderia continuar dentro da aplicação?

**Não necessariamente.**

A funcionalidade poderia continuar dentro da aplicação principal e, dependendo do tamanho, da equipe, da infraestrutura disponível e dos requisitos do sistema, essa poderia inclusive ser uma decisão mais simples e adequada.

A extração para um microsserviço foi realizada nesta etapa porque a atividade propõe demonstrar a separação de uma responsabilidade em uma aplicação independente.

Manter a movimentação como um microsserviço pode ser interessante quando existe necessidade de:

* evolução independente;
* implantação independente;
* escalabilidade específica;
* isolamento de responsabilidade;
* integração com outros sistemas;
* equipes diferentes trabalhando no domínio.

Por outro lado, manter a funcionalidade dentro da aplicação principal reduz a complexidade de comunicação, infraestrutura, monitoramento e tratamento de falhas de rede.

Portanto, **microsserviços não são obrigatoriamente melhores que um monólito**. A escolha depende das necessidades do sistema e deve ser tratada como uma decisão arquitetural.

Neste projeto, a separação foi adotada principalmente para demonstrar a independência da responsabilidade de movimentação e a comunicação entre aplicações através de uma API REST utilizando OpenFeign.

---

# 13. Conclusão

A Etapa 2 demonstra a evolução da aplicação da Etapa 1 para uma arquitetura com duas aplicações independentes.

A responsabilidade de movimentação foi extraída da aplicação principal e transformada em um serviço Spring Boot próprio, com:

* API REST;
* banco de dados próprio;
* DTOs;
* Controller;
* Service;
* Repository;
* documentação OpenAPI;
* comunicação via HTTP;
* consumo através do OpenFeign;
* tratamento de indisponibilidade.

A aplicação principal continua responsável pelo estoque atual, enquanto o serviço independente mantém o histórico de movimentações.

Dessa forma, a solução demonstra não apenas a implementação técnica de um microsserviço, mas também a **análise das vantagens, custos e consequências da decisão de separar uma responsabilidade em outro processo**.
