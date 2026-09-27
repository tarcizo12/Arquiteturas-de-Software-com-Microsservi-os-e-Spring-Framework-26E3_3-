package com.movimentacao.repository;

import com.movimentacao.domain.entity.ItemMovimentacaoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemMovimentacaoRepository extends JpaRepository<ItemMovimentacaoEntity, Long> {
}