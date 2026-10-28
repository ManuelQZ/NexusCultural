package com.uniquindio.nexuscultural.infraestructure.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/articulos")
public class ArticuloController {

    // Ruta restringida exclusivamente para el rol ARTESANO (vendedor)
    @PostMapping
    public ResponseEntity<Map<String, String>> publicarArticulo() {
        return ResponseEntity.ok(Map.of("mensaje", "Artículo publicado exitosamente"));
    }
}
