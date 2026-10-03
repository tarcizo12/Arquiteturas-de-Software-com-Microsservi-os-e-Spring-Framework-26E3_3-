package com.movimentacao.dto;

import com.movimentacao.domain.MovimentacaoEntity;
import java.time.LocalDateTime;
import java.util.List;

public record MovimentacaoResponse(
    Long id,
    LocalDateTime dataHora,
    String tipo,
    Long idUsuarioResponsavel,
    String nomeUsuario,
    String observacao,
    List<ItemResponse> itens
) {
    public record ItemResponse(Long idProduto, Integer quantidade) {}

    public static MovimentacaoResponse from(MovimentacaoEntity e) {
        return new MovimentacaoResponse(
            e.getId(), e.getDataHora(), e.getTipo().name(),
            e.getIdUsuarioResponsavel(), e.getNomeUsuario(), e.getObservacao(),
            e.getItens().stream().map(i -> new ItemResponse(i.getIdProduto(), i.getQuantidade())).toList()
        );
    }
}
