package com.categoria.domain.entity;

import com.produto.domain.entity.ProdutoEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "categorias")
@Data
public class CategoriaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    private String descricao;

    @OneToMany(mappedBy = "categoria")
    @EqualsAndHashCode.Exclude
    private List<ProdutoEntity> produtos = new ArrayList<>();

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