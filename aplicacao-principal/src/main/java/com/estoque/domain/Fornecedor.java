package com.estoque.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "fornecedores")
public class Fornecedor {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private String email;

    public Fornecedor() {}
    public Fornecedor(Long id, String nome, String email) {
        this.id = id; this.nome = nome; this.email = email;
    }
    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
}
