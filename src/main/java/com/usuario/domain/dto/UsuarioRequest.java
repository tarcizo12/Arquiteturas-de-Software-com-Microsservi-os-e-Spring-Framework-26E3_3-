package com.usuario.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "Dados de uma categoria")
@Data
public class UsuarioRequest {

    @Schema(description = "ID da categoria", example = "1")
    private Long id;

    @Schema(description = "Nome da categoria", example = "Eletrônicos", required = true)
    private String nome;

    @Schema(description = "Descrição da categoria", example = "Equipamentos eletrônicos")
    private String descricao;
}