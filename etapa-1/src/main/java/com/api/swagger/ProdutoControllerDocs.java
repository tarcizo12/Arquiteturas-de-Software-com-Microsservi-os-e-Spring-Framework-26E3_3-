package com.api.swagger;

import com.produto.domain.dto.ProdutoRequest;
import com.produto.domain.dto.ProdutoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Produtos", description = "Gerenciamento de produtos do estoque")
public interface ProdutoControllerDocs {

    @Operation(summary = "Listar todos os produtos (ordenados por nome)")
    @GetMapping
    ResponseEntity<List<ProdutoResponse>> listarTodos();

    @Operation(summary = "Listar produtos com estoque abaixo do limite")
    @GetMapping("/estoque-baixo")
    ResponseEntity<List<ProdutoResponse>> listarEstoqueBaixo(
            @Parameter(description = "Quantidade mínima para considerar estoque baixo", example = "5")
            @RequestParam(defaultValue = "10") Integer limite);

    @Operation(summary = "Listar produtos por categoria")
    @GetMapping("/categoria/{categoriaId}")
    ResponseEntity<List<ProdutoResponse>> listarPorCategoria(@PathVariable Long categoriaId);

    @Operation(summary = "Listar produtos por fornecedor")
    @GetMapping("/fornecedor/{fornecedorId}")
    ResponseEntity<List<ProdutoResponse>> listarPorFornecedor(@PathVariable Long fornecedorId);

    @Operation(summary = "Buscar produto por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "ProdutoEntity encontrado"),
            @ApiResponse(responseCode = "404", description = "ProdutoEntity não encontrado")
    })
    @GetMapping("/{id}")
    ResponseEntity<ProdutoResponse> obterPorId(@PathVariable Long id);

    @Operation(summary = "Cadastrar novo produto")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "ProdutoEntity criado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @PostMapping
    ResponseEntity<ProdutoResponse> incluir(@RequestBody ProdutoRequest request);

    @Operation(summary = "Atualizar produto")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "ProdutoEntity atualizado"),
            @ApiResponse(responseCode = "404", description = "ProdutoEntity não encontrado")
    })
    @PutMapping("/{id}")
    ResponseEntity<ProdutoResponse> alterar(@PathVariable Long id, @RequestBody ProdutoRequest request);

    @Operation(summary = "Excluir produto")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "ProdutoEntity excluído"),
            @ApiResponse(responseCode = "404", description = "ProdutoEntity não encontrado")
    })
    @DeleteMapping("/{id}")
    ResponseEntity<Void> excluir(@PathVariable Long id);
}