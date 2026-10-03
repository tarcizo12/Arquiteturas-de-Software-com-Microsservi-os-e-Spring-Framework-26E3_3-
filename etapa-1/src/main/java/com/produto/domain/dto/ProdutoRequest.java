package com.produto.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ProdutoRequest {

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
    private String nome;

    @Size(max = 255)
    private String descricao;

    @NotNull(message = "Preço é obrigatório")
    @Min(value = 0, message = "Preço não pode ser negativo")
    private Double preco;

    @NotNull(message = "Quantidade em estoque é obrigatória")
    @Min(value = 0, message = "Quantidade não pode ser negativa")
    private Integer quantidadeEstoque;

    @NotNull(message = "Id da categoria é obrigatório")
    private Long idCategoria;

    @NotNull(message = "Id do fornecedor é obrigatório")
    private Long idFornecedor;

    @NotNull(message = "Deve informar se é perecível")
    private Boolean perecivel;

    @Schema(description = "Data de validade (obrigatório se perecivel)", example = "2026-12-31")
    private LocalDate dataValidade;

    @Schema(description = "Lote (obrigatório se perecivel)", example = "L12345")
    private String lote;

    @Schema(description = "Garantia em meses (obrigatório se não perecivel)", example = "12")
    @Min(value = 0, message = "Garantia não pode ser negativa")
    private Integer garantiaMeses;
}