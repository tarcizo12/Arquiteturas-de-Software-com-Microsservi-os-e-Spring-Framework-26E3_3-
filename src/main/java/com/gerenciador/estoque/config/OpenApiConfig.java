package com.gerenciador.estoque.config;

import io.swagger.v3.oas.models.media.Schema;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class OpenApiConfig {

    //Professor, nesta classe tambem foi utilizado a IA para me apoiar em como ocultar os objetos de entity na exibição do swagger
    @Bean
    public OpenApiCustomizer removeEntitySchemas() {
        return openApi -> {
            if (openApi.getComponents() != null && openApi.getComponents().getSchemas() != null) {
                Map<String, Schema> schemas = openApi.getComponents().getSchemas();
                schemas.remove("Categoria");
                schemas.remove("Fornecedor");
                schemas.remove("Produto");
                schemas.remove("ItemMovimentacao");
                schemas.remove("Movimentacao");
                schemas.remove("Usuario");
            }
        };
    }
}