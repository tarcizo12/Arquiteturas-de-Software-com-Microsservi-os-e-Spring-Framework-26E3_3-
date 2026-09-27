package com.categoria.exception;

public class RegistroNaoLocalizadoException extends RuntimeException{
    public RegistroNaoLocalizadoException(String mensagem) {
        super(mensagem);
    }
}
