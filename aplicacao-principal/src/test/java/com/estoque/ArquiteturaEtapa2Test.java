package com.estoque;

import com.produto.client.MovimentacaoClient;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.openfeign.FeignClient;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ArquiteturaEtapa2Test {
    @Test
    void projetoDeveUsarOpenFeignParaComunicacaoExterna() {
        assertTrue(MovimentacaoClient.class.isAnnotationPresent(FeignClient.class));
    }
}
