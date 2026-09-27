package com.gerenciador.estoque.controller.docs;

import com.gerenciador.estoque.domain.dto.ProdutoRequest;
import com.gerenciador.estoque.domain.entity.Produto;
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
    ResponseEntity<List<Produto>> listarTodos();

    @Operation(summary = "Listar produtos com estoque abaixo do limite")
    @GetMapping("/estoque-baixo")
    ResponseEntity<List<Produto>> listarEstoqueBaixo(
            @Parameter(description = "Quantidade mínima para considerar estoque baixo", example = "5")
            @RequestParam(defaultValue = "10") Integer limite);

    @Operation(summary = "Listar produtos por categoria")
    @GetMapping("/categoria/{categoriaId}")
    ResponseEntity<List<Produto>> listarPorCategoria(@PathVariable Long categoriaId);

    @Operation(summary = "Listar produtos por fornecedor")
    @GetMapping("/fornecedor/{fornecedorId}")
    ResponseEntity<List<Produto>> listarPorFornecedor(@PathVariable Long fornecedorId);

    @Operation(summary = "Buscar produto por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Produto encontrado"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    @GetMapping("/{id}")
    ResponseEntity<Produto> obterPorId(@PathVariable Long id);

    @Operation(summary = "Cadastrar novo produto")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Produto criado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @PostMapping
    ResponseEntity<Produto> incluir(@RequestBody ProdutoRequest request);

    @Operation(summary = "Atualizar produto")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Produto atualizado"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    @PutMapping("/{id}")
    ResponseEntity<Produto> alterar(@PathVariable Long id, @RequestBody ProdutoRequest request);

    @Operation(summary = "Excluir produto")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Produto excluído"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    @DeleteMapping("/{id}")
    ResponseEntity<Void> excluir(@PathVariable Long id);
}