package com.movimentacao.service;

import com.movimentacao.domain.ItemMovimentacaoEntity;
import com.movimentacao.domain.MovimentacaoEntity;
import com.movimentacao.dto.MovimentacaoRequest;
import com.movimentacao.dto.MovimentacaoResponse;
import com.movimentacao.repository.MovimentacaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class MovimentacaoService {
    private final MovimentacaoRepository repository;

    public MovimentacaoService(MovimentacaoRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public MovimentacaoResponse registrar(MovimentacaoRequest request) {
        request.itens().forEach(item -> {
            if (item.quantidade() == null || item.quantidade() <= 0) {
                throw new IllegalArgumentException("Quantidade deve ser maior que zero.");
            }
        });

        MovimentacaoEntity entity = new MovimentacaoEntity();
        entity.setDataHora(LocalDateTime.now());
        entity.setTipo(request.tipo());
        entity.setIdUsuarioResponsavel(request.idUsuarioResponsavel());
        entity.setNomeUsuario(request.nomeUsuario());
        entity.setObservacao(request.observacao());

        request.itens().forEach(item ->
            entity.adicionarItem(new ItemMovimentacaoEntity(item.idProduto(), item.quantidade()))
        );

        return MovimentacaoResponse.from(repository.save(entity));
    }

    @Transactional(readOnly=true)
    public List<MovimentacaoResponse> listar() {
        return repository.findAll().stream().map(MovimentacaoResponse::from).toList();
    }

    @Transactional(readOnly=true)
    public MovimentacaoResponse obter(Long id) {
        return repository.findById(id)
            .map(MovimentacaoResponse::from)
            .orElseThrow(() -> new java.util.NoSuchElementException("Movimentação não encontrada."));
    }
}
