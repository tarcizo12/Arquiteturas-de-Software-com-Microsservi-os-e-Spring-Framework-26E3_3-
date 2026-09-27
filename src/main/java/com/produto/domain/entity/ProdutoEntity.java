package com.produto.domain.entity;

import com.categoria.domain.entity.CategoriaEntity;
import com.fornecedor.domain.entity.FornecedorEntity;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_produto", discriminatorType = DiscriminatorType.STRING)
@Table(name = "produtos")
@Data
public abstract class ProdutoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    // Renomeado de "categorias" (plural, incorreto pra um @ManyToOne) para "categoria".
    // Isso também corrige o bug do ProdutoRepository.findByCategoriaId(...), que
    // não encontrava a propriedade "categoriaId" por causa desse nome errado.
    // ATENÇÃO: troquei o nome da coluna de "CategoriaEntity_id" para "categoria_id".
    // Se a tabela "produtos" já existe em algum ambiente com dados, isso exige uma
    // migration (Flyway/Liquibase) renomeando a coluna — senão o Hibernate vai
    // procurar uma coluna "categoria_id" que não existe. Se preferir não migrar
    // agora, troque o "categoria_id" abaixo de volta para "CategoriaEntity_id" e
    // mantenha só a renomeação do campo Java (que já resolve o bug do repository).
    @ManyToOne
    @JoinColumn(name = "categoria_id")
    private CategoriaEntity categoria;

    @ManyToOne
    @JoinColumn(name = "fornecedor_id")
    private FornecedorEntity fornecedor;

    private String descricao;

    private Double preco;

    private Integer quantidadeEstoque;

    public ProdutoEntity() {}

    public ProdutoEntity(String nome,
                         String descricao,
                         Double preco,
                         Integer quantidadeEstoque,
                         CategoriaEntity categoria,
                         FornecedorEntity fornecedor) {
        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
        this.quantidadeEstoque = quantidadeEstoque;
        this.categoria = categoria;
        this.fornecedor = fornecedor;
    }

    public abstract boolean isValido();

    protected String dadosToString() {
        return "  id=" + id + ",\n" +
                "  nome='" + nome + "',\n" +
                "  descricao='" + descricao + "',\n" +
                "  preco=" + preco + ",\n" +
                "  quantidadeEstoque=" + quantidadeEstoque + ",\n" +
                "  categoria=" + categoria.getNome() + ",\n" +
                "  fornecedor=" + fornecedor.getNome();
    }

    @Override
    public String toString() {
        return "ProdutoEntity{\n" +
                dadosToString() +
                "\n}";
    }
}