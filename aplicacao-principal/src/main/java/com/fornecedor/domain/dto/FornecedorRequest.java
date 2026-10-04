package com.fornecedor.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados do fornecedor")
public class FornecedorRequest {

    @Schema(description = "ID do fornecedor", example = "1")
    private Long id;

    @Schema(description = "Nome do fornecedor", example = "Eletronicas LTDA", required = true)
    private String nome;


    public FornecedorRequest() {}

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
}