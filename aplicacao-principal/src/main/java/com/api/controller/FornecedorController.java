package com.api.controller;

import com.fornecedor.domain.dto.FornecedorResponse;
import com.fornecedor.service.FornecedorService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/com/api/fornecedores")
@RequiredArgsConstructor
public class FornecedorController {

    private final FornecedorService fornecedorService;

    @Operation(summary = "Realiza a importacao em lote de fornecedores")
    @PutMapping(
            value = "/importacao",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<?> importar(
            @RequestPart("arquivo") MultipartFile arquivo) {
        String retornoUsuario = "Csv importado, em alguns instantes a base de dados deve se atualizada, arquivo temporario: " +
                fornecedorService.salvarArquivoTemporariamente(arquivo);
        return ResponseEntity.accepted()
                .body(retornoUsuario);
    }

    @GetMapping("/")
    public ResponseEntity<List<FornecedorResponse>> listarTodos() {
        return ResponseEntity.ok(fornecedorService.listarTodos());
    }
}