package com.api.config;

import com.categoria.domain.entity.CategoriaEntity;
import com.categoria.repository.CategoriaRepository;
import com.fornecedor.domain.entity.FornecedorEntity;
import com.fornecedor.repository.FornecedorRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final CategoriaRepository categoriaRepository;
    private final FornecedorRepository fornecedorRepository;

    public DataInitializer(CategoriaRepository categoriaRepository,
                           FornecedorRepository fornecedorRepository) {
        this.categoriaRepository = categoriaRepository;
        this.fornecedorRepository = fornecedorRepository;
    }

    @Override
    public void run(String... args) {
        if (categoriaRepository.count() == 0) {
            CategoriaEntity cat = new CategoriaEntity("Eletrônicos", "Equipamentos eletrônicos");
            cat.setId(1L);
            categoriaRepository.save(cat);
        }
        if (fornecedorRepository.count() == 0) {
            FornecedorEntity fornecedor = new FornecedorEntity(
                    "Distribuidora São Paulo",
                    "Fornecedor mockado"
            );
            fornecedorRepository.save(fornecedor);
        }
    }
}