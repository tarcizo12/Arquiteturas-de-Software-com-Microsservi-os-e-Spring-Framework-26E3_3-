package com.gerenciador.estoque.config;

import com.gerenciador.estoque.domain.entity.Categoria;
import com.gerenciador.estoque.domain.entity.Fornecedor;
import com.gerenciador.estoque.repository.CategoriaRepository;
import com.gerenciador.estoque.repository.FornecedorRepository;
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
            Categoria cat = new Categoria("Eletrônicos", "Equipamentos eletrônicos");
            cat.setId(1L);
            categoriaRepository.save(cat);
        }
        if (fornecedorRepository.count() == 0) {
            Fornecedor fornecedor = new Fornecedor(
                    "Distribuidora São Paulo",
                    "12.345.678/0001-90",
                    "(11) 99999-8888",
                    "contato@distribuidora.com",
                    "Rua das Flores, 123 - São Paulo/SP"
            );
            fornecedorRepository.save(fornecedor);
        }
    }
}