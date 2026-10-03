package com.fornecedor.service;

import com.fornecedor.domain.entity.FornecedorEntity;
import com.fornecedor.repository.FornecedorRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FornecedorService {

    private final FornecedorRepository repository;

    @Autowired
    public FornecedorService(FornecedorRepository fornecedorRepository){
        this.repository = fornecedorRepository;
    }

    public FornecedorEntity getFornecedorById(Long id){
        return this.repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Forecedor não encontrado para o id " + id));
    }
}
