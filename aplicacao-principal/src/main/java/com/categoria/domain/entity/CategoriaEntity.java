package com.categoria.domain.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "categoria")
@Data
public class CategoriaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome", nullable = false, unique = true, length = 100)
    private String nome;

    @Column(name = "descricao", length = 255)
    private String descricao;

    public CategoriaEntity() {}

    public CategoriaEntity(String nome, String descricao) {
        this.nome = nome;
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return "CategoriaEntity{\n" +
                "  id=" + id + "\n" +
                "  nome='" + nome + "'\n" +
                "  descricao='" + descricao + "'\n" +
                '}';
    }
}