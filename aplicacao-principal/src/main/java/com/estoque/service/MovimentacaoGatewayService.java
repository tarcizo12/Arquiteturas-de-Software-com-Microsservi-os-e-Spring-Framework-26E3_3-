package com.estoque.service;

import com.estoque.client.MovimentacaoClient;
import com.estoque.client.dto.MovimentacaoResponse;
import com.estoque.domain.Produto;
import com.estoque.domain.Usuario;
import com.estoque.dto.MovimentacaoRequest;
import com.estoque.repository.ProdutoRepository;
import com.estoque.repository.UsuarioRepository;
import feign.FeignException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class MovimentacaoGatewayService {
    private final MovimentacaoClient client;
    private final ProdutoRepository produtos;
    private final UsuarioRepository usuarios;

    public MovimentacaoGatewayService(MovimentacaoClient client, ProdutoRepository produtos,
                                      UsuarioRepository usuarios) {
        this.client = client;
        this.produtos = produtos;
        this.usuarios = usuarios;
    }

    public MovimentacaoResponse registrar(MovimentacaoRequest request) {
        Usuario usuario = usuarios.findById(request.idUsuarioResponsavel())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado."));

        System.out.println("Usuario localizado:  " + usuario.getNome());

        for (MovimentacaoRequest.Item item : request.itens()) {
            Produto produto = produtos.findById(item.idProduto())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado: " + item.idProduto()));

            if (item.quantidade() == null || item.quantidade() <= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quantidade deve ser maior que zero.");
            }
            if (request.tipo() == com.estoque.client.dto.MovimentacaoRequest.TipoMovimentacao.SAIDA
                    && produto.getQuantidadeEstoque() < item.quantidade()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Estoque insuficiente para o produto " + produto.getNome() + ".");
            }
        }

        var itens = request.itens().stream()
            .map(i -> new com.estoque.client.dto.ItemMovimentacaoRequest(i.idProduto(), i.quantidade()))
            .toList();

        var remoteRequest = new com.estoque.client.dto.MovimentacaoRequest(
            request.tipo(), request.idUsuarioResponsavel(), request.observacao(), itens);

        final MovimentacaoResponse resposta;
        try {
            resposta = client.registrar(remoteRequest);
        } catch (FeignException ex) {
            throw new ResponseStatusException(
                HttpStatus.SERVICE_UNAVAILABLE,
                "Serviço de movimentação indisponível. Tente novamente mais tarde."
            );
        }

        for (MovimentacaoRequest.Item item : request.itens()) {
            Produto produto = produtos.findById(item.idProduto()).orElseThrow();
            int atual = produto.getQuantidadeEstoque();
            int nova = request.tipo() == com.estoque.client.dto.MovimentacaoRequest.TipoMovimentacao.ENTRADA
                ? atual + item.quantidade()
                : atual - item.quantidade();
            produto.setQuantidadeEstoque(nova);
            produtos.save(produto);
        }
        return resposta;
    }

    public List<MovimentacaoResponse> listar() {
        try {
            return client.listar();
        } catch (FeignException ex) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                "Serviço de movimentação indisponível. Tente novamente mais tarde.");
        }
    }

    public MovimentacaoResponse obter(Long id) {
        try {
            return client.obter(id);
        } catch (FeignException.NotFound ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Movimentação não encontrada.");
        } catch (FeignException ex) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                "Serviço de movimentação indisponível. Tente novamente mais tarde.");
        }
    }
}
