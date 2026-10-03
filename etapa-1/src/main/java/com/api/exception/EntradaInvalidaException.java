package com.api.exception;

public class EntradaInvalidaException extends IllegalArgumentException{
    public EntradaInvalidaException(String mensagem) {
        super(mensagem);
    }
}
