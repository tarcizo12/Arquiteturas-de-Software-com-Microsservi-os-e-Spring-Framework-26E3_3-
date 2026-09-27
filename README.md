# Mapeamento da organização atual dos modulos e descrição de qual fucionalidade pode evoluir para um serviço proprio

# Mapa de dependências entre módulos

```
categoria   fornecedor   usuario
    \           |           |
     \          |           |  (via service, nunca repository)
      v         v           v
          produto  <----  movimentacao
              ^
              |
             api  (depende de todos; nenhum módulo de negócio depende de api)
```

## Módulo `produto`

### Papel no negócio

`produto` é o **núcleo do catálogo** e a **fonte de verdade do estoque**. Ele
não é só um CRUD de cadastro: é o módulo que decide se um produto pode ou não
ser movimentado, e é o único lugar do sistema autorizado a alterar a
quantidade em estoque de um produto — mesmo quando quem pede a alteração é
outro módulo (`movimentacao`).

### Modelo de domínio

`ProdutoEntity` é abstrata e usa herança `SINGLE_TABLE` (`tipo_produto` como
discriminator) com duas especializações:

- **`ProdutoPerecivel`**: acrescenta `dataValidade` e `lote`. Sua regra de
  `isValido()` inclui não estar vencido.
- **`ProdutoNaoPerecivel`**: acrescenta `garantiaMeses`.

Essa divisão existe porque as duas categorias de produto têm regras de
validade diferentes, mas compartilham tudo o resto (nome, preço, estoque,
categoria, fornecedor) — daí a escolha por herança em vez de dois agregados
separados.

`produto` tem relação `@ManyToOne` com `categoria` e `fornecedor`
(módulos auxiliares, só leitura de cadastro).

### Responsabilidades do `ProdutoService`

1. **CRUD do catálogo**: incluir, alterar, excluir, listar (por categoria,
   por fornecedor, todos ordenados por nome, com estoque baixo).
2. **Validação de regra por tipo**: perecível exige `dataValidade` + `lote`;
   não perecível exige `garantiaMeses`. Essa validação não dá pra fazer só
   com Bean Validation porque é condicional a outro campo (`perecivel`), por
   isso vive no service.
3. **Guarda contra troca de tipo em update**: uma vez criado como perecível
   ou não perecível, o produto não pode trocar de tipo num `alterar()` — o
   discriminator column da herança `SINGLE_TABLE` não foi feito pra isso.
4. **Porta de entrada para outros módulos mexerem no estoque**, via dois
   métodos pensados especificamente para uso por `movimentacao`:
    - `buscarEntidadeParaMovimentacao(idProduto)`: busca e valida
      (`isValido()`) o produto antes de qualquer movimentação.
    - `atualizarEstoque(produto, novaQuantidade)`: única forma permitida de
      persistir uma mudança de `quantidadeEstoque`.

   Esses dois métodos existem para que `movimentacao` **nunca** precise
   conhecer `ProdutoRepository`. Toda regra de "o que torna um produto apto a
   ser movimentado" fica centralizada aqui, não espalhada pelo módulo que
   está registrando a movimentação.

### Por que o estoque mora aqui, e não em `movimentacao`

`quantidadeEstoque` é um atributo do produto, não da movimentação — a
movimentação é o *evento* que causa a mudança, não o dono do dado. Manter o
estoque em `produto` significa que qualquer consulta de "quanto tem em
estoque agora" lê direto do catálogo, sem precisar somar histórico de
movimentações.

### Este módulo tende a virar o serviço principal (Consultar Produtos)

Na evolução planejada da arquitetura, `produto` (com `categoria` e
`fornecedor` fortemente acoplados a ele) é o candidato natural a se tornar
**o serviço principal** do sistema — o "Consultar Produtos" — enquanto
`movimentacao` sai como um serviço à parte que **consome** esse serviço (ver
seção seguinte). Por isso os dois métodos citados acima já foram desenhados
como uma fronteira de API clara: o dia que `movimentacao` virar um serviço
externo, esses dois métodos do `ProdutoService` são exatamente o que vira
endpoint HTTP (ex.: `GET /produtos/{id}/elegibilidade-movimentacao` e
`PATCH /produtos/{id}/estoque`).

### Dependências

