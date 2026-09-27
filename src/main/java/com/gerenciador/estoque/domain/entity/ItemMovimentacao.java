package com.gerenciador.estoque.domain.entity;


import jakarta.persistence.*;

@Entity
@Table(name = "itens_movimentacao")
public class ItemMovimentacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer quantidade;

    @ManyToOne
    @JoinColumn(name = "produto_id")
    private Produto produto;

    @ManyToOne
    @JoinColumn(name = "movimentacao_id")
    private Movimentacao movimentacao;
    public ItemMovimentacao() {}

    public ItemMovimentacao(Integer quantidade, Produto produto) {
        this.quantidade = quantidade;
        this.produto = produto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public Movimentacao getMovimentacao() {
        return movimentacao;
    }

    public void setMovimentacao(Movimentacao movimentacao) {
        this.movimentacao = movimentacao;
    }

    @Override
    public String toString() {
        return "ItemMovimentacao{\n" +
                "  id=" + id + ",\n" +
                "  quantidade=" + quantidade + ",\n" +
                "  produto=" + produto.getNome() + ",\n" +
                "  movimentacao=" + movimentacao + "\n" +
                '}';
    }
}