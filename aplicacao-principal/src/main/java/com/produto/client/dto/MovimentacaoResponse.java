package com.produto.client.dto;

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
}
