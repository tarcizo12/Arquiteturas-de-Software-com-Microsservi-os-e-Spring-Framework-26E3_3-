package com.movimentacao.service;

import com.api.exception.EntradaInvalidaException;
import com.api.exception.EstoqueInsuficienteException;
import com.api.exception.RegistroNaoLocalizadoException;
import com.movimentacao.domain.dto.ItemMovimentacaoRequest;
import com.movimentacao.domain.dto.MovimentacaoRequest;
import com.movimentacao.domain.entity.ItemMovimentacaoEntity;
import com.movimentacao.domain.entity.MovimentacaoEntity;
import com.movimentacao.domain.enums.TipoMovimentacao;
import com.movimentacao.repository.MovimentacaoRepository;
import com.produto.domain.entity.ProdutoEntity;
import com.produto.service.ProdutoService;
import com.usuario.domain.entity.UsuarioEntity;
import com.usuario.service.UsuarioService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class MovimentacaoService {

    // Só services de outros domínios são injetados aqui (nunca repository de outro
    // agregado). MovimentacaoRepository é o próprio repositório deste serviço, então
    // continua sendo injetado normalmente.
    private final MovimentacaoRepository movimentacaoRepository;
    private final ProdutoService produtoService;
    private final UsuarioService usuarioService;

    public MovimentacaoService(MovimentacaoRepository movimentacaoRepository,
                               ProdutoService produtoService,
                               UsuarioService usuarioService) {
        this.movimentacaoRepository = movimentacaoRepository;
        this.produtoService = produtoService;
        this.usuarioService = usuarioService;
    }

    @Transactional
    public MovimentacaoEntity registrarMovimentacao(MovimentacaoRequest movimentacao) {

        if (movimentacao == null || movimentacao.getItens() == null || movimentacao.getItens().isEmpty()) {
            throw new EntradaInvalidaException("Movimentação deve conter pelo menos um item.");
        }

        Long idUsuarioResponsavel = movimentacao.getIdUsuarioResposavel();
        if (Objects.isNull(idUsuarioResponsavel)) {
            throw new EntradaInvalidaException("Usuário responsável não informado.");
        }

        TipoMovimentacao tipo = movimentacao.getTipo();
        if (Objects.isNull(tipo)) {
            throw new EntradaInvalidaException("Tipo da movimentação (ENTRADA/SAIDA) não informado.");
        }

        UsuarioEntity usuario = usuarioService.getUsuarioById(idUsuarioResponsavel);

        MovimentacaoEntity movimentacaoPersistir = new MovimentacaoEntity();
        movimentacaoPersistir.setUsuarioEntity(usuario);
        movimentacaoPersistir.setTipo(tipo);
        movimentacaoPersistir.setDataHora(LocalDateTime.now());
        movimentacaoPersistir.setObservacao(movimentacao.getObservacao());

        for (ItemMovimentacaoRequest itemRequest : movimentacao.getItens()) {
            Long idProduto = itemRequest.getIdProdutoMovimetado();
            Integer quantidadeSolicitada = itemRequest.getQuantidade();

            if (idProduto == null) {
                throw new EntradaInvalidaException("O id do produto não foi informado.");
            }
            if (quantidadeSolicitada == null || quantidadeSolicitada <= 0) {
                throw new EntradaInvalidaException("Quantidade inválida para o produto informado.");
            }

            ProdutoEntity produtoEstoque = produtoService.buscarEntidadeParaMovimentacao(idProduto);

            Integer novaQuantidade = defineNovaQuantidadeEstoqueAposMovimentacao(
                    tipo, quantidadeSolicitada, produtoEstoque);

            produtoService.atualizarEstoque(produtoEstoque, novaQuantidade);

            ItemMovimentacaoEntity itemEntity = new ItemMovimentacaoEntity();
            itemEntity.setProduto(produtoEstoque);
            itemEntity.setQuantidade(quantidadeSolicitada);

            movimentacaoPersistir.adicionarItem(itemEntity);
        }

        return movimentacaoRepository.save(movimentacaoPersistir);
    }

    private Integer defineNovaQuantidadeEstoqueAposMovimentacao(TipoMovimentacao tipo,
                                                                Integer quantidadeSolicitada,
                                                                ProdutoEntity produtoEstoque) {
        Integer quantidadeAtual = produtoEstoque.getQuantidadeEstoque();

        if (tipo == TipoMovimentacao.ENTRADA) {
            return quantidadeAtual + quantidadeSolicitada;
        }

        if (quantidadeAtual < quantidadeSolicitada) {
            throw new EstoqueInsuficienteException(
                    "Estoque insuficiente para o produto " + produtoEstoque.getNome() +
                            ". Disponível: " + quantidadeAtual + ", solicitado: " + quantidadeSolicitada);
        }

        return quantidadeAtual - quantidadeSolicitada;
    }

    public List<MovimentacaoEntity> listarTodas() {
        return movimentacaoRepository.findAll();
    }

    public MovimentacaoEntity obterPorId(Long id) {
        return movimentacaoRepository.findById(id)
                .orElseThrow(() -> new RegistroNaoLocalizadoException("Movimentação com ID " + id + " não encontrada."));
    }
}