package com.api.controller;


import com.api.swagger.MovimentacaoControllerDocs;
import com.movimentacao.domain.dto.MovimentacaoRequest;
import com.movimentacao.domain.entity.MovimentacaoEntity;
import com.movimentacao.service.MovimentacaoService;
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
    public ResponseEntity<MovimentacaoEntity> registrar(@RequestBody MovimentacaoRequest movimentacao) {
        return ResponseEntity.status(HttpStatus.CREATED).body(movimentacaoService.registrarMovimentacao(movimentacao));
    }

    @GetMapping
    public ResponseEntity<List<MovimentacaoEntity>> listarTodas() {
        return ResponseEntity.ok(movimentacaoService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovimentacaoEntity> obterPorId(@PathVariable Long id) {
        return ResponseEntity.ok(movimentacaoService.obterPorId(id));
    }


}