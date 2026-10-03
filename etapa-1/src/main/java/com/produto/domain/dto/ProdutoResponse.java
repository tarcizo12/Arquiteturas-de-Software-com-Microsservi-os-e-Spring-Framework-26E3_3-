package com.produto.domain.dto;

import com.produto.domain.entity.ProdutoEntity;
import com.produto.domain.entity.ProdutoNaoPerecivel;
import com.produto.domain.entity.ProdutoPerecivel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProdutoResponse {

    private Long idProduto;
    private String nome;
    private String descricao;
    private Double preco;
    private Integer quantidadeEstoque;

    private Long idCategoria;
    private String nomeCategoria;

    private Long idFornecedor;
    private String nomeFornecedor;

    private Boolean perecivel;
    private LocalDate dataValidade;
    private String lote;
    private Integer garantiaMeses;

    // Mantido para não quebrar chamadas existentes que usam só o id
    // (ex.: retorno simplificado de inclusão). Prefira fromEntity(...) sempre que possível.
    public ProdutoResponse(Long idProduto) {
        this.idProduto = idProduto;
    }

    public static ProdutoResponse fromEntity(ProdutoEntity entity) {
        ProdutoResponseBuilder builder = ProdutoResponse.builder()
                .idProduto(entity.getId())
                .nome(entity.getNome())
                .descricao(entity.getDescricao())
                .preco(entity.getPreco())
                .quantidadeEstoque(entity.getQuantidadeEstoque())
                .idCategoria(entity.getCategoria() != null ? entity.getCategoria().getId() : null)
                .nomeCategoria(entity.getCategoria() != null ? entity.getCategoria().getNome() : null)
                .idFornecedor(entity.getFornecedor() != null ? entity.getFornecedor().getId() : null)
                .nomeFornecedor(entity.getFornecedor() != null ? entity.getFornecedor().getNome() : null);

        if (entity instanceof ProdutoPerecivel perecivel) {
            builder.perecivel(true)
                    .dataValidade(perecivel.getDataValidade())
                    .lote(perecivel.getLote());
        } else if (entity instanceof ProdutoNaoPerecivel naoPerecivel) {
            builder.perecivel(false)
                    .garantiaMeses(naoPerecivel.getGarantiaMeses());
        }

        return builder.build();
    }
}