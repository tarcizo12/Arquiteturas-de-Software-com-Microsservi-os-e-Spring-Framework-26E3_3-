package com.movimentacao.domain.entity;

import com.movimentacao.domain.enums.TipoMovimentacao;
import com.usuario.domain.entity.UsuarioEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "movimentacoes")
public class MovimentacaoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime dataHora;

    @Enumerated(EnumType.STRING)
    private TipoMovimentacao tipo;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private UsuarioEntity usuario;

    private String observacao;

    @OneToMany(mappedBy = "movimentacao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemMovimentacaoEntity> itens = new ArrayList<>();

    public MovimentacaoEntity() {}

    public MovimentacaoEntity(TipoMovimentacao tipo, String observacao) {
        this.dataHora = LocalDateTime.now();
        this.tipo = tipo;
        this.usuario = new UsuarioEntity();
        this.observacao = observacao;
    }

    public void adicionarItem(ItemMovimentacaoEntity item) {
        itens.add(item);
        item.setMovimentacao(this);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public TipoMovimentacao getTipo() {
        return tipo;
    }

    public void setTipo(TipoMovimentacao tipo) {
        this.tipo = tipo;
    }

    public UsuarioEntity getUsuarioEntity() {
        return usuario;
    }

    public void setUsuarioEntity(UsuarioEntity usuario) {
        this.usuario = usuario;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public List<ItemMovimentacaoEntity> getItens() {
        return itens;
    }

    public void setItens(List<ItemMovimentacaoEntity> itens) {
        this.itens = itens;
    }

    @Override
    public String toString() {
        return "MovimentacaoEntity{\n" +
                "  id=" + id + ",\n" +
                "  dataHora=" + dataHora + ",\n" +
                "  tipo=" + tipo + ",\n" +
                "  usuario=" + usuario.getNome() + ",\n" +
                "  observacao='" + observacao + "',\n" +
                "  quantidadeItens=" + itens.size() + "\n" +
                '}';
    }
}
