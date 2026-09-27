package com.gerenciador.estoque.repository;

import com.gerenciador.estoque.domain.entity.Movimentacao;
import com.gerenciador.estoque.domain.enums.TipoMovimentacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MovimentacaoRepository extends JpaRepository<Movimentacao, Long> {

    List<Movimentacao> findByTipo(TipoMovimentacao tipo);

    List<Movimentacao> findByDataHoraBetween(LocalDateTime inicio, LocalDateTime fim);

    List<Movimentacao> findByUsuarioId(Long usuarioId);

    List<Movimentacao> findByTipoAndDataHoraBetween(TipoMovimentacao tipo,
                                                    LocalDateTime inicio,
                                                    LocalDateTime fim);

    List<Movimentacao> findAllByOrderByDataHoraDesc();
}