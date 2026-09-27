package com.gerenciador.estoque.domain.dto;

import java.time.LocalDateTime;

public class ErroResponse {
    private LocalDateTime timestamp;
    private String mensagem;
    private int status;
    private String path;

    public ErroResponse() {
        this.timestamp = LocalDateTime.now();
    }

    public ErroResponse(String mensagem, int status, String path) {
        this.timestamp = LocalDateTime.now();
        this.mensagem = mensagem;
        this.status = status;
        this.path = path;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}