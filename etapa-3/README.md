## Reflexão Arquitetural – Etapa 3

### 1. Quais configurações da aplicação podem variar entre ambientes?

As configurações que podem variar entre os ambientes de desenvolvimento e produção são principalmente a porta da aplicação, a URL de conexão com o banco de dados, o usuário e a senha do banco, além de algumas configurações relacionadas à execução da aplicação, como o comportamento do Hibernate e a exibição dos comandos SQL.

Também podem variar as configurações relacionadas à comunicação entre os serviços, como as URLs utilizadas para acessar outros serviços da aplicação.

### 2. Quais dessas configurações foram externalizadas?

As configurações relacionadas ao ambiente foram externalizadas utilizando arquivos de configuração específicos para cada profile, como `application-dev.properties` e `application-prod.properties`.

Além disso, foram utilizadas variáveis de ambiente para configurações como:

- `SERVER_PORT`
- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`
- `JPA_DDL_AUTO`
- `JPA_SHOW_SQL`

Dessa forma, informações específicas do ambiente, principalmente as credenciais do banco de dados, não precisam ficar diretamente inseridas no código Java.

### 3. Por que um serviço não deve acessar diretamente o banco de outro serviço?

Cada serviço deve ser responsável pelos seus próprios dados e pelas regras relacionadas a esses dados. Permitir que um serviço acesse diretamente as tabelas de outro cria um forte acoplamento entre as aplicações.

Neste projeto, a comunicação entre responsabilidades separadas deve ocorrer através das interfaces disponibilizadas pelos próprios serviços, como APIs HTTP. Dessa forma, cada serviço pode alterar sua estrutura interna ou seu banco de dados sem obrigatoriamente quebrar os demais serviços.

Essa separação também facilita a evolução, manutenção, escalabilidade e implantação independente das aplicações.

### 4. Qual problema o Docker resolve no projeto?

O Docker permite empacotar a aplicação juntamente com o ambiente necessário para sua execução, tornando o comportamento da aplicação mais previsível e reproduzível em diferentes ambientes.

Com a utilização de containers, a aplicação não depende diretamente da configuração específica da máquina do desenvolvedor. Dessa forma, diferenças relacionadas a sistema operacional, instalação de dependências, versões de ferramentas e configuração do ambiente são reduzidas.

No projeto, o Docker também permite executar as aplicações e os bancos de dados de forma isolada e padronizada.

### 5. Qual é a função do Docker Compose?

O Docker Compose permite definir e executar os principais componentes do projeto de forma integrada.

Por meio do arquivo `docker-compose.yml`, é possível configurar os principais componentes da solução, como os bancos de dados e, quando aplicável, as aplicações e o Config Server, especificando suas respectivas configurações, redes, portas, volumes e dependências.

Assim, os componentes podem ser iniciados de maneira padronizada através de um único comando, facilitando a execução e os testes da solução completa.

Além disso, os containers podem se comunicar através da rede criada pelo Docker Compose utilizando os nomes dos serviços, evitando a utilização de `localhost` para a comunicação entre containers.

### 6. Qual problema uma configuração centralizada procura resolver?

A configuração centralizada procura evitar que cada aplicação distribuída mantenha suas configurações espalhadas e duplicadas em diferentes projetos.

Com a utilização do Spring Cloud Config Server, configurações compartilhadas ou específicas dos ambientes podem ser disponibilizadas de forma centralizada para as aplicações.

Isso facilita a manutenção e a alteração das configurações, pois uma mudança pode ser realizada no local responsável pelo gerenciamento das configurações sem a necessidade de modificar o código-fonte das aplicações.

Dessa forma, a configuração fica separada da lógica de negócio, contribuindo para uma arquitetura mais flexível e adequada à execução em diferentes ambientes.

---

## 7. Execução com Docker Compose

O projeto possui um arquivo `docker-compose.yml` responsável pela definição e execução dos containers utilizados pela solução.

Antes de executar o Compose, é necessário garantir que o **Docker Desktop** esteja instalado e em execução.

Na raiz do projeto, onde está localizado o arquivo `docker-compose.yml`, execute:

```bash
docker compose up -d
````

