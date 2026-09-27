package com.gerenciador.estoque.domain.dto;

import com.gerenciador.estoque.domain.entity.*;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class ProdutoRequest {
    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
    private String nome;

    @Size(max = 255)
    private String descricao;

    @NotNull(message = "Preço é obrigatório")
    @Min(value = 0, message = "Preço não pode ser negativo")
    private Double preco;

    @NotNull(message = "Quantidade em estoque é obrigatória")
    @Min(value = 0, message = "Quantidade não pode ser negativa")
    private Integer quantidadeEstoque;

    @NotNull(message = "Categoria é obrigatória")
    private CategoriaRequest categoria;

    @NotNull(message = "Fornecedor é obrigatório")
    private FornecedorRequest fornecedor;

    @NotNull(message = "Deve informar se é perecível")
    private Boolean perecivel;

    @Schema(description = "Data de validade (obrigatório se perecivel)", example = "2026-12-31")
    private LocalDate dataValidade;

    @Schema(description = "Lote (obrigatório se perecivel)", example = "L12345")
    private String lote;

    @Schema(description = "Garantia em meses (obrigatório se não perecivel)", example = "12")
    @Min(value = 0, message = "Garantia não pode ser negativa")
    private Integer garantiaMeses;

    public Produto toProduto() {
        Categoria categoria = this.getCategoria().toEntity();
        Fornecedor fornecedor = this.getFornecedor().toEntity();

        if (this.isPerecivel()) {
            return new ProdutoPerecivel(
                    this.getNome(),
                    this.getDescricao(),
                    this.getPreco(),
                    this.getQuantidadeEstoque(),
                    categoria,
                    fornecedor,
                    this.getDataValidade(),
                    this.getLote()
            );
        } else {
            return new ProdutoNaoPerecivel(
                    this.getNome(),
                    this.getDescricao(),
                    this.getPreco(),
                    this.getQuantidadeEstoque(),
                    categoria,
                    fornecedor,
                    this.getGarantiaMeses()
            );
        }
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Integer getGarantiaMeses() {
        return garantiaMeses;
    }

    public void setGarantiaMeses(Integer garantiaMeses) {
        this.garantiaMeses = garantiaMeses;
    }

    public String getLote() {
        return lote;
    }

    public void setLote(String lote) {
        this.lote = lote;
    }

    public LocalDate getDataValidade() {
        return dataValidade;
    }

    public void setDataValidade(LocalDate dataValidade) {
        this.dataValidade = dataValidade;
    }

    public boolean isPerecivel() {
        return perecivel;
    }

    public void setPerecivel(boolean perecivel) {
        this.perecivel = perecivel;
    }

    public FornecedorRequest getFornecedor() {
        return fornecedor;
    }

    public void setFornecedor(FornecedorRequest fornecedor) {
        this.fornecedor = fornecedor;
    }

    public CategoriaRequest getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaRequest categoria) {
        this.categoria = categoria;
    }

    public Integer getQuantidadeEstoque() {
        return quantidadeEstoque;
    }

    public void setQuantidadeEstoque(Integer quantidadeEstoque) {
        this.quantidadeEstoque = quantidadeEstoque;
    }

    public Double getPreco() {
        return preco;
    }

    public void setPreco(Double preco) {
        this.preco = preco;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}