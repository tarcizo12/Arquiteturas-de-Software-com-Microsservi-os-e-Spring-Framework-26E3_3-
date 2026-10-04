package com.fornecedor.domain.dto;

public record FornecedorResponse(
        Long id,
        String nome,
        String cnpj,
        String telefone,
        String email,
        String endereco){}