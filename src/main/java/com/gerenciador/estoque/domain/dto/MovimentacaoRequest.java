package com.gerenciador.estoque.domain.dto;

import com.gerenciador.estoque.domain.entity.ItemMovimentacao;
import com.gerenciador.estoque.domain.entity.Movimentacao;
import com.gerenciador.estoque.domain.entity.Produto;
import com.gerenciador.estoque.domain.enums.TipoMovimentacao;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.ArrayList;
import java.util.List;

@Schema(description = "Dados para registrar uma movimentação (entrada ou saída)")
public class MovimentacaoRequest {

    @Schema(description = "Tipo da movimentação", example = "ENTRADA", required = true)
    private TipoMovimentacao tipo;

    @Schema(description = "Usuário responsável", required = true)
    private UsuarioRequest usuario;

    @Schema(description = "Observação opcional", example = "Compra inicial")
    private String observacao;

    @Schema(description = "Lista de itens movimentados", required = true)
    private List<ItemMovimentacaoRequest> itens = new ArrayList<>();

    public TipoMovimentacao getTipo() {
        return tipo;
    }

    public void setTipo(TipoMovimentacao tipo) {
        this.tipo = tipo;
    }

    public UsuarioRequest getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioRequest usuario) {
        this.usuario = usuario;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public List<ItemMovimentacaoRequest> getItens() {
        return itens;
    }

    public void setItens(List<ItemMovimentacaoRequest> itens) {
        this.itens = itens;
    }

    public Movimentacao toMovimentacao() {
        Movimentacao mov = new Movimentacao(this.tipo, this.usuario.toUsuario(), this.observacao);
        for (ItemMovimentacaoRequest itemReq : this.itens) {
            Produto produto = itemReq.getProduto(); 
            ItemMovimentacao item = new ItemMovimentacao(itemReq.getQuantidade(), produto);
            mov.adicionarItem(item);
        }
        return mov;
    }


}