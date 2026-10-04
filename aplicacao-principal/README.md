# Etapa 4 — Comunicação Assíncrona com RabbitMQ e Processamento em Lote

## 1. Objetivo

Nesta etapa foi implementada uma solução de **comunicação assíncrona utilizando RabbitMQ**, integrada ao **Spring Batch** para realizar o processamento em lote de fornecedores.

A funcionalidade permite que o usuário envie um arquivo CSV contendo fornecedores sem que a aplicação precise realizar toda a importação durante a requisição HTTP original.

O fluxo implementado utiliza três componentes principais:

* **Aplicação Principal**: recebe o arquivo CSV e solicita seu processamento.
* **RabbitMQ**: responsável por armazenar e disponibilizar a solicitação de processamento através de uma fila.
* **Spring Batch**: responsável por realizar a importação dos fornecedores em lotes.

O fluxo geral é:

```text
Usuário
   │
   │ Upload do CSV
   ▼
Aplicação Principal
   │
   │ Salva arquivo temporariamente
   │
   │ Publica mensagem
   ▼
RabbitMQ
   │
   │ Fila solicitar.processamento
   ▼
FornecedorConsumer
   │
   │ Inicia Spring Batch
   ▼
Spring Batch
   │
   │ Lê CSV em lotes
   │
   │ Processa fornecedores
   │
   ▼
MySQL
```

---

# 2. Operação escolhida para comunicação assíncrona

A operação escolhida foi a **importação de fornecedores através de arquivo CSV**.

A aplicação disponibiliza o endpoint:

```text
PUT /com/api/fornecedores/importacao
```

O usuário envia um arquivo CSV contendo os dados dos fornecedores.

Em vez de realizar toda a importação durante a requisição HTTP, a aplicação:

1. Recebe o arquivo;
2. Salva o CSV temporariamente;
3. Publica uma mensagem no RabbitMQ;
4. Retorna o controle da requisição;
5. Posteriormente, o Consumer recebe a mensagem;
6. O Consumer inicia o processamento através do Spring Batch.

O CSV utilizado possui a seguinte estrutura:

```text
nome,cnpj,telefone,email,endereco
```

Essa operação foi escolhida porque a importação pode envolver uma quantidade significativa de registros e não precisa bloquear o usuário enquanto todos os fornecedores são inseridos no banco de dados.

---

# 3. Por que essa operação não precisa ser concluída durante a requisição original?

A importação de fornecedores é uma operação que pode levar mais tempo do que uma requisição HTTP comum, principalmente quando o arquivo possui muitos registros.

O usuário não precisa permanecer aguardando a conclusão de todos os inserts no banco de dados para que a solicitação seja aceita.

Por isso, a aplicação separa o momento da **solicitação** do momento do **processamento**.

O fluxo é:

```text
Requisição HTTP
      │
      ▼
Arquivo recebido
      │
      ▼
Arquivo salvo temporariamente
      │
      ▼
Mensagem enviada ao RabbitMQ
      │
      ▼
Requisição pode ser encerrada
```

Depois disso:

```text
RabbitMQ
    │
    ▼
FornecedorConsumer
    │
    ▼
Spring Batch
    │
    ▼
Importação dos fornecedores
```

Essa abordagem evita manter a requisição HTTP aberta durante todo o processamento e permite que a aplicação execute a importação de forma independente.

---

# 4. Arquitetura da comunicação assíncrona

A comunicação implementada segue o padrão **Producer/Consumer**.

```text
┌──────────────────────────┐
│     Aplicação Principal  │
│                          │
│  Importação de Fornecedor│
└────────────┬─────────────┘
             │
             │ mensagem
             ▼
┌──────────────────────────┐
│    FornecedorProducer    │
│                          │
│      RabbitTemplate      │
└────────────┬─────────────┘
             │
             ▼
┌────────────────────────────────┐
│            RabbitMQ             │
│                                │
│  solicitar.processamento       │
└────────────┬───────────────────┘
             │
             │ mensagem
             ▼
┌──────────────────────────┐
│    FornecedorConsumer    │
│                          │
│     @RabbitListener      │
└────────────┬─────────────┘
             │
             ▼
┌──────────────────────────┐
│       Spring Batch       │
│                          │
│ Importação dos fornecedores│
└────────────┬─────────────┘
             │
             ▼
┌──────────────────────────┐
│          MySQL           │
│                          │
│       fornecedor         │
└──────────────────────────┘
```

