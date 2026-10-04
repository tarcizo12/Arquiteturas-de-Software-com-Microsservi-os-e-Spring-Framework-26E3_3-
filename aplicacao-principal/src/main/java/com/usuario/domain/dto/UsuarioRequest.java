package com.usuario.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "Dados do usuario")
@Data
public class UsuarioRequest {

    @Schema(description = "ID do usuario", example = "1")
    private Long id;

    @Schema(description = "Nome do usuario", example = "Jose", required = true)
    private String nome;
}