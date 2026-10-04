Claro. Vou montar um `README.md`/documento Markdown específico para **execução e teste da funcionalidade de importação assíncrona de fornecedores**, incluindo o `fornecedores.csv`, endpoint, RabbitMQ e fluxo Producer → fila → Consumer.

# Teste da Importação Assíncrona de Fornecedores

## Objetivo

Este documento apresenta como executar e testar a funcionalidade de **importação de fornecedores por arquivo CSV**, utilizando comunicação assíncrona com **RabbitMQ**.

A funcionalidade permite enviar um arquivo CSV para a aplicação. O arquivo é armazenado temporariamente e uma mensagem é enviada para uma fila do RabbitMQ para que o processamento possa ser realizado de forma assíncrona.

---

## Pré-requisitos

Antes de executar o teste, certifique-se de que:

* A aplicação principal esteja executando;
* O container do RabbitMQ esteja em execução;
* A fila `solicitar.processamento` esteja criada no RabbitMQ;
* A aplicação esteja configurada para acessar o RabbitMQ em `localhost:5672`.

O RabbitMQ utiliza:

```text
Host: localhost
Porta AMQP: 5672
Usuário: admin
Senha: admin
```

A interface de gerenciamento pode ser acessada em:

```text
http://localhost:15672
```

---

## Arquivo CSV para teste

O projeto possui um arquivo chamado:

```text
fornecedores.csv
```

Esse arquivo está disponível no diretório do projeto e pode ser utilizado para realizar os testes de importação.

Não é necessário criar um novo arquivo para realizar o teste inicial. Basta utilizar o arquivo disponibilizado no projeto.

---

## Endpoint de importação

A importação é realizada através do endpoint:

```http
PUT /com/api/fornecedores/importacao
```

O endpoint recebe um arquivo CSV através de uma requisição `multipart/form-data`.

### Parâmetro

O nome do campo utilizado para enviar o arquivo é:

```text
arquivo
```

---

## Executando pelo Postman

Abra o Postman e crie uma nova requisição.

### 1. Método

Selecione:

```text
PUT
```

### 2. URL

Utilize:

```text
http://localhost:8080/com/api/fornecedores/importacao
```

### 3. Body

Acesse:

```text
Body
→ form-data
```

Adicione uma propriedade com:

```text
Key: arquivo
Type: File
Value: fornecedores.csv
```

O resultado deve ficar semelhante a:

| Key     | Type | Value            |
| ------- | ---- | ---------------- |
| arquivo | File | fornecedores.csv |

Não é necessário informar manualmente o `Content-Type`. O Postman irá gerar o `multipart/form-data` automaticamente.

---

## Fluxo da funcionalidade

Após o envio do arquivo, o fluxo da aplicação ocorre da seguinte maneira:

```text
Postman
   |
   | PUT /com/api/fornecedores/importacao
   v
Aplicação Principal
   |
   | Recebe fornecedores.csv
   v
Armazenamento temporário
   |
   | Envia mensagem
   v
FornecedorProducer
   |
   | RabbitTemplate
   v
RabbitMQ
   |
   | fila: solicitar.processamento
   v
FornecedorConsumer
   |
   | Recebe mensagem
   v
Processamento assíncrono
```

A comunicação entre o Producer e o Consumer não ocorre diretamente.

O **RabbitMQ atua como intermediário**, armazenando a mensagem na fila até que o Consumer esteja disponível para recebê-la.

---

## Fila utilizada

A funcionalidade utiliza a seguinte fila:

```text
solicitar.processamento
```

Essa fila precisa estar disponível no RabbitMQ antes da realização do teste.

No painel de gerenciamento do RabbitMQ, acesse:

```text
Queues and Streams
```

e verifique se a fila:

```text
solicitar.processamento
```

está disponível.

---

## Configuração do RabbitMQ

A aplicação utiliza as seguintes propriedades:

```properties
spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=admin
spring.rabbitmq.password=admin

rabbitmq.queue.solicitar-processamento=solicitar.processamento
```

Como a aplicação principal está sendo executada localmente pela IDE, o host utilizado é:

```text
localhost
```

O RabbitMQ, por sua vez, está sendo executado através do Docker.

---

## Validando o envio da mensagem

Depois de enviar o arquivo pelo Postman, verifique o RabbitMQ.

Acesse:

```text
http://localhost:15672
```

Faça login utilizando:

```text
Usuário: admin
Senha: admin
```

Depois acesse a fila:

```text
solicitar.processamento
```

Durante o processamento, a mensagem enviada pelo Producer poderá ser visualizada na fila.

---

## Validação do Consumer

O Consumer está configurado para escutar a fila:

```text
solicitar.processamento
```

Quando uma mensagem é recebida, o método do Consumer é acionado automaticamente.

A aplicação apresenta no console:

```text
Mensagem recebida: <mensagem>
```

Isso permite confirmar que a mensagem publicada pelo Producer foi recebida pelo Consumer.

---

## Resultado esperado

Ao executar o teste corretamente, o fluxo esperado é:

1. O arquivo `fornecedores.csv` é enviado através do endpoint;
2. A aplicação recebe o arquivo;
3. O arquivo é armazenado temporariamente;
4. O Producer publica uma mensagem no RabbitMQ;
5. A mensagem é direcionada para a fila `solicitar.processamento`;
6. O Consumer recebe a mensagem;
7. O processamento assíncrono é iniciado.

O principal objetivo desta etapa é demonstrar a comunicação assíncrona entre os componentes da aplicação utilizando RabbitMQ.

---

## Teste rápido

Para realizar um teste rápido:

### 1. Inicie o RabbitMQ

```bash
docker compose up -d rabbitmq
```

### 2. Verifique o RabbitMQ

Acesse:

```text
http://localhost:15672
```

Utilize:

```text
admin
admin
```

### 3. Confirme a fila

Verifique se existe:

```text
solicitar.processamento
```

### 4. Execute a aplicação principal

Execute a aplicação pela IDE ou através do Maven.

### 5. Envie o CSV

No Postman:

```text
PUT http://localhost:8080/com/api/fornecedores/importacao
```

Em:

```text
Body → form-data
```

utilize:

```text
arquivo = fornecedores.csv
```

### 6. Verifique o console

Confirme o recebimento da mensagem pelo:

```text
FornecedorConsumer
```

---

## Estrutura utilizada

A funcionalidade utiliza os seguintes componentes principais:

```text
com
├── api
│   └── controller
│       └── ...
│
├── fornecedor
│   ├── service
│   │   └── ...
│   └── messaging
│       └── producer
│           └── FornecedorProducer
│
└── messaging
    └── FornecedorConsumer
```

O Producer utiliza o `RabbitTemplate` para publicar mensagens no RabbitMQ.

O Consumer utiliza `@RabbitListener` para consumir mensagens da fila:

```text
solicitar.processamento
```

---

## Conclusão

A funcionalidade implementada demonstra o uso de **comunicação assíncrona** na aplicação através do RabbitMQ.

O envio do arquivo CSV não depende de uma comunicação direta entre o componente responsável pela importação e o componente responsável pelo processamento. A mensagem é encaminhada para uma fila, permitindo que o processamento seja realizado posteriormente pelo Consumer.

Dessa forma, a funcionalidade apresenta os conceitos de:

* Producer;
* Consumer;
* Message Broker;
* Fila;
* Comunicação assíncrona;
* RabbitMQ;
* Processamento desacoplado.