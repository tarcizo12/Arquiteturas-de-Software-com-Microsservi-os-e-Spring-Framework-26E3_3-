package com.estoque.controller;

import com.estoque.client.dto.MovimentacaoResponse;
import com.estoque.dto.MovimentacaoRequest;
import com.estoque.service.MovimentacaoGatewayService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movimentacoes")
public class MovimentacaoController {
    private final MovimentacaoGatewayService service;

    public MovimentacaoController(MovimentacaoGatewayService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<MovimentacaoResponse> registrar(@Valid @RequestBody MovimentacaoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registrar(request));
    }

    @GetMapping
    public List<MovimentacaoResponse> listar() { return service.listar(); }

    @GetMapping("/{id}")
    public MovimentacaoResponse obter(@PathVariable Long id) { return service.obter(id); }
}