O parâmetro `-d` faz com que os containers sejam executados em segundo plano, permitindo continuar utilizando o terminal.

Para verificar os containers em execução, utilize:

```bash
docker compose ps
```

Para acompanhar os logs dos serviços:

```bash
docker compose logs -f
```

Para parar os containers sem remover os dados persistidos:

```bash
docker compose stop
```

Para iniciar novamente os containers que foram parados:

```bash
docker compose start
```

Caso seja necessário parar e remover os containers criados pelo Compose:

```bash
docker compose down
```

Os volumes não são removidos pelo comando `docker compose down` quando utilizados separadamente, permitindo preservar os dados dos bancos.

Para remover também os volumes e recriar o ambiente de banco do zero, pode ser utilizado o script `resetar-banco.bat`, descrito abaixo.

### Execução simplificada

O fluxo básico utilizando Docker Compose é:

```text
                 docker compose up -d
                         |
                         v
              +----------------------+
              | Containers iniciados |
              +----------+-----------+
                         |
                         v
              Bancos disponíveis
                         |
                         v
              Aplicações executadas
                         |
                         v
                    Testes
```

Dessa forma, o Docker Compose centraliza a configuração da infraestrutura necessária para execução do projeto e permite reproduzir o ambiente de maneira padronizada.

---

## 8. Scripts de gerenciamento (.bat)

Além dos comandos do Docker Compose, o projeto possui arquivos `.bat` para facilitar a execução das tarefas mais utilizadas durante o desenvolvimento no Windows.

Os scripts podem ser executados diretamente com **duplo clique** ou pelo terminal, desde que o Docker Desktop esteja em execução.

### `subir-banco.bat`

O arquivo `subir-banco.bat` automatiza a inicialização dos bancos de dados do projeto.

Ele executa os comandos necessários para:

* iniciar os containers MySQL;
* aguardar os bancos ficarem disponíveis;
* executar os scripts SQL de inicialização;
* preparar os bancos para utilização pelas aplicações.

Assim, em vez de executar manualmente vários comandos do Docker Compose e do MySQL, basta executar:

```text
subir-banco.bat
```

### `resumo-bancos.bat`

O arquivo `resumo-bancos.bat` foi criado para facilitar a consulta da estrutura dos bancos.

Ele permite verificar informações como:

* bancos existentes;
* tabelas;
* campos;
* tipos de dados;
* chaves estrangeiras;
* quantidade aproximada de registros.

Pode ser utilizado para verificar rapidamente se a estrutura dos bancos está de acordo com o esperado.

Para executar:

```text
resumo-bancos.bat
```

### `resetar-banco.bat`

O arquivo `resetar-banco.bat` automatiza a limpeza completa do ambiente de banco utilizado nos testes.

Ele remove os containers, volumes e imagens relacionados ao ambiente configurado no Docker Compose.

Como os volumes são removidos, os dados armazenados nos bancos também são apagados.

Após a execução do reset, o ambiente pode ser criado novamente utilizando:

```text
subir-banco.bat
```

Esse processo permite retornar os bancos para um estado inicial e repetir os testes desde o começo.

### Resumo dos scripts

| Arquivo             | Finalidade                                                          |
| ------------------- | ------------------------------------------------------------------- |
| `subir-banco.bat`   | Inicia e prepara os bancos de dados                                 |
| `resumo-bancos.bat` | Consulta e apresenta a estrutura dos bancos                         |
| `resetar-banco.bat` | Remove o ambiente de banco e seus dados para uma nova inicialização |

Os arquivos `.bat` funcionam como uma camada de automação sobre os comandos do Docker Compose, tornando o projeto mais simples de executar e administrar no ambiente Windows.
