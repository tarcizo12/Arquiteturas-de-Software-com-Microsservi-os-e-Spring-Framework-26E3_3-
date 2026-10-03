package com.api.exception;

public class RegistroNaoLocalizadoException extends RuntimeException{
    public RegistroNaoLocalizadoException(String mensagem) {
        super(mensagem);
    }
}
