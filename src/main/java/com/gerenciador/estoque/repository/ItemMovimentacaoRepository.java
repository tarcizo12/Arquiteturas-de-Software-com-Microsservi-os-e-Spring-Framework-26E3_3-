package com.gerenciador.estoque.repository;

import com.gerenciador.estoque.domain.entity.ItemMovimentacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemMovimentacaoRepository extends JpaRepository<ItemMovimentacao, Long> {
    List<ItemMovimentacao> findByProdutoId(Long produtoId);
    List<ItemMovimentacao> findByMovimentacaoId(Long movimentacaoId);
}