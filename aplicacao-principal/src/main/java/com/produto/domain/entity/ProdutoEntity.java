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