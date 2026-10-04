# Etapa 4 — Comunicação Assíncrona com RabbitMQ

## 1. Objetivo

Nesta etapa foi implementada uma funcionalidade de **comunicação assíncrona** entre os componentes da aplicação utilizando **RabbitMQ** como message broker.

O objetivo é permitir que uma solicitação de processamento seja enviada para uma fila, sem que o componente responsável pelo envio precise aguardar o processamento da mensagem para continuar sua execução.

A implementação utiliza o padrão **Producer/Consumer**, onde:

* O **Producer** publica uma mensagem no RabbitMQ.
* O **RabbitMQ** recebe e mantém a mensagem na fila.
* O **Consumer** fica aguardando novas mensagens.
* O **Consumer** recebe e processa a mensagem de forma independente.

---

# 2. Arquitetura da comunicação

A comunicação implementada segue o seguinte fluxo:

```text
┌──────────────────────┐
│   Aplicação Principal│
│                      │
│     Fornecedor       │
└──────────┬───────────┘
           │
           │ envia mensagem
           ▼
┌──────────────────────┐
│  FornecedorProducer  │
│                      │
│    RabbitTemplate     │
└──────────┬───────────┘
           │
           │ mensagem
           ▼
┌────────────────────────────────┐
│           RabbitMQ             │
│                                │
│  Fila: solicitar.processamento │
└──────────┬─────────────────────┘
           │
           │ entrega mensagem
           ▼
┌──────────────────────┐
│  FornecedorConsumer  │
│                      │
│ @RabbitListener      │
└──────────┬───────────┘
           │
           ▼
      Processamento
```

A principal característica dessa arquitetura é o **desacoplamento** entre quem produz a mensagem e quem realiza o processamento.

O Producer não precisa conhecer a implementação interna do Consumer. Ele apenas publica a mensagem na fila definida.

---

# 3. RabbitMQ

O RabbitMQ foi utilizado como **message broker** da aplicação.

Sua responsabilidade é receber, armazenar e encaminhar as mensagens publicadas pelos Producers para os Consumers interessados.

Neste projeto foi criada a fila:

```text
solicitar.processamento
```

Essa fila representa o canal utilizado para as solicitações de processamento assíncrono.

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

# 4. Fila solicitar.processamento

A fila utilizada pela funcionalidade é:

```text
solicitar.processamento
```

A fila deve existir no RabbitMQ para que o Producer possa publicar as mensagens e o Consumer possa recebê-las.

Durante a configuração atual do ambiente, a fila é criada no RabbitMQ antes da execução da funcionalidade.

A aplicação utiliza a referência da fila através do arquivo `application.properties`:

```properties
rabbitmq.queue.solicitar-processamento=solicitar.processamento
```

Dessa forma, o nome da fila não fica diretamente espalhado pelo código da aplicação.

---

# 5. Producer

O Producer é responsável por publicar as mensagens no RabbitMQ.

Foi criada a classe:

```text
com.messaging.FornecedorProducer
```

A comunicação com o RabbitMQ é realizada utilizando o `RabbitTemplate`, disponibilizado pelo Spring AMQP.

O Producer recebe uma `String` e publica seu conteúdo na fila:

```text
solicitar.processamento
```

O fluxo é:

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

O Producer não precisa aguardar o Consumer concluir o processamento da mensagem.

Essa característica permite que a aplicação continue sua execução mesmo que o processamento posterior seja realizado em outro momento.

---

# 6. Consumer

O Consumer é responsável por receber as mensagens disponibilizadas na fila.

Foi criada a classe:

```text
com.messaging.FornecedorConsumer
```

O recebimento da mensagem é realizado através da anotação:

```java
@RabbitListener
```

A configuração utiliza a mesma referência definida no `application.properties`:

```properties
rabbitmq.queue.solicitar-processamento=solicitar.processamento
```

Quando uma mensagem é publicada na fila, o Consumer recebe automaticamente o conteúdo.

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
Processamento da mensagem
```

---

# 7. Funcionamento assíncrono

A principal característica implementada nesta etapa é a **execução assíncrona**.

Em uma comunicação síncrona, o componente que realiza uma solicitação normalmente precisa aguardar uma resposta para continuar o fluxo.

Na abordagem implementada, o Producer apenas publica a mensagem:

```text
Producer
   │
   │ publica
   ▼
RabbitMQ
```

O processamento posterior fica sob responsabilidade do Consumer:

```text
RabbitMQ
   │
   │ entrega posteriormente
   ▼
Consumer
```

Isso permite que os dois componentes tenham menor acoplamento temporal.

O Producer não precisa esperar que o Consumer finalize seu processamento.

---

# 8. Configuração do RabbitMQ

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

# 9. Configuração no Docker Compose

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

A aplicação principal depende do RabbitMQ estar disponível antes de iniciar sua comunicação com o broker.

---

# 10. Configuração da aplicação

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

# 11. Teste da funcionalidade

Com o RabbitMQ em execução, é possível acessar o painel administrativo:

```text
http://localhost:15672
```

Após o login, a fila deve estar disponível em:

```text
Queues and Streams
```

com o nome:

```text
solicitar.processamento
```

Uma mensagem pode ser publicada através do Producer.

Por exemplo:

```text
teste de processamento
```

O RabbitMQ recebe a mensagem e a disponibiliza na fila.

O Consumer, por sua vez, recebe a mensagem através do `@RabbitListener`.

O resultado esperado é:

```text
Mensagem recebida: teste de processamento
```

---

# 12. Benefícios da solução

A utilização do RabbitMQ nesta etapa proporciona:

* Comunicação assíncrona entre componentes.
* Desacoplamento entre Producer e Consumer.
* Processamento independente das mensagens.
* Possibilidade de o Consumer processar as mensagens posteriormente.
* Redução da dependência temporal entre os componentes.
* Possibilidade de adicionar novos Consumers futuramente.
* Gerenciamento das mensagens através do RabbitMQ.
* Monitoramento das filas através do painel administrativo.

---

# 13. Resultado da Etapa 4

Ao final desta etapa, foi implementado um fluxo completo de comunicação assíncrona:

```text
┌──────────────┐
│   Producer   │
└──────┬───────┘
       │
       │ String
       ▼
┌───────────────────────┐
│       RabbitMQ        │
│                       │
│ solicitar.processamento│
└──────────┬────────────┘
           │
           │ String
           ▼
┌──────────────┐
│   Consumer   │
└──────────────┘