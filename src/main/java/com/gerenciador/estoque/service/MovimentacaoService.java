package com.gerenciador.estoque.service;

import com.gerenciador.estoque.domain.entity.ItemMovimentacao;
import com.gerenciador.estoque.domain.entity.Movimentacao;
import com.gerenciador.estoque.domain.entity.Produto;
import com.gerenciador.estoque.domain.entity.Usuario;
import com.gerenciador.estoque.domain.enums.TipoMovimentacao;
import com.gerenciador.estoque.exception.EntradaInvalidaException;
import com.gerenciador.estoque.exception.EstoqueInsuficienteException;
import com.gerenciador.estoque.exception.RegistroNaoLocalizadoException;
import com.gerenciador.estoque.repository.MovimentacaoRepository;
import com.gerenciador.estoque.repository.ProdutoRepository;
import com.gerenciador.estoque.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MovimentacaoService {

    private final MovimentacaoRepository movimentacaoRepository;
    private final ProdutoRepository produtoRepository;
    private final UsuarioRepository usuarioRepository;

    public MovimentacaoService(MovimentacaoRepository movimentacaoRepository,
                               ProdutoRepository produtoRepository,
                               UsuarioRepository usuarioRepository) {
        this.movimentacaoRepository = movimentacaoRepository;
        this.produtoRepository = produtoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public Movimentacao registrarMovimentacao(Movimentacao movimentacao) {
        if (movimentacao == null || movimentacao.getItens() == null || movimentacao.getItens().isEmpty()) {
            throw new EntradaInvalidaException("Movimentação deve conter pelo menos um item.");
        }
        if (movimentacao.getUsuario() == null) {
            throw new EntradaInvalidaException("Usuário responsável não informado.");
        }


        Usuario usuario = usuarioRepository.findById(movimentacao.getUsuario().getId())
                .orElseThrow(() -> new RegistroNaoLocalizadoException("Usuário com ID " + movimentacao.getUsuario().getId() + " não encontrado."));
        movimentacao.setUsuario(usuario);

        for (ItemMovimentacao item : movimentacao.getItens()) {
            Produto produto = item.getProduto();
            if (produto == null || produto.getId() == null) {
                throw new EntradaInvalidaException("Produto do item não identificado.");
            }

            Produto produtoEstoque = produtoRepository.findById(produto.getId())
                    .orElseThrow(() -> new RegistroNaoLocalizadoException("Produto com ID " + produto.getId() + " não encontrado."));

            if (!produtoEstoque.isValido()) {
                throw new EntradaInvalidaException("Produto " + produtoEstoque.getNome() + " está vencido e não pode ser movimentado.");
            }

            Integer novaQuantidade = defineNovaQuantidadeEstoqueAposMovimentacao(movimentacao, item, produtoEstoque);
            produtoEstoque.setQuantidadeEstoque(novaQuantidade);
            produtoRepository.save(produtoEstoque);
            item.setProduto(produtoEstoque);
        }

        return movimentacaoRepository.save(movimentacao);
    }

    private Integer defineNovaQuantidadeEstoqueAposMovimentacao(Movimentacao movimentacao, ItemMovimentacao item, Produto produtoEstoque) {
        Integer novaQuantidade = produtoEstoque.getQuantidadeEstoque();
        if (movimentacao.getTipo() == TipoMovimentacao.ENTRADA) {
            novaQuantidade += item.getQuantidade();
        } else {
            if (novaQuantidade < item.getQuantidade()) {
                throw new EstoqueInsuficienteException(
                        "Estoque insuficiente para o produto " + produtoEstoque.getNome() +
                                ". Disponível: " + novaQuantidade + ", solicitado: " + item.getQuantidade()
                );
            }
            novaQuantidade -= item.getQuantidade();
        }
        return novaQuantidade;
    }

    public List<Movimentacao> listarTodas() {
        return movimentacaoRepository.findAll();
    }

    public Movimentacao obterPorId(Long id) {
        return movimentacaoRepository.findById(id)
                .orElseThrow(() -> new RegistroNaoLocalizadoException("Movimentação com ID " + id + " não encontrada."));
    }
}