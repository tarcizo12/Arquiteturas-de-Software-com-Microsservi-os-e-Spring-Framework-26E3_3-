package com.produto.client.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ItemMovimentacaoRequest(
    @NotNull Long idProduto,
    @NotNull @Min(1) Integer quantidade
) {}
