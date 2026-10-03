package com.movimentacao.repository;
import com.movimentacao.domain.MovimentacaoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
public interface MovimentacaoRepository extends JpaRepository<MovimentacaoEntity, Long> {}
