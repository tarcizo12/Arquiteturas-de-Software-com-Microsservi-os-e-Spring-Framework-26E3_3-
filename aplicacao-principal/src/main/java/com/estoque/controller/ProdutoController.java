package com.estoque.controller;

import com.estoque.domain.Produto;
import com.estoque.repository.ProdutoRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/produtos")
public class ProdutoController {
    private final ProdutoRepository repository;
    public ProdutoController(ProdutoRepository repository) { this.repository = repository; }

    @GetMapping
    public List<Produto> listar() { return repository.findAll(); }

    @GetMapping("/{id}")
    public Produto obter(@PathVariable Long id) {
        return repository.findById(id).orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
            org.springframework.http.HttpStatus.NOT_FOUND, "Produto não encontrado."));
    }
}