A principal característica dessa arquitetura é o **desacoplamento temporal** entre quem solicita o processamento e quem efetivamente executa a operação.

O Producer não precisa conhecer a implementação interna do Consumer. Ele apenas publica uma mensagem na fila.

---

# 5. RabbitMQ

O RabbitMQ foi utilizado como **Message Broker** da aplicação.

Sua responsabilidade é receber as mensagens publicadas pelo Producer e disponibilizá-las para os Consumers.

Neste projeto foi utilizada a fila:

```text
solicitar.processamento
```

A comunicação com o RabbitMQ utiliza:

```text
Host: localhost
Porta AMQP: 5672
Porta Management: 15672
Usuário: admin
Senha: admin
```

O painel administrativo pode ser acessado através de:

```text
http://localhost:15672
```

---

# 6. Necessidade da criação da fila no RabbitMQ

Para que a comunicação assíncrona funcione, é necessário que exista uma fila responsável por receber as mensagens produzidas pela aplicação.

Neste projeto foi criada a fila:

```text
solicitar.processamento
```

A fila representa o canal de comunicação entre o Producer e o Consumer.

O Producer publica a mensagem nessa fila:

```text
FornecedorProducer
       │
       ▼
RabbitMQ
       │
       ▼
solicitar.processamento
```

Enquanto o Consumer fica aguardando mensagens:

```text
solicitar.processamento
       │
       ▼
FornecedorConsumer
```

Durante a configuração do ambiente, essa fila deve ser criada no RabbitMQ antes da utilização da funcionalidade.

A aplicação utiliza o nome da fila através do `application.properties`:

```properties
rabbitmq.queue.solicitar-processamento=solicitar.processamento
```

Dessa forma, o nome da fila fica centralizado na configuração da aplicação, evitando que o valor precise ser repetido diretamente no código.

---

# 7. O que acontece se o Consumer estiver temporariamente indisponível?

Uma das vantagens da utilização de uma fila é que o Producer não precisa depender da disponibilidade imediata do Consumer.

Quando uma mensagem é publicada, ela fica disponível na fila para ser consumida.

Dessa forma, se o Consumer estiver temporariamente indisponível, a mensagem pode permanecer aguardando na fila até que exista um consumidor disponível para processá-la.

O comportamento pode ser representado da seguinte forma:

```text
Producer
   │
   ▼
RabbitMQ
   │
   │ Consumer indisponível
   │
   ▼
Mensagem permanece na fila
   │
   │
   │ Consumer volta a funcionar
   ▼
FornecedorConsumer
   │
   ▼
Processamento
```

Isso reduz a dependência temporal entre os componentes.

A aplicação que produz a mensagem não precisa aguardar o funcionamento do Consumer para realizar a publicação.

---

# 8. Producer

O Producer é responsável por publicar as solicitações de processamento no RabbitMQ.

Foi criada a classe:

```text
com.messaging.FornecedorProducer
```

A comunicação é realizada utilizando o `RabbitTemplate`, disponibilizado pelo Spring AMQP.

O Producer envia uma mensagem para:

```text
solicitar.processamento
```

Fluxo:

```text
FornecedorProducer
       │
       ▼
RabbitTemplate
       │
       ▼
RabbitMQ
       │
       ▼
solicitar.processamento
```

No contexto da importação, a mensagem contém o nome do arquivo CSV temporário que deverá ser processado.

---

# 9. Consumer

O Consumer é responsável por receber as mensagens disponibilizadas na fila.

Foi criada a classe:

```text
com.messaging.FornecedorConsumer
```

O recebimento da mensagem é realizado através da anotação:

```java
@RabbitListener
```

Quando uma mensagem é recebida, o Consumer utiliza o nome do arquivo informado na mensagem para iniciar o processamento do Spring Batch.

O fluxo é:

```text
RabbitMQ
    │
    ▼
solicitar.processamento
    │
    ▼
FornecedorConsumer
    │
    ▼
Spring Batch
    │
    ▼
Importação dos fornecedores
```

---

# 10. Processamento em lote com Spring Batch

