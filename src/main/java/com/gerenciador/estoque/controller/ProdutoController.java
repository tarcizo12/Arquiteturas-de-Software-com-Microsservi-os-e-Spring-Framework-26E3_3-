package com.gerenciador.estoque.controller;

import com.gerenciador.estoque.controller.docs.ProdutoControllerDocs;
import com.gerenciador.estoque.domain.dto.ProdutoRequest;
import com.gerenciador.estoque.domain.entity.Produto;
import com.gerenciador.estoque.service.ProdutoService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/produtos")
public class ProdutoController implements ProdutoControllerDocs {

    private static final Logger log = LoggerFactory.getLogger(ProdutoController.class);
    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @GetMapping
    public ResponseEntity<List<Produto>> listarTodos() {
        return ResponseEntity.ok(produtoService.listarTodosOrdenadosPorNome());
    }

    @GetMapping("/estoque-baixo")
    public ResponseEntity<List<Produto>> listarEstoqueBaixo(@RequestParam(defaultValue = "10") Integer limite) {
        return ResponseEntity.ok(produtoService.listarProdutosComEstoqueBaixo(limite));
    }

    @GetMapping("/categoria/{categoriaId}")
    public ResponseEntity<List<Produto>> listarPorCategoria(@PathVariable Long categoriaId) {
        return ResponseEntity.ok(produtoService.listarPorCategoria(categoriaId));
    }

    @GetMapping("/fornecedor/{fornecedorId}")
    public ResponseEntity<List<Produto>> listarPorFornecedor(@PathVariable Long fornecedorId) {
        return ResponseEntity.ok(produtoService.listarPorFornecedor(fornecedorId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Produto> obterPorId(@PathVariable Long id) {
        return ResponseEntity.ok(produtoService.obterPorId(id));
    }

    @PostMapping
    public ResponseEntity<Produto> incluir(@Valid @RequestBody ProdutoRequest request) {
        Produto novo = produtoService.incluir(request.toProduto());
        log.info("Produto {} incluído com sucesso.", novo.getNome());
        return ResponseEntity.status(HttpStatus.CREATED).body(novo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Produto> alterar(@PathVariable Long id, @RequestBody ProdutoRequest request) {
        Produto atualizado = produtoService.alterar(id, request.toProduto());
        log.info("Produto {} atualizado com sucesso.", atualizado.getNome());
        return ResponseEntity.ok(atualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        produtoService.excluir(id);
        log.info("Registro {} excluído com sucesso.", id);
        return ResponseEntity.noContent().build();
    }
}