- Depende de: `categoria`, `fornecedor` (leitura de cadastro).
- É consumido por: `movimentacao` (via `ProdutoService`), `api`.
- **Não depende de `movimentacao`** — a dependência é sempre nessa direção
  (movimentação conhece produto, produto não conhece movimentação). Isso é
  o que torna a extração futura possível sem dependência circular.

---

## Módulo `movimentacao`

### Papel no negócio

`movimentacao` registra **eventos de entrada e saída de estoque**: quem
(`usuario`), o quê (lista de `ItemMovimentacao`, cada um referenciando um
`produto` e uma quantidade), quando (`dataHora`) e por quê (`observacao`).
É um módulo essencialmente **transacional e de auditoria** — ele não é dono
de dado de catálogo nem de estoque atual, ele é dono do *histórico* de
mudanças de estoque.

### Modelo de domínio

- `MovimentacaoEntity`: cabeçalho da movimentação (`tipo` — `ENTRADA`/`SAIDA`,
  `dataHora`, `observacao`, `usuario`) e a lista de itens, adicionada via
  `adicionarItem(ItemMovimentacao)`.
- `ItemMovimentacaoEntity`: quantidade movimentada de um produto específico
  dentro daquela movimentação.
- `TipoMovimentacao` (enum): `ENTRADA` soma ao estoque, `SAIDA` subtrai.

### Responsabilidades do `MovimentacaoService`

1. Validar a requisição (usuário informado, tipo informado, pelo menos um
   item, quantidade válida por item).
2. Para cada item: pedir ao `produto` (via `ProdutoService`, nunca via
   repository) se o produto existe e está apto a ser movimentado, calcular a
   nova quantidade de estoque (soma se `ENTRADA`, subtrai com checagem de
   estoque suficiente se `SAIDA`) e mandar persistir essa nova quantidade —
   quem persiste é o `ProdutoService`, `movimentacao` só decide o número.
3. Montar e persistir a própria entidade `MovimentacaoEntity` (isso sim é
   responsabilidade exclusiva deste módulo).
4. Consultas de histórico: listar todas, obter por id.

A **regra de cálculo de saldo** (`ENTRADA` soma, `SAIDA` subtrai e valida se
há saldo suficiente) vive em `movimentacao`, não em `produto` — porque essa
regra é sobre *como interpretar um evento de movimentação*, não sobre o
produto em si. Já a regra "esse produto pode ser mexido?" (vencido,
inválido) vive em `produto`, porque é uma regra sobre o produto,
independente de quem está perguntando.

### Por que este módulo é o principal candidato a virar um serviço separado

A ideia é `movimentacao` se tornar, no futuro, **um serviço à parte do
serviço principal** (que seria o de consulta de produtos). Isso faz sentido
técnico e de negócio pelos seguintes motivos:

1. **Padrão de acesso diferente.** `produto` é predominantemente lido
   (consultas de catálogo, telas de listagem); `movimentacao` é
   predominantemente escrito (cada operação de estoque gera um novo
   registro). Volumes e picos de carga de escrita/leitura são diferentes o
   suficiente para justificar escalar os dois de forma independente.
2. **Fronteira de negócio já é clara no código.** `movimentacao` nunca
   acessa `ProdutoRepository`/`UsuarioRepository` diretamente — toda
   interação passa por `ProdutoService`/`UsuarioService`. Ou seja, a "API
   interna" que `movimentacao` usa hoje já é, na prática, o contrato que
   viraria uma chamada de rede (REST ou mensageria) numa arquitetura de
   serviços.
3. **Direção única de dependência.** `movimentacao` depende de `produto` e
   `usuario`, nunca o contrário. Isso evita dependência circular quando um
   dos dois lados sai do monólito primeiro.
4. **Consistência pode ser eventual sem quebrar o negócio.** Diferente de
   "consultar o catálogo", que geralmente precisa responder na hora,
   registrar uma movimentação pode tolerar ser processado de forma
   assíncrona (fila) sem impacto perceptível pro usuário — o que é o
   cenário típico onde vale a pena separar um serviço.

#### O que muda tecnicamente na extração

Quando `movimentacao` virar um serviço externo:

