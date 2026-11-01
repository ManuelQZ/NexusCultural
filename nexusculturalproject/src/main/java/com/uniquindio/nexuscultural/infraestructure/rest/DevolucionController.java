package com.uniquindio.nexuscultural.infraestructure.rest;

import com.uniquindio.nexuscultural.application.dto.request.DevolverProductoRequest;
import com.uniquindio.nexuscultural.application.dto.response.DevolucionResponse;
import com.uniquindio.nexuscultural.application.usecase.DevolverProductoUseCase;
import com.uniquindio.nexuscultural.application.usecase.ObtenerDevolucionPorPedidoUseCase;
import com.uniquindio.nexuscultural.domain.entity.Devolucion;
import com.uniquindio.nexuscultural.infraestructure.rest.mapper.DevolucionMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/devoluciones")
public class DevolucionController {
    private final DevolverProductoUseCase devolverProductoUseCase;
    private final ObtenerDevolucionPorPedidoUseCase obtenerDevolucionUseCase;
    private final DevolucionMapper devolucionMapper;

    public DevolucionController(DevolverProductoUseCase devolverProductoUseCase,
                                ObtenerDevolucionPorPedidoUseCase obtenerDevolucionUseCase,
                                DevolucionMapper devolucionMapper) {
        this.devolverProductoUseCase = devolverProductoUseCase;
        this.obtenerDevolucionUseCase = obtenerDevolucionUseCase;
        this.devolucionMapper = devolucionMapper;
    }
    @PostMapping
    public ResponseEntity<DevolucionResponse> solicitarReembolso(@Valid @RequestBody DevolverProductoRequest request) {
        devolverProductoUseCase.ejecutar(request);

        Devolucion devolucion = obtenerDevolucionUseCase.ejecutar(request.idPedido());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(devolucionMapper.toResponse(devolucion));
    }
}
