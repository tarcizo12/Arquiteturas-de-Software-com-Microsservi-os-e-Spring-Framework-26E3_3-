package com.produto.client;

import com.produto.client.dto.MovimentacaoRequest;
import com.produto.client.dto.MovimentacaoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(
        name = "movimentacao-service",
        url = "${servico.movimentacao.url}"
)
public interface MovimentacaoClient {

    @PostMapping("/api/movimentacoes")
    MovimentacaoResponse registrar(
            @RequestBody MovimentacaoRequest request
    );

    @GetMapping("/api/movimentacoes")
    List<MovimentacaoResponse> listar();

    @GetMapping("/api/movimentacoes/{id}")
    MovimentacaoResponse obter(
            @PathVariable("id") Long id
    );
}
