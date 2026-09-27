package com.gerenciador.estoque.domain.entity;

import com.gerenciador.estoque.domain.enums.TipoMovimentacao;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "movimentacoes")
public class Movimentacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime dataHora;

    @Enumerated(EnumType.STRING)
    private TipoMovimentacao tipo;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    private String observacao;

    @OneToMany(mappedBy = "movimentacao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemMovimentacao> itens = new ArrayList<>();

    public Movimentacao() {}

    public Movimentacao(TipoMovimentacao tipo, Usuario usuario, String observacao) {
        this.dataHora = LocalDateTime.now();
        this.tipo = tipo;
        this.usuario = usuario;
        this.observacao = observacao;
    }

    public void adicionarItem(ItemMovimentacao item) {
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

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public List<ItemMovimentacao> getItens() {
        return itens;
    }

    public void setItens(List<ItemMovimentacao> itens) {
        this.itens = itens;
    }

    @Override
    public String toString() {
        return "Movimentacao{\n" +
                "  id=" + id + ",\n" +
                "  dataHora=" + dataHora + ",\n" +
                "  tipo=" + tipo + ",\n" +
                "  usuario=" + usuario.getNome() + ",\n" +
                "  observacao='" + observacao + "',\n" +
                "  quantidadeItens=" + itens.size() + "\n" +
                '}';
    }
}
