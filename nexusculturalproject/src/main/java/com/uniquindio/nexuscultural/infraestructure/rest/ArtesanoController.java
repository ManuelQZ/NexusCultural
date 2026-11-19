package com.uniquindio.nexuscultural.infraestructure.rest;

import com.uniquindio.nexuscultural.application.dto.request.RegistrarArtesanoRequest;
import com.uniquindio.nexuscultural.application.dto.response.ArtesanoResponse;
import com.uniquindio.nexuscultural.application.usecase.RegistrarArtesanoUseCase;
import com.uniquindio.nexuscultural.domain.entity.Artesano;
import com.uniquindio.nexuscultural.domain.valueobject.TrayectoriaCultural;
import com.uniquindio.nexuscultural.domain.valueobject.UbicacionGeografica;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/artesanos")
@RequiredArgsConstructor
public class ArtesanoController {

    private final RegistrarArtesanoUseCase registrarArtesanoUseCase;

    @PostMapping
    public ResponseEntity<ArtesanoResponse> registrar(@Valid @RequestBody RegistrarArtesanoRequest request) {
        TrayectoriaCultural trayectoria = new TrayectoriaCultural(request.anosExperiencia(), request.reconocimientos());
        UbicacionGeografica ubicacion = new UbicacionGeografica(request.departamento(), request.municipio(), request.direccion());

        Artesano guardado = registrarArtesanoUseCase.ejecutar(request.nombreCompleto(), request.email(), trayectoria, ubicacion);

        ArtesanoResponse response = new ArtesanoResponse(
                guardado.getId(),
                guardado.getNombreCompleto(),
                guardado.getEmail().valor(),
                guardado.getFechaRegistro(),
                guardado.isActivo(),
                guardado.getTrayectoriaCultural(),
                guardado.getUbicacionGeografica(),
                List.of()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