A funcionalidade escolhida para o processamento em lote foi a **importação de fornecedores através de arquivo CSV**.

Essa funcionalidade foi implementada utilizando o **Spring Batch**.

O Batch é responsável por:

1. Receber o nome do arquivo através de um parâmetro do Job;
2. Localizar o arquivo CSV temporário;
3. Ler os registros;
4. Converter cada linha em um `FornecedorEntity`;
5. Persistir os fornecedores no banco de dados;
6. Excluir o arquivo temporário após a conclusão do processamento.

O processamento utiliza **chunks de 50 registros**.

```text
CSV
 │
 ├── Registro 1
 ├── Registro 2
 ├── ...
 └── Registro 50
          │
          ▼
      Processor
          │
          ▼
     Writer / JPA
          │
          ▼
        MySQL
```

Após processar um conjunto de 50 registros, o Batch realiza a persistência do chunk antes de continuar com os próximos registros.

---

# 11. Por que a importação de fornecedores é adequada para Batch?

A importação de fornecedores é adequada para processamento em lote porque trabalha com um **conjunto de registros independentes**, provenientes de um arquivo.

Em vez de realizar uma requisição individual para cada fornecedor, o Spring Batch pode processar vários registros em sequência.

Isso proporciona algumas vantagens:

* Processamento de grandes quantidades de registros;
* Leitura sequencial do arquivo;
* Processamento em chunks;
* Persistência agrupada dos registros;
* Controle do processamento pelo Spring Batch;
* Registro do estado da execução do Job;
* Separação entre a solicitação e a execução do processamento.

O uso de chunks de 50 registros também evita que todos os fornecedores do arquivo sejam mantidos simultaneamente em memória.

---

# 12. Fluxo completo da importação

A funcionalidade completa pode ser representada da seguinte maneira:

```text
┌───────────────────────┐
│       Usuário         │
└───────────┬───────────┘
            │
            │ Upload CSV
            ▼
┌───────────────────────┐
│   Aplicação Principal  │
└───────────┬───────────┘
            │
            │ Salva arquivo
            ▼
┌───────────────────────┐
│ Arquivo temporário    │
│ fornecedores/*.csv    │
└───────────┬───────────┘
            │
            │ Publica mensagem
            ▼
┌───────────────────────┐
│       RabbitMQ        │
│                       │
│ solicitar.processamento│
└───────────┬───────────┘
            │
            │ Consumo
            ▼
┌───────────────────────┐
│ FornecedorConsumer    │
└───────────┬───────────┘
            │
            │ inicia Job
            ▼
┌───────────────────────┐
│      Spring Batch     │
│                       │
│ Reader                │
│ Processor             │
│ Writer                │
└───────────┬───────────┘
            │
            │ saveAll()
            ▼
┌───────────────────────┐
│         MySQL         │
│                       │
│       fornecedor      │
└───────────────────────┘
            │
            ▼
┌───────────────────────┐
│ Exclusão do arquivo   │
│ temporário             │
└───────────────────────┘
```

---

# 13. Configuração do Spring Batch

O processamento do Batch possui uma configuração específica para seus metadados.

O banco principal da aplicação continua sendo o MySQL:

```text
estoque_principal
```

Enquanto os metadados do Spring Batch são mantidos em um banco H2 separado.

```text
MySQL
  │
  └── Dados da aplicação
       ├── fornecedor
       ├── produto
       ├── categoria
       └── ...

H2
  │
  └── Metadados do Spring Batch
       ├── BATCH_JOB_INSTANCE
       ├── BATCH_JOB_EXECUTION
       ├── BATCH_STEP_EXECUTION
       └── ...
```

Essa separação evita que as tabelas internas do Spring Batch sejam criadas no banco de dados principal da aplicação.

---

# 14. Configuração do RabbitMQ

O RabbitMQ foi adicionado ao ambiente através de um container Docker.

A imagem utilizada é:

```text
rabbitmq:3.13-management
```

O plugin de gerenciamento permite acompanhar as filas e mensagens através do navegador.

As portas utilizadas são:

```text
5672  → comunicação da aplicação com o RabbitMQ
15672 → painel administrativo
```

O container utiliza:

```text
Usuário: admin
Senha: admin
```

