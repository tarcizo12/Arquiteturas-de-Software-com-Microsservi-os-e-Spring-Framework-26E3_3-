package com.movimentacao.domain.entity;


import com.produto.domain.entity.ProdutoEntity;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "itens_movimentacao")
@Data
public class ItemMovimentacaoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer quantidade;

    @ManyToOne
    @JoinColumn(name = "ProdutoEntity_id")
    private ProdutoEntity produto;

    @ManyToOne
    @JoinColumn(name = "movimentacao_id")
    private MovimentacaoEntity movimentacao;

    public ItemMovimentacaoEntity() {}

    public ItemMovimentacaoEntity(Integer quantidade, ProdutoEntity produto) {
        this.quantidade = quantidade;
        this.produto = produto;
    }


    @Override
    public String toString() {
        return "ItemMovimentacaoEntity{\n" +
                "  id=" + id + ",\n" +
                "  quantidade=" + quantidade + ",\n" +
                "  ProdutoEntity=" + this.getProduto().getNome() + ",\n" +
                "  movimentacao=" + movimentacao + "\n" +
                '}';
    }
}