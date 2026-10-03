package com.estoque.config;

import com.estoque.domain.Categoria;
import com.estoque.domain.Fornecedor;
import com.estoque.domain.Produto;
import com.estoque.domain.Usuario;
import com.estoque.repository.CategoriaRepository;
import com.estoque.repository.FornecedorRepository;
import com.estoque.repository.ProdutoRepository;
import com.estoque.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
    private final CategoriaRepository categorias;
    private final FornecedorRepository fornecedores;
    private final ProdutoRepository produtos;
    private final UsuarioRepository usuarios;

    public DataInitializer(CategoriaRepository categorias, FornecedorRepository fornecedores,
                           ProdutoRepository produtos, UsuarioRepository usuarios) {
        this.categorias = categorias;
        this.fornecedores = fornecedores;
        this.produtos = produtos;
        this.usuarios = usuarios;
    }

    @Override
    public void run(String... args) {
        Categoria categoria = categorias.save(new Categoria(null, "Eletrônicos", "Equipamentos eletrônicos"));
        Fornecedor fornecedor = fornecedores.save(new Fornecedor(null, "Distribuidora São Paulo", "fornecedor@exemplo.com"));
        produtos.save(new Produto(null, "Teclado USB", "Teclado para demonstração", 89.90, 20, categoria, fornecedor));
        usuarios.save(new Usuario(null, "Administrador", "admin", "ADMIN"));
    }
}