---

# 15. Configuração no Docker Compose

O RabbitMQ também faz parte da infraestrutura definida no `docker-compose.yml`.

Exemplo:

```yaml
rabbitmq:
  build: ./rabbitmq
  container_name: estoque-rabbitmq
  restart: unless-stopped
  environment:
    RABBITMQ_DEFAULT_USER: admin
    RABBITMQ_DEFAULT_PASS: admin
  ports:
    - "5672:5672"
    - "15672:15672"
  healthcheck:
    test: ["CMD", "rabbitmq-diagnostics", "-q", "ping"]
    interval: 5s
    timeout: 5s
    retries: 20
    start_period: 10s
```

A aplicação principal é executada localmente durante o desenvolvimento e utiliza o RabbitMQ disponibilizado pelo container através da porta `5672`.

---

# 16. Configuração da aplicação

As configurações necessárias para conexão com o RabbitMQ são mantidas no `application.properties`:

```properties
spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=admin
spring.rabbitmq.password=admin

rabbitmq.queue.solicitar-processamento=solicitar.processamento
```

A propriedade:

```properties
rabbitmq.queue.solicitar-processamento
```

centraliza o nome da fila utilizada pelo Producer e pelo Consumer.

---

# 17. Teste da funcionalidade

Com o RabbitMQ em execução, o painel administrativo pode ser acessado através de:

```text
http://localhost:15672
```

Após o login, a fila pode ser visualizada em:

```text
Queues and Streams
```

com o nome:

```text
solicitar.processamento
```

A funcionalidade principal pode ser testada através do envio de um arquivo CSV para:

```text
PUT /com/api/fornecedores/importacao
```

O arquivo deve possuir as colunas:

```text
nome,cnpj,telefone,email,endereco
```

Após o envio:

```text
Upload CSV
    │
    ▼
Arquivo salvo temporariamente
    │
    ▼
Mensagem enviada ao RabbitMQ
    │
    ▼
Consumer recebe mensagem
    │
    ▼
Spring Batch inicia
    │
    ▼
Fornecedores são processados
    │
    ▼
Registros são persistidos no MySQL
```

Ao final do processamento bem-sucedido, o arquivo CSV temporário é excluído.

---

# 18. Quando utilizar REST, Mensageria ou Batch?

As três tecnologias possuem objetivos diferentes dentro da aplicação.

## REST

O REST é mais adequado quando a operação precisa de uma **resposta imediata**.

Exemplos nesta aplicação:

```text
Cadastrar produto
Consultar produto
Alterar produto
Excluir produto
Consultar fornecedor
Consultar categoria
```

Nessas operações, normalmente o usuário precisa saber imediatamente se a solicitação foi realizada com sucesso.

```text
Cliente
   │
   │ HTTP
   ▼
API REST
   │
   ▼
Processamento
   │
   ▼
Resposta HTTP
```

---

## Mensageria

A mensageria é mais adequada quando o processamento pode ocorrer **de forma assíncrona**, sem necessidade de manter a requisição original aberta.

Neste projeto, o principal exemplo é:

```text
Importação de fornecedores
```

O usuário envia o arquivo, a aplicação publica uma mensagem e o processamento ocorre posteriormente.

```text
Aplicação
   │
   ▼
RabbitMQ
   │
   ▼
Consumer
```

A mensageria também é adequada quando existe necessidade de desacoplamento entre componentes ou quando o processamento pode ser realizado posteriormente.

---

## Batch

O Spring Batch é mais adequado para operações que envolvem **grandes volumes de dados ou processamento estruturado em etapas**.

Neste projeto, o exemplo é:

```text
Importação de fornecedores através de CSV
```

O arquivo pode conter muitos fornecedores e o processamento é realizado em chunks de 50 registros.

```text
CSV
 │
 ▼
Reader
 │
 ▼
Processor
 │
 ▼
Writer
 │
 ▼
MySQL
```

Portanto, de forma resumida:

| Tecnologia | Quando utilizar                                                            | Exemplo na aplicação                      |
| ---------- | -------------------------------------------------------------------------- | ----------------------------------------- |
| REST       | Quando é necessária uma resposta imediata                                  | CRUD de produtos e fornecedores           |
| Mensageria | Quando o processamento pode ser assíncrono e desacoplado                   | Solicitação da importação de fornecedores |
| Batch      | Quando existe processamento de muitos registros ou etapas de processamento | Importação do CSV de fornecedores         |