- As duas chamadas Java `produtoService.buscarEntidadeParaMovimentacao(...)`
  e `produtoService.atualizarEstoque(...)` viram chamadas HTTP (ou eventos)
  para o serviço "Consultar Produtos" — por isso elas já foram isoladas como
  métodos específicos no `ProdutoService`, em vez de `movimentacao` acessar
  `ProdutoEntity`/`ProdutoRepository` livremente.
- `usuarioService.getUsuarioById(...)` vira, da mesma forma, uma chamada
  para onde quer que a identidade do usuário passe a viver (pode continuar
  no monólito principal ou virar um serviço de identidade próprio,
  dependendo da evolução).
- `movimentacao` passa a ter seu próprio banco (ou schema), guardando
  `MovimentacaoEntity`/`ItemMovimentacaoEntity`, e talvez precise de uma
  cópia mínima/cache dos dados de produto que usa com mais frequência
  (nome, se é perecível) para não depender de uma chamada de rede em toda
  leitura de histórico.
- Precisa decidir consistência: se a chamada para debitar estoque falhar
  depois da movimentação já estar gravada (ou vice-versa), qual lado é a
  fonte de verdade e como reconciliar — hoje isso é resolvido de graça pela
  transação `@Transactional` local; separado em serviços, vira o principal
  problema de design a resolver (saga, outbox, etc.).

### Dependências

- Depende de: `produto` (via `ProdutoService`), `usuario` (via
  `UsuarioService`).
- É consumido por: `api`.
- Ninguém depende de `movimentacao` — é sempre o módulo que "puxa" dados dos
  outros, nunca o contrário. Essa unidirecionalidade é o que viabiliza a
  extração futura sem reescrever `produto`/`usuario`.

---

## Módulo `categoria`

### Papel no negócio

Cadastro simples de categorias de produto (ex.: "Bebidas", "Limpeza"). É um
módulo de apoio: existe pra dar contexto ao catálogo, não tem regra de
negócio própria além de CRUD básico.

### Modelo de domínio

`CategoriaEntity`: `id`, `nome`, `descricao`, e a coleção `produtos`
(`@OneToMany(mappedBy = "categoria")`) — usada só para o mapeamento JPA
funcionar, nunca navegada diretamente por outro módulo (ver nota no início
sobre isso).

### Dependências

- Não depende de nenhum outro módulo de negócio.
- É consumido por: `produto` (relação `@ManyToOne`), `api`.

---

## Módulo `fornecedor`

### Papel no negócio

Cadastro de fornecedores (`nome`, `cnpj`, `telefone`, `email`, `endereco`).
Assim como `categoria`, é um módulo de apoio ao catálogo, sem regra de
negócio própria além de CRUD.

### Dependências

- Não depende de nenhum outro módulo de negócio.
- É consumido por: `produto` (relação `@ManyToOne`), `api`.

---

## Módulo `usuario`

### Papel no negócio

Identifica quem executa uma ação no sistema — hoje, especificamente, quem
registra uma movimentação de estoque (`nome`, `login`, `senha`, `perfil`).

### Dependências

- Não depende de nenhum outro módulo de negócio.
- É consumido por: `movimentacao` (via `UsuarioService`, para saber o
  responsável por cada movimentação), `api`.

### Observação para evolução futura

Se `movimentacao` for extraído como serviço separado, vale decidir nesse
momento se `usuario` continua como parte do serviço principal (produto) ou
se evolui para um serviço de identidade/autenticação próprio, compartilhado
por ambos os serviços.

---

## Camada `api`

### Papel no negócio

Não é um módulo de negócio — é a **camada transversal de entrada** da
aplicação: controllers REST, configuração (`config`), exceções HTTP
(`exception`) e documentação Swagger (`swagger`). É aqui que os módulos de
negócio são compostos para atender uma requisição.

### Regra de dependência

`api` é o único pacote que pode depender de **todos** os módulos de negócio
(`produto`, `movimentacao`, `categoria`, `fornecedor`, `usuario`). O inverso
nunca acontece: nenhum `XxxService` de módulo de negócio pode importar algo
de `com.api`. Isso mantém os módulos de negócio testáveis e reutilizáveis
independente de estarem expostos via REST, e é o que garante que, se
`movimentacao` virar um serviço externo, ele leve consigo sua própria camada
`api` sem arrastar nada do restante do monólito.