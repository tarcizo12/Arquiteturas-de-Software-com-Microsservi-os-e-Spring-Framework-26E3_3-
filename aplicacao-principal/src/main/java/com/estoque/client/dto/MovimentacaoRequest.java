package com.estoque.client.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record MovimentacaoRequest(
    @NotNull TipoMovimentacao tipo,
    @NotNull Long idUsuarioResponsavel,
    String observacao,
    @NotEmpty List<@Valid ItemMovimentacaoRequest> itens
) {
    public enum TipoMovimentacao { ENTRADA, SAIDA }
}
