package com.movimentacao.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="movimentacoes")
public class MovimentacaoEntity {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime dataHora;

    @Enumerated(EnumType.STRING)
    private TipoMovimentacao tipo;

    private Long idUsuarioResponsavel;
    private String nomeUsuario;
    private String observacao;

    @OneToMany(mappedBy="movimentacao", cascade=CascadeType.ALL, orphanRemoval=true)
    private List<ItemMovimentacaoEntity> itens = new ArrayList<>();

    public MovimentacaoEntity() {}

    public void adicionarItem(ItemMovimentacaoEntity item) {
        itens.add(item);
        item.setMovimentacao(this);
    }

    public Long getId() { return id; }
    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }
    public TipoMovimentacao getTipo() { return tipo; }
    public void setTipo(TipoMovimentacao tipo) { this.tipo = tipo; }
    public Long getIdUsuarioResponsavel() { return idUsuarioResponsavel; }
    public void setIdUsuarioResponsavel(Long idUsuarioResponsavel) { this.idUsuarioResponsavel = idUsuarioResponsavel; }
    public String getNomeUsuario() { return nomeUsuario; }
    public void setNomeUsuario(String nomeUsuario) { this.nomeUsuario = nomeUsuario; }
    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }
    public List<ItemMovimentacaoEntity> getItens() { return itens; }
}
