package com.categoria.controller.docs;

import com.categoria.service.CategoriaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

    private static final Logger log = LoggerFactory.getLogger(CategoriaController.class);
    private final CategoriaService service;

    public CategoriaController(CategoriaService cateogriaService) {
        this.service = cateogriaService;
    }

    @GetMapping
    public ResponseEntity<String> listarTodos() {
        return ResponseEntity.ok("TODO");
    }


}