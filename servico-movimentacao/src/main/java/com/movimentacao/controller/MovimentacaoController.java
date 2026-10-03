package com.movimentacao.controller;

import com.movimentacao.dto.MovimentacaoRequest;
import com.movimentacao.dto.MovimentacaoResponse;
import com.movimentacao.service.MovimentacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movimentacoes")
public class MovimentacaoController {
    private final MovimentacaoService service;

    public MovimentacaoController(MovimentacaoService service) {
        this.service = service;
    }

    @Operation(summary="Registra uma movimentação de estoque")
    @ApiResponses({
        @ApiResponse(responseCode="201", description="Movimentação registrada"),
        @ApiResponse(responseCode="400", description="Dados inválidos")
    })
    @PostMapping
    public ResponseEntity<MovimentacaoResponse> registrar(@Valid @RequestBody MovimentacaoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registrar(request));
    }

    @Operation(summary="Lista o histórico de movimentações")
    @ApiResponse(responseCode="200", description="Lista retornada")
    @GetMapping
    public List<MovimentacaoResponse> listar() {
        return service.listar();
    }

    @Operation(summary="Consulta uma movimentação pelo ID")
    @ApiResponses({
        @ApiResponse(responseCode="200", description="Movimentação encontrada"),
        @ApiResponse(responseCode="404", description="Movimentação não encontrada")
    })
    @GetMapping("/{id}")
    public MovimentacaoResponse obter(@PathVariable Long id) {
        return service.obter(id);
    }

    @ExceptionHandler(java.util.NoSuchElementException.class)
    public ResponseEntity<String> notFound(Exception ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Movimentação não encontrada.");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> badRequest(Exception ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}
