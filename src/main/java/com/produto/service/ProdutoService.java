package com.produto.service;

import com.api.exception.EntradaInvalidaException;
import com.api.exception.RegistroNaoLocalizadoException;
import com.categoria.domain.entity.CategoriaEntity;
import com.categoria.service.CategoriaService;
import com.fornecedor.domain.entity.FornecedorEntity;
import com.fornecedor.service.FornecedorService;
import com.produto.domain.dto.ProdutoRequest;
import com.produto.domain.dto.ProdutoResponse;
import com.produto.domain.entity.ProdutoEntity;
import com.produto.domain.entity.ProdutoNaoPerecivel;
import com.produto.domain.entity.ProdutoPerecivel;
import com.produto.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProdutoService {

    private static final String MENSAGEM_DEFAULT_ID_NAO_LOCALIZADO = "ProdutoEntity com ID %d não encontrado.";

    private final ProdutoRepository produtoRepository;
    private final CategoriaService categoriaService;
    private final FornecedorService fornecedorService;

    @Autowired
    public ProdutoService(
            ProdutoRepository produtoRepository,
            CategoriaService categoriaService,
            FornecedorService fornecedorService
    ) {
        this.produtoRepository = produtoRepository;
        this.categoriaService = categoriaService;
        this.fornecedorService = fornecedorService;
    }

    // ---------- Incluir ----------

    public ProdutoResponse incluir(ProdutoRequest request) {

        validarRegrasPerecivel(request);

        CategoriaEntity categoria = categoriaService.getCategoriaById(request.getIdCategoria());
        FornecedorEntity fornecedor = fornecedorService.getFornecedorById(request.getIdFornecedor());

        ProdutoEntity produto = montarEntidade(request, categoria, fornecedor);

        if (!produto.isValido()) {
            throw new IllegalArgumentException("Dados do produto inválidos para o tipo informado");
        }

        ProdutoEntity salvo = produtoRepository.save(produto);

        return ProdutoResponse.fromEntity(salvo);
    }

    private ProdutoEntity montarEntidade(ProdutoRequest request,
                                         CategoriaEntity categoria,
                                         FornecedorEntity fornecedor) {

        if (Boolean.TRUE.equals(request.getPerecivel())) {
            return new ProdutoPerecivel(
                    request.getNome(),
                    request.getDescricao(),
                    request.getPreco(),
                    request.getQuantidadeEstoque(),
                    categoria,
                    fornecedor,
                    request.getDataValidade(),
                    request.getLote()
            );
        }

        return new ProdutoNaoPerecivel(
                request.getNome(),
                request.getDescricao(),
                request.getPreco(),
                request.getQuantidadeEstoque(),
                categoria,
                fornecedor,
                request.getGarantiaMeses()
        );
    }

    // ---------- Alterar ----------

    public ProdutoResponse alterar(Long id, ProdutoRequest request) {

        ProdutoEntity produtoExistente = produtoRepository.findById(id)
                .orElseThrow(() -> new RegistroNaoLocalizadoException(
                        MENSAGEM_DEFAULT_ID_NAO_LOCALIZADO.formatted(id)));

        validarRegrasPerecivel(request);
        validarTipoNaoAlterado(produtoExistente, request);

        CategoriaEntity categoria = categoriaService.getCategoriaById(request.getIdCategoria());
        FornecedorEntity fornecedor = fornecedorService.getFornecedorById(request.getIdFornecedor());

        atualizarCamposComuns(produtoExistente, request, categoria, fornecedor);
        atualizarCamposEspecificos(produtoExistente, request);

        if (!produtoExistente.isValido()) {
            throw new IllegalArgumentException("Dados do produto inválidos para o tipo informado");
        }

        ProdutoEntity salvo = produtoRepository.save(produtoExistente);

        return ProdutoResponse.fromEntity(salvo);
    }

    // O tipo (perecível / não perecível) é definido pelo discriminator column e não
    // pode ser trocado em uma entidade já persistida via SINGLE_TABLE inheritance.
    // Se o negócio precisar permitir essa troca, o caminho correto é excluir e recriar
    // o registro, não fazer update in-place.
    private void validarTipoNaoAlterado(ProdutoEntity existente, ProdutoRequest request) {
        boolean perecivelSolicitado = Boolean.TRUE.equals(request.getPerecivel());
        boolean eraPerecivel = existente instanceof ProdutoPerecivel;

        if (perecivelSolicitado != eraPerecivel) {
            throw new IllegalArgumentException(
                    "Não é possível alterar o tipo (perecível/não perecível) de um produto existente");
        }
    }

    private void atualizarCamposComuns(ProdutoEntity produto,
                                       ProdutoRequest request,
                                       CategoriaEntity categoria,
                                       FornecedorEntity fornecedor) {
        produto.setNome(request.getNome());
        produto.setDescricao(request.getDescricao());
        produto.setPreco(request.getPreco());
        produto.setQuantidadeEstoque(request.getQuantidadeEstoque());
        produto.setCategoria(categoria);
        produto.setFornecedor(fornecedor);
    }

    private void atualizarCamposEspecificos(ProdutoEntity produto, ProdutoRequest request) {
        if (produto instanceof ProdutoPerecivel perecivel) {
            perecivel.setDataValidade(request.getDataValidade());
            perecivel.setLote(request.getLote());
        } else if (produto instanceof ProdutoNaoPerecivel naoPerecivel) {
            naoPerecivel.setGarantiaMeses(request.getGarantiaMeses());
        }
    }

    // ---------- Validação compartilhada ----------

    private void validarRegrasPerecivel(ProdutoRequest request) {
        boolean perecivel = Boolean.TRUE.equals(request.getPerecivel());

        if (perecivel) {
            if (request.getDataValidade() == null || request.getLote() == null || request.getLote().isBlank()) {
                throw new IllegalArgumentException(
                        "Data de validade e lote são obrigatórios para produtos perecíveis");
            }
        } else {
            if (request.getGarantiaMeses() == null) {
                throw new IllegalArgumentException(
                        "Garantia em meses é obrigatória para produtos não perecíveis");
            }
        }
    }

    // ---------- Exclusão / consultas ----------

    public void excluir(Long id) {
        if (!produtoRepository.existsById(id)) {
            throw new RegistroNaoLocalizadoException(MENSAGEM_DEFAULT_ID_NAO_LOCALIZADO.formatted(id));
        }
        produtoRepository.deleteById(id);
    }

    // ---------- Suporte a movimentação de estoque (usado por MovimentacaoService) ----------
    // Expostos aqui (em vez de expor o repository) para que outras camadas de service
    // (ex.: MovimentacaoService) nunca dependam de ProdutoRepository diretamente.

    public ProdutoEntity buscarEntidadeParaMovimentacao(Long idProduto) {
        ProdutoEntity produto = produtoRepository.findById(idProduto)
                .orElseThrow(() -> new RegistroNaoLocalizadoException(
                        MENSAGEM_DEFAULT_ID_NAO_LOCALIZADO.formatted(idProduto)));

        if (!produto.isValido()) {
            throw new EntradaInvalidaException(
                    "Produto " + produto.getNome() + " está com dados inválidos/vencido e não pode ser movimentado.");
        }

        return produto;
    }

    public void atualizarEstoque(ProdutoEntity produto, Integer novaQuantidade) {
        produto.setQuantidadeEstoque(novaQuantidade);
        produtoRepository.save(produto);
    }

    public List<ProdutoResponse> listarPorCategoria(Long categoriaId) {
        return produtoRepository.findByCategoriaId(categoriaId).stream()
                .map(ProdutoResponse::fromEntity)
                .toList();
    }

    public List<ProdutoResponse> listarPorFornecedor(Long fornecedorId) {
        return produtoRepository.findByFornecedorId(fornecedorId).stream()
                .map(ProdutoResponse::fromEntity)
                .toList();
    }

    public ProdutoResponse obterPorId(Long id) {
        return produtoRepository.findById(id)
                .map(ProdutoResponse::fromEntity)
                .orElseThrow(() -> new RegistroNaoLocalizadoException(
                        MENSAGEM_DEFAULT_ID_NAO_LOCALIZADO.formatted(id)));
    }

    public List<ProdutoResponse> listarTodosOrdenadosPorNome() {
        return produtoRepository.findAllByOrderByNomeAsc().stream()
                .map(ProdutoResponse::fromEntity)
                .toList();
    }

    public List<ProdutoResponse> listarProdutosComEstoqueBaixo(Integer limite) {
        return produtoRepository.findByQuantidadeEstoqueLessThan(limite).stream()
                .map(ProdutoResponse::fromEntity)
                .toList();
    }
}