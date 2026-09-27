package com.gerenciador.estoque.domain.dto;

import com.gerenciador.estoque.domain.entity.Produto;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Item de uma movimentação")
public class ItemMovimentacaoRequest {

    @Schema(description = "Produto movimentado", required = true)
    private Produto produto;

    @Schema(description = "Quantidade", example = "10", required = true)
    private Integer quantidade;

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }
}