package com.api.config;

import com.categoria.domain.entity.CategoriaEntity;
import com.categoria.repository.CategoriaRepository;
import com.fornecedor.domain.entity.FornecedorEntity;
import com.fornecedor.repository.FornecedorRepository;
import com.usuario.domain.entity.UsuarioEntity;
import com.usuario.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
    private final CategoriaRepository categorias;
    private final FornecedorRepository fornecedores;
    private final UsuarioRepository usuarios;

    public DataInitializer(CategoriaRepository categorias, FornecedorRepository fornecedores, UsuarioRepository usuarios) {
        this.categorias = categorias;
        this.fornecedores = fornecedores;
        this.usuarios = usuarios;
    }

    @Override
    public void run(String... args) {
        categorias.save(new CategoriaEntity("Eletrônicos", "Equipamentos eletrônicos"));
        fornecedores.save(new FornecedorEntity("Distribuidora São Paulo", "fornecedor@exemplo.com"));
        usuarios.save(new UsuarioEntity("Jose Alves", "Administrador", "admin", "ADMIN"));
    }
}
