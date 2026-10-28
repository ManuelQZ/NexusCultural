package com.uniquindio.nexuscultural.infraestructure.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/devoluciones")
public class DevolucionController {

    // Ruta restringida exclusivamente para el rol COMPRADOR (solicitar reembolsos / devoluciones)
    @PostMapping
    public ResponseEntity<Map<String, String>> solicitarReembolso() {
        return ResponseEntity.ok(Map.of("mensaje", "Solicitud de reembolso registrada exitosamente"));
    }
}
