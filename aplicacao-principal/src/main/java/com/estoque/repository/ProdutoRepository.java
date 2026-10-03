package com.estoque.repository;
import com.estoque.domain.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ProdutoRepository extends JpaRepository<Produto, Long> {}
