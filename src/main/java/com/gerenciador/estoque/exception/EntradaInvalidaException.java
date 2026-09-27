package com.gerenciador.estoque.exception;

public class EntradaInvalidaException extends IllegalArgumentException{
    public EntradaInvalidaException(String mensagem) {
        super(mensagem);
    }
}
