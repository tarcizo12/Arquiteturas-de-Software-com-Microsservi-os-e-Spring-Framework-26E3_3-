package com.estoque.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "produtos")
public class Produto {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private String descricao;
    private Double preco;
    private Integer quantidadeEstoque;

    @ManyToOne(optional = false)
    private Categoria categoria;

    @ManyToOne(optional = false)
    private Fornecedor fornecedor;

    public Produto() {}
    public Produto(Long id, String nome, String descricao, Double preco, Integer quantidadeEstoque,
                   Categoria categoria, Fornecedor fornecedor) {
        this.id=id; this.nome=nome; this.descricao=descricao; this.preco=preco;
        this.quantidadeEstoque=quantidadeEstoque; this.categoria=categoria; this.fornecedor=fornecedor;
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getDescricao() { return descricao; }
    public Double getPreco() { return preco; }
    public Integer getQuantidadeEstoque() { return quantidadeEstoque; }
    public void setQuantidadeEstoque(Integer quantidadeEstoque) { this.quantidadeEstoque = quantidadeEstoque; }
    public Categoria getCategoria() { return categoria; }
    public Fornecedor getFornecedor() { return fornecedor; }
}
