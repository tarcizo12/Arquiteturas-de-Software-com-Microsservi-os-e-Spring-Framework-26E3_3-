package com.movimentacao.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "movimentacao")
public class MovimentacaoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    // Se TipoMovimentacao estiver em outro pacote, adicione o import.
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 10)
    private TipoMovimentacao tipo;

    @Column(name = "observacao", length = 255)
    private String observacao;

    /**
     * Mapeia a coluna usuario_id (referencia logica ao usuario do banco
     * estoque_principal; sem FK pois os dados estao em outro banco).
     */
    @Column(name = "usuario_id", nullable = false)
    private Long idUsuarioResponsavel;


    @OneToMany(mappedBy = "movimentacao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemMovimentacaoEntity> itens = new ArrayList<>();

    @PrePersist
    void aoPersistir() {
        if (dataHora == null) {
            dataHora = LocalDateTime.now();
        }
    }

    public void adicionarItem(ItemMovimentacaoEntity item) {
        item.setMovimentacao(this);
        this.itens.add(item);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }

    public TipoMovimentacao getTipo() { return tipo; }
    public void setTipo(TipoMovimentacao tipo) { this.tipo = tipo; }

    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }

    public Long getIdUsuarioResponsavel() { return idUsuarioResponsavel; }
    public void setIdUsuarioResponsavel(Long idUsuarioResponsavel) { this.idUsuarioResponsavel = idUsuarioResponsavel; }

    public List<ItemMovimentacaoEntity> getItens() { return itens; }
    public void setItens(List<ItemMovimentacaoEntity> itens) { this.itens = itens; }
}