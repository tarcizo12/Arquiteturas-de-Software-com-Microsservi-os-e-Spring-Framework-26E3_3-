package com.estoque.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "categorias")
public class Categoria {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private String descricao;

    public Categoria() {}
    public Categoria(Long id, String nome, String descricao) {
        this.id = id; this.nome = nome; this.descricao = descricao;
    }
    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getDescricao() { return descricao; }
}
