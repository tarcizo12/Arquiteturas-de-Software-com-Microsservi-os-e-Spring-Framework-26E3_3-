package com.api.controller;

import com.api.swagger.ProdutoControllerDocs;
import com.produto.client.dto.MovimentacaoResponse;
import com.produto.domain.dto.MovimentacaoRequest;
import com.produto.service.MovimentacaoGatewayService;
import com.produto.domain.dto.ProdutoRequest;
import com.produto.domain.dto.ProdutoResponse;
import com.produto.service.ProdutoService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/com/api/produtos")
public class ProdutoController implements ProdutoControllerDocs {

    private static final Logger log = LoggerFactory.getLogger(ProdutoController.class);
    private final MovimentacaoGatewayService movimentacaoGatewayService;
    private final ProdutoService produtoService;


    public ProdutoController(ProdutoService produtoService, MovimentacaoGatewayService movimentacaoGatewayService) {
        this.produtoService = produtoService;
        this.movimentacaoGatewayService = movimentacaoGatewayService;
    }


    //Endpoints das movimentacoes dos produtos
    @PostMapping("/movimentacoes")
    public ResponseEntity<MovimentacaoResponse> registrar(@Valid @RequestBody MovimentacaoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(movimentacaoGatewayService.registrar(request));
    }

    @GetMapping("/movimentacoes")
    public List<MovimentacaoResponse> listar() { return movimentacaoGatewayService.listar(); }

    @GetMapping("/movimentacoes/{id}")
    public MovimentacaoResponse obter(@PathVariable Long id) { return movimentacaoGatewayService.obter(id); }


    //Endpoint dos produtos
    @GetMapping
    public ResponseEntity<List<ProdutoResponse>> listarTodos() {
        return ResponseEntity.ok(produtoService.listarTodosOrdenadosPorNome());
    }

    @GetMapping("/estoque-baixo")
    public ResponseEntity<List<ProdutoResponse>> listarEstoqueBaixo(@RequestParam(defaultValue = "10") Integer limite) {
        return ResponseEntity.ok(produtoService.listarProdutosComEstoqueBaixo(limite));
    }

    @GetMapping("/com/categoria/{categoriaId}")
    public ResponseEntity<List<ProdutoResponse>> listarPorCategoria(@PathVariable Long categoriaId) {
        return ResponseEntity.ok(produtoService.listarPorCategoria(categoriaId));
    }

    @GetMapping("/com/fornecedor/{fornecedorId}")
    public ResponseEntity<List<ProdutoResponse>> listarPorFornecedor(@PathVariable Long fornecedorId) {
        return ResponseEntity.ok(produtoService.listarPorFornecedor(fornecedorId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProdutoResponse> obterPorId(@PathVariable Long id) {
        return ResponseEntity.ok(produtoService.obterPorId(id));
    }

    @PostMapping
    public ResponseEntity<ProdutoResponse> incluir(@Valid @RequestBody ProdutoRequest request) {
        ProdutoResponse novo = produtoService.incluir(request);
        log.info("ProdutoEntity {} incluído com sucesso (id={}).", novo.getNome(), novo.getIdProduto());
        return ResponseEntity.status(HttpStatus.CREATED).body(novo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProdutoResponse> alterar(@PathVariable Long id, @Valid @RequestBody ProdutoRequest request) {
        ProdutoResponse atualizado = produtoService.alterar(id, request);
        log.info("ProdutoEntity {} atualizado com sucesso (id={}).", atualizado.getNome(), atualizado.getIdProduto());
        return ResponseEntity.ok(atualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        produtoService.excluir(id);
        log.info("Registro {} excluído com sucesso.", id);
        return ResponseEntity.noContent().build();
    }
}