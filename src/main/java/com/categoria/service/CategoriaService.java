package com.categoria.service;

import com.categoria.repository.CategoriaRepository;
import org.springframework.stereotype.Service;

@Service
public class CategoriaService {

    private final CategoriaRepository repository;

    public CategoriaService(CategoriaRepository categoriaRepository){
        this.repository = categoriaRepository;
    }
}
