package com.gerenciador.estoque.domain.dto;

import com.gerenciador.estoque.domain.entity.Usuario;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados básicos do usuário responsável")
public class UsuarioRequest {

    @Schema(description = "ID do usuário", example = "1", required = true)
    private Long id;

    @Schema(description = "Nome do usuário", example = "João Silva", required = true)
    private String nome;

    @Schema(description = "Login do usuário", example = "joao.silva")
    private String login;

    @Schema(description = "Perfil (ADMIN ou OPERADOR)", example = "OPERADOR")
    private String perfil;


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

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPerfil() {
        return perfil;
    }

    public void setPerfil(String perfil) {
        this.perfil = perfil;
    }

    public Usuario toUsuario() {
        Usuario usuario = new Usuario();
        usuario.setId(this.getId());
        usuario.setNome(this.getNome());
        usuario.setLogin(this.getLogin());
        usuario.setPerfil(this.getPerfil());
        return usuario;
    }
}