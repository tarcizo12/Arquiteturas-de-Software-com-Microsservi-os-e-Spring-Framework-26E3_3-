package com.gerenciador.estoque.repository;

import com.gerenciador.estoque.domain.entity.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    List<Produto> findByNomeContainingIgnoreCase(String nome);

    List<Produto> findByCategoriaId(Long categoriaId);

    List<Produto> findByFornecedorId(Long fornecedorId);

    List<Produto> findByQuantidadeEstoqueLessThan(Integer quantidade);

    List<Produto> findAllByOrderByNomeAsc();
}