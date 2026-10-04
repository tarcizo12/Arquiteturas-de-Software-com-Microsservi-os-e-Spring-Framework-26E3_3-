package com.produto.domain.entity;

import com.categoria.domain.entity.CategoriaEntity;
import com.fornecedor.domain.entity.FornecedorEntity;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_produto", discriminatorType = DiscriminatorType.STRING, length = 20)
@Table(name = "produto")
@Data
public abstract class ProdutoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome", nullable = false, length = 150)
    private String nome;

    @ManyToOne(optional = false)
    @JoinColumn(name = "categoria_id", nullable = false)
    private CategoriaEntity categoria;

    @ManyToOne(optional = false)
    @JoinColumn(name = "fornecedor_id", nullable = false)
    private FornecedorEntity fornecedor;

    @Column(name = "descricao", length = 255)
    private String descricao;

    // Coluna DECIMAL(12,2) no banco
    @Column(name = "preco", nullable = false)
    private Double preco;

    @Column(name = "quantidade_estoque", nullable = false)
    private Integer quantidadeEstoque = 0;

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