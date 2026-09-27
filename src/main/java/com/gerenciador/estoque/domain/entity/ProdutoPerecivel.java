package com.gerenciador.estoque.domain.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.time.LocalDate;

@Entity
@DiscriminatorValue("PERECIVEL")
public class ProdutoPerecivel extends Produto {

    private LocalDate dataValidade;
    private String lote;
    public ProdutoPerecivel() {}

    public ProdutoPerecivel(String nome, String descricao, Double preco, Integer quantidadeEstoque,
                            Categoria categoria, Fornecedor fornecedor,
                            LocalDate dataValidade, String lote) {
        super(nome, descricao, preco, quantidadeEstoque, categoria, fornecedor);
        this.dataValidade = dataValidade;
        this.lote = lote;
    }

    @Override
    public boolean isValido() { return LocalDate.now().isBefore(dataValidade);}

    public LocalDate getDataValidade() {
        return dataValidade;
    }

    public void setDataValidade(LocalDate dataValidade) {
        this.dataValidade = dataValidade;
    }

    public String getLote() {
        return lote;
    }

    public void setLote(String lote) {
        this.lote = lote;
    }

    @Override
    public String toString() {
        return "ProdutoPerecivel{\n" +
                dadosToString() + ",\n" +
                "  dataValidade=" + dataValidade + ",\n" +
                "  lote='" + lote + "'\n" +
                "}";
    }
}