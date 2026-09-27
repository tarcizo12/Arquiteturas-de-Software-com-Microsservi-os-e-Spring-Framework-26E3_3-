package com.gerenciador.estoque.service;

import com.gerenciador.estoque.domain.entity.Produto;
import com.gerenciador.estoque.exception.EntradaInvalidaException;
import com.gerenciador.estoque.exception.RegistroNaoLocalizadoException;
import com.gerenciador.estoque.repository.ProdutoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;

    public ProdutoService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    private static final String MENSAGEM_DEFAULT_ID_NAO_LOCALIZADO = "Produto com ID %d não encontrado.";

    // Incluir novo produto
    public Produto incluir(Produto produto) {
        validarProduto(produto);
        return produtoRepository.save(produto);
    }

    public Produto alterar(Long id, Produto produtoAtualizado) {
        if (!produtoRepository.existsById(id)) {
            throw new RegistroNaoLocalizadoException(MENSAGEM_DEFAULT_ID_NAO_LOCALIZADO.formatted(id));
        }
        validarProduto(produtoAtualizado);
        produtoAtualizado.setId(id);
        return produtoRepository.save(produtoAtualizado);
    }

    public void excluir(Long id) {
        if (!produtoRepository.existsById(id)) {
            throw new RegistroNaoLocalizadoException(MENSAGEM_DEFAULT_ID_NAO_LOCALIZADO.formatted(id));
        }
        produtoRepository.deleteById(id);
    }

    public List<Produto> listarPorCategoria(Long categoriaId) {
        return produtoRepository.findByCategoriaId(categoriaId);
    }

    public List<Produto> listarPorFornecedor(Long fornecedorId) {
        return produtoRepository.findByFornecedorId(fornecedorId);
    }

    public Produto obterPorId(Long id) {
        return produtoRepository.findById(id)
                .orElseThrow(() ->  new RegistroNaoLocalizadoException(MENSAGEM_DEFAULT_ID_NAO_LOCALIZADO.formatted(id)));
    }

    public List<Produto> listarTodosOrdenadosPorNome() {
        return produtoRepository.findAllByOrderByNomeAsc();
    }

    public List<Produto> listarProdutosComEstoqueBaixo(Integer limite) {
        return produtoRepository.findByQuantidadeEstoqueLessThan(limite);
    }

    private void validarProduto(Produto produto) {
        if (produto == null) {
            throw new EntradaInvalidaException("Produto não pode ser nulo.");
        }
        if (produto.getCategoria() == null) {
            throw new EntradaInvalidaException("Não é possível incluir produto sem informar CATEGORIA.");
        }
        if (produto.getFornecedor() == null) {
            throw new EntradaInvalidaException("Não é possível incluir produto sem informar FORNECEDOR.");
        }
    }

}