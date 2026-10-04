package com.produto.domain.entity;

import com.categoria.domain.entity.CategoriaEntity;
import com.fornecedor.domain.entity.FornecedorEntity;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@DiscriminatorValue("NAO_PERECIVEL")
public class ProdutoNaoPerecivel extends ProdutoEntity {

    @Getter
    @Setter
    @Column(name = "garantia_meses")
    private Integer garantiaMeses;

    public ProdutoNaoPerecivel() {}

    public ProdutoNaoPerecivel(String nome,
                               String descricao,
                               Double preco,
                               Integer quantidadeEstoque,
                               CategoriaEntity categoria,
                               FornecedorEntity fornecedor,
                               Integer garantiaMeses) {
        super(nome, descricao, preco, quantidadeEstoque, categoria, fornecedor);
        this.garantiaMeses = garantiaMeses;
    }


    @Override
    public boolean isValido() { return true; }

    @Override
    public String toString() {
        return "ProdutoNaoPerecivel{\n" +
                dadosToString() + ",\n" +
                "  garantiaMeses=" + garantiaMeses + "\n" +
                "}";
    }
}