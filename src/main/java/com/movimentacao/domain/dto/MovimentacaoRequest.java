package com.movimentacao.domain.dto;

import com.movimentacao.domain.entity.MovimentacaoEntity;
import com.movimentacao.domain.enums.TipoMovimentacao;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Schema(description = "Dados para registrar uma movimentação (entrada ou saída)")
@Data
public class MovimentacaoRequest {

    @Schema(description = "Tipo da movimentação", example = "ENTRADA", required = true)
    private TipoMovimentacao tipo;

    @Schema(description = "Usuário responsável", required = true)
    private Long idUsuarioResposavel;

    @Schema(description = "Observação opcional", example = "Compra inicial")
    private String observacao;

    @Schema(description = "Lista de itens movimentados", required = true)
    private List<ItemMovimentacaoRequest> itens = new ArrayList<>();

    public MovimentacaoEntity toMovimentacaoEntity() {
        return new MovimentacaoEntity(this.tipo, this.observacao);
    }
}