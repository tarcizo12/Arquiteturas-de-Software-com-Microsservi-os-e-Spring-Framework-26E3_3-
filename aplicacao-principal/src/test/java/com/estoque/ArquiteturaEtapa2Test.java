package com.estoque;

import org.junit.jupiter.api.Test;
import org.springframework.cloud.openfeign.FeignClient;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ArquiteturaEtapa2Test {
    @Test
    void projetoDeveUsarOpenFeignParaComunicacaoExterna() {
        assertTrue(com.estoque.client.MovimentacaoClient.class.isAnnotationPresent(FeignClient.class));
    }
}
