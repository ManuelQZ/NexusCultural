package com.uniquindio.nexuscultural.infraestructure.rest;

import com.uniquindio.nexuscultural.application.dto.request.RegistrarCompradorRequest;
import com.uniquindio.nexuscultural.application.usecase.RegistrarCompradorUseCase;
import com.uniquindio.nexuscultural.domain.entity.Comprador;
import com.uniquindio.nexuscultural.domain.valueobject.UbicacionGeografica;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/compradores")
@RequiredArgsConstructor
public class CompradorController {

    private final RegistrarCompradorUseCase registrarCompradorUseCase;

    @PostMapping
    public ResponseEntity<Map<String, Object>> registrar(@Valid @RequestBody RegistrarCompradorRequest request) {
        UbicacionGeografica ubicacion = new UbicacionGeografica(request.departamento(), request.municipio(), request.direccion());
        Comprador comprador = registrarCompradorUseCase.ejecutar(request.nombreCompleto(), request.email(), ubicacion);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "id", comprador.getId(),
                "nombreCompleto", comprador.getNombreCompleto(),
                "email", comprador.getEmail().valor(),
                "rol", comprador.getRol().name()
        ));
    }
}
