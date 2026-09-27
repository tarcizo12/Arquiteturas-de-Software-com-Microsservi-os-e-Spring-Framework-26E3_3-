package com.gerenciador.estoque.domain.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("NAO_PERECIVEL")
public class ProdutoNaoPerecivel extends Produto {

    private Integer garantiaMeses;

    public ProdutoNaoPerecivel() {}

    public ProdutoNaoPerecivel(String nome, String descricao, Double preco, Integer quantidadeEstoque,
                               Categoria categoria, Fornecedor fornecedor,
                               Integer garantiaMeses) {
        super(nome, descricao, preco, quantidadeEstoque, categoria, fornecedor);
        this.garantiaMeses = garantiaMeses;
    }

    @Override
    public boolean isValido() { return true; }

    public Integer getGarantiaMeses() {
        return garantiaMeses;
    }

    public void setGarantiaMeses(Integer garantiaMeses) {
        this.garantiaMeses = garantiaMeses;
    }

    @Override
    public String toString() {
        return "ProdutoNaoPerecivel{\n" +
                dadosToString() + ",\n" +
                "  garantiaMeses=" + garantiaMeses + "\n" +
                "}";
    }
}
