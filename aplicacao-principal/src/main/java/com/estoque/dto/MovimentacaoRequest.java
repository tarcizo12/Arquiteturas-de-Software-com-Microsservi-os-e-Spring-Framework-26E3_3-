package com.estoque.dto;

import com.estoque.client.dto.MovimentacaoRequest.TipoMovimentacao;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record MovimentacaoRequest(
    @NotNull TipoMovimentacao tipo,
    @NotNull Long idUsuarioResponsavel,
    String observacao,
    @NotEmpty List<@Valid Item> itens
) {
    public record Item(@NotNull Long idProduto, @NotNull Integer quantidade) {}
}
