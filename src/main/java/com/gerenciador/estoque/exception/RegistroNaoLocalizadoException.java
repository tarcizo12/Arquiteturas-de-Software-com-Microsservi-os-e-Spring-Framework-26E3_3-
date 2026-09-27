package com.gerenciador.estoque.exception;

public class RegistroNaoLocalizadoException extends RuntimeException{
    public RegistroNaoLocalizadoException(String mensagem) {
        super(mensagem);
    }
}