---

# 19. Benefícios da solução

A solução implementada proporciona:

* Comunicação assíncrona entre componentes;
* Desacoplamento entre Producer e Consumer;
* Processamento independente da requisição HTTP;
* Possibilidade de manter mensagens na fila enquanto o Consumer estiver indisponível;
* Processamento de grandes quantidades de fornecedores;
* Processamento em chunks de 50 registros;
* Controle da execução através do Spring Batch;
* Separação dos metadados do Batch e dos dados da aplicação;
* Monitoramento das filas através do RabbitMQ Management;
* Organização da comunicação através do padrão Producer/Consumer.

---

# 20. Respostas aos requisitos da Etapa 4

### 1. Qual operação foi escolhida para comunicação assíncrona?

Foi escolhida a **importação de fornecedores através de arquivo CSV**.

O arquivo é recebido pela aplicação, armazenado temporariamente e seu processamento é solicitado através de uma mensagem enviada ao RabbitMQ.

### 2. Por que essa operação não precisa necessariamente ser concluída durante a requisição original?

Porque a importação pode envolver muitos registros e demandar um tempo maior de processamento. O usuário não precisa aguardar a conclusão de todos os registros para que a solicitação seja recebida pela aplicação.

### 3. O que acontece com a mensagem caso o consumidor esteja temporariamente indisponível?

A mensagem permanece disponível na fila do RabbitMQ para ser consumida posteriormente, quando houver um consumidor disponível, desde que a configuração da fila e das mensagens esteja adequada para essa retenção.

### 4. Qual funcionalidade foi escolhida para processamento em lote?

Foi escolhida a **importação em lote de fornecedores através de um arquivo CSV**, utilizando Spring Batch.

### 5. Por que essa funcionalidade é adequada para Batch?

Porque a importação trabalha com um conjunto de registros que pode ser grande e permite processamento sequencial, transformação dos dados e persistência em grupos de registros. O projeto utiliza chunks de 50 fornecedores.

### 6. Em quais situações da aplicação seria mais adequado utilizar REST, mensageria ou Batch?

**REST** é mais adequado para operações que precisam de resposta imediata, como consultas e operações de CRUD.

**Mensageria** é mais adequada para operações assíncronas, nas quais o processamento pode ocorrer posteriormente e existe necessidade de desacoplamento entre os componentes.

**Batch** é mais adequado para processamento de grandes volumes de dados ou operações estruturadas em etapas, como a importação de fornecedores através de CSV.

---

# 21. Resultado da Etapa 4

Ao final desta etapa, foi implementada uma solução completa envolvendo **REST, RabbitMQ e Spring Batch**, cada tecnologia sendo utilizada de acordo com sua finalidade.

O fluxo principal desenvolvido foi:

```text
                    ┌───────────────┐
                    │    Usuário    │
                    └───────┬───────┘
                            │
                            │ Upload CSV
                            ▼
                 ┌─────────────────────┐
                 │ Aplicação Principal │
                 └──────────┬──────────┘
                            │
                            │ Mensagem
                            ▼
                 ┌─────────────────────┐
                 │      RabbitMQ       │
                 │                     │
                 │ solicitar.processamento
                 └──────────┬──────────┘
                            │
                            │ Consumo
                            ▼
                 ┌─────────────────────┐
                 │ FornecedorConsumer  │
                 └──────────┬──────────┘
                            │
                            │ Inicia Job
                            ▼
                 ┌─────────────────────┐
                 │    Spring Batch     │
                 │                     │
                 │ Reader              │
                 │ Processor           │
                 │ Writer              │
                 └──────────┬──────────┘
                            │
                            ▼
                 ┌─────────────────────┐
                 │        MySQL        │
                 │     fornecedor      │
                 └─────────────────────┘
```

Dessa forma, a aplicação passou a possuir uma solução de **comunicação assíncrona baseada em RabbitMQ** e uma solução de **processamento em lote baseada em Spring Batch**, permitindo demonstrar na prática diferentes estratégias de comunicação e processamento dentro de uma aplicação distribuída.
