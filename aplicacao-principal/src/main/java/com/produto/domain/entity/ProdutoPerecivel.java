package com.produto.domain.entity;

import com.categoria.domain.entity.CategoriaEntity;
import com.fornecedor.domain.entity.FornecedorEntity;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@DiscriminatorValue("PERECIVEL")
public class ProdutoPerecivel extends ProdutoEntity {
    @Getter @Setter
    private LocalDate dataValidade;

    @Getter @Setter
    private String lote;

    public ProdutoPerecivel() {}

    public ProdutoPerecivel(String nome,
                            String descricao,
                            Double preco,
                            Integer quantidadeEstoque,
                            CategoriaEntity categoria,
                            FornecedorEntity fornecedor,
                            LocalDate dataValidade,
                            String lote) {
        super(nome, descricao, preco, quantidadeEstoque, categoria, fornecedor);
        this.dataValidade = dataValidade;
        this.lote = lote;
    }

    @Override
    public boolean isValido() { return LocalDate.now().isBefore(dataValidade);}

    @Override
    public String toString() {
        return "ProdutoPerecivel{\n" +
                dadosToString() + ",\n" +
                "  dataValidade=" + dataValidade + ",\n" +
                "  lote='" + lote + "'\n" +
                "}";
    }
}