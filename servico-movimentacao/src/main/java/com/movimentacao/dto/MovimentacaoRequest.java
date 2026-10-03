package com.movimentacao.dto;

import com.movimentacao.domain.TipoMovimentacao;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record MovimentacaoRequest(
    @NotNull TipoMovimentacao tipo,
    @NotNull Long idUsuarioResponsavel,
    String nomeUsuario,
    String observacao,
    @NotEmpty List<@Valid ItemRequest> itens
) {
    public record ItemRequest(@NotNull Long idProduto, @NotNull Integer quantidade) {}
}
