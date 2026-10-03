package com.fornecedor.domain.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "fornecedores")
public class FornecedorEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    private String descricao;

    public FornecedorEntity() {}

    public FornecedorEntity(String nome, String descricao) {
        this.nome = nome;
        this.descricao = descricao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return "FornecedorEntity{\n" +
                "  id=" + id + "\n" +
                "  nome='" + nome + "'\n" +
                "  descricao='" + descricao + "'\n" +
                '}';
    }

}