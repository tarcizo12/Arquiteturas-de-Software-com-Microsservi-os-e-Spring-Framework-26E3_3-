package com.gerenciador.estoque.domain.dto;

import com.gerenciador.estoque.domain.entity.Categoria;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados de uma categoria")
public class CategoriaRequest {

    @Schema(description = "ID da categoria", example = "1")
    private Long id;

    @Schema(description = "Nome da categoria", example = "Eletrônicos", required = true)
    private String nome;

    @Schema(description = "Descrição da categoria", example = "Equipamentos eletrônicos")
    private String descricao;

    public CategoriaRequest() {}

    public CategoriaRequest(Categoria categoria) {
        this.id = categoria.getId();
        this.nome = categoria.getNome();
        this.descricao = categoria.getDescricao();
    }

    public Categoria toEntity() {
        Categoria categoria = new Categoria(this.nome, this.descricao);
        categoria.setId(this.id);
        return categoria;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}