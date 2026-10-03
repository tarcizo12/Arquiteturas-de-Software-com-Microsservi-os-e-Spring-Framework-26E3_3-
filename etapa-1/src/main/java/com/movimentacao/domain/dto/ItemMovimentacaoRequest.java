package com.movimentacao.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "Item de uma movimentação")
@Data
public class ItemMovimentacaoRequest {

    @Schema(description = "Id do movimentado", required = true)
    private Long  idProdutoMovimetado;

    @Schema(description = "Quantidade", example = "10", required = true)
    private Integer quantidade;
}