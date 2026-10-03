package com.movimentacao.domain;

import jakarta.persistence.*;

@Entity
@Table(name="itens_movimentacao")
public class ItemMovimentacaoEntity {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    private Long idProduto;
    private Integer quantidade;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="movimentacao_id")
    private MovimentacaoEntity movimentacao;

    public ItemMovimentacaoEntity() {}
    public ItemMovimentacaoEntity(Long idProduto, Integer quantidade) {
        this.idProduto = idProduto; this.quantidade = quantidade;
    }

    public Long getId() { return id; }
    public Long getIdProduto() { return idProduto; }
    public Integer getQuantidade() { return quantidade; }
    public MovimentacaoEntity getMovimentacao() { return movimentacao; }
    public void setMovimentacao(MovimentacaoEntity movimentacao) { this.movimentacao = movimentacao; }
}
