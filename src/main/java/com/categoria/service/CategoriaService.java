package com.categoria.service;

import com.categoria.domain.entity.CategoriaEntity;
import com.categoria.repository.CategoriaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoriaService {

    private final CategoriaRepository repository;

    @Autowired
    public CategoriaService(CategoriaRepository categoriaRepository){
        this.repository = categoriaRepository;
    }

    public CategoriaEntity getCategoriaById(Long id){
        return this.repository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException(
                    "Categoria não encontrada para o id " + id));
    }
}
