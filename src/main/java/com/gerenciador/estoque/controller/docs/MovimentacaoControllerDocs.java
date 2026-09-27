package com.gerenciador.estoque.controller.docs;


import com.gerenciador.estoque.domain.dto.MovimentacaoRequest;
import com.gerenciador.estoque.domain.entity.Movimentacao;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Movimentações", description = "Registro de entradas e saídas de produtos")
public interface MovimentacaoControllerDocs {

    @Operation(summary = "Registrar movimentação",
            description = "Cria uma movimentação de entrada ou saída, atualizando o estoque dos produtos envolvidos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Movimentação registrada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos (ex: itens vazios, estoque insuficiente, produto vencido)")
    })
    @PostMapping
    ResponseEntity<Movimentacao> registrar(@RequestBody MovimentacaoRequest movimentacao);

    @Operation(summary = "Listar todas as movimentações",
            description = "Retorna a lista de todas as movimentações registradas")
    @ApiResponse(responseCode = "200", description = "Lista de movimentações retornada com sucesso")
    @GetMapping
    ResponseEntity<List<Movimentacao>> listarTodas();

    @Operation(summary = "Buscar movimentação por ID",
            description = "Retorna uma movimentação específica pelo seu identificador")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Movimentação encontrada"),
            @ApiResponse(responseCode = "404", description = "Movimentação não encontrada")
    })
    @GetMapping("/{id}")
    ResponseEntity<Movimentacao> obterPorId(@PathVariable Long id);
}