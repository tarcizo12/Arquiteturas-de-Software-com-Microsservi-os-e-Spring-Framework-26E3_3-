package com.api.swagger;


import com.movimentacao.domain.dto.MovimentacaoRequest;
import com.movimentacao.domain.entity.MovimentacaoEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

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
    ResponseEntity<MovimentacaoEntity> registrar(@RequestBody MovimentacaoRequest MovimentacaoEtity);

    @Operation(summary = "Listar todas as movimentações",
            description = "Retorna a lista de todas as movimentações registradas")
    @ApiResponse(responseCode = "200", description = "Lista de movimentações retornada com sucesso")
    @GetMapping
    ResponseEntity<List<MovimentacaoEntity>> listarTodas();

    @Operation(summary = "Buscar movimentação por ID",
            description = "Retorna uma movimentação específica pelo seu identificador")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Movimentação encontrada"),
            @ApiResponse(responseCode = "404", description = "Movimentação não encontrada")
    })
    @GetMapping("/{id}")
    ResponseEntity<MovimentacaoEntity> obterPorId(@PathVariable Long id);
}