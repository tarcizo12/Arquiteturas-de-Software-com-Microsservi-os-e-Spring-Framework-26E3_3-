package com.estoque.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "usuarios")
public class Usuario {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private String login;
    private String perfil;

    public Usuario() {}
    public Usuario(Long id, String nome, String login, String perfil) {
        this.id=id; this.nome=nome; this.login=login; this.perfil=perfil;
    }
    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getLogin() { return login; }
    public String getPerfil() { return perfil; }
}
