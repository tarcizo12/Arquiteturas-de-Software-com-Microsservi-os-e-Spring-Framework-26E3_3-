package com.usuario.domain.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "usuario")
@Data
public class UsuarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome", nullable = false, length = 150)
    private String nome;

    @Column(name = "login", nullable = false, unique = true, length = 50)
    private String login;

    @Column(name = "senha", nullable = false, length = 255)
    private String senha;

    @Column(name = "perfil", nullable = false, length = 30)
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