package com.gerenciador.estoque.controller;


import com.gerenciador.estoque.controller.docs.MovimentacaoControllerDocs;
import com.gerenciador.estoque.domain.dto.MovimentacaoRequest;
import com.gerenciador.estoque.domain.entity.Movimentacao;
import com.gerenciador.estoque.service.MovimentacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movimentacoes")
public class MovimentacaoController implements MovimentacaoControllerDocs {

    @Autowired
    private MovimentacaoService movimentacaoService;

    @PostMapping
    public ResponseEntity<Movimentacao> registrar(@RequestBody MovimentacaoRequest movimentacao) {
        return ResponseEntity.status(HttpStatus.CREATED).body(movimentacaoService.registrarMovimentacao(movimentacao.toMovimentacao()));
    }

    @GetMapping
    public ResponseEntity<List<Movimentacao>> listarTodas() {
        return ResponseEntity.ok(movimentacaoService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Movimentacao> obterPorId(@PathVariable Long id) {
        return ResponseEntity.ok(movimentacaoService.obterPorId(id));
    }


}