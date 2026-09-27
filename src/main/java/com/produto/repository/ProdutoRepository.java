package com.produto.repository;

import com.produto.domain.entity.ProdutoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProdutoRepository extends JpaRepository<ProdutoEntity, Long> {

    List<ProdutoEntity> findByCategoriaId(Long categoriaId);

    List<ProdutoEntity> findByFornecedorId(Long fornecedorId);

    List<ProdutoEntity> findAllByOrderByNomeAsc();

    List<ProdutoEntity> findByQuantidadeEstoqueLessThan(Integer limite);
}