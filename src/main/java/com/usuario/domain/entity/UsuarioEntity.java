package com.usuario.domain.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "usuarios")
@Data
public class UsuarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, unique = true, length = 100)
    private String login;

    @Column(nullable = false)
    private String senha;

    @Column(nullable = false, length = 50)
    private String perfil;

    public UsuarioEntity() {}

    public UsuarioEntity(String nome, String login, String senha, String perfil) {
        this.nome = nome;
        this.login = login;
        this.senha = senha;
        this.perfil = perfil;
    }

    @Override
    public String toString() {
        return "UsuarioEntity{\n" +
                "  id=" + id + "\n" +
                "  nome='" + nome + "'\n" +
                "  login='" + login + "'\n" +
                "  perfil='" + perfil + "'\n" +
                '}';
    }
}