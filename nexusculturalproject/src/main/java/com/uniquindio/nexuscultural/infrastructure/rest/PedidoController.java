package com.uniquindio.nexuscultural.infrastructure.rest;

import com.uniquindio.nexuscultural.application.dto.request.CancelarPedidoRequest;
import com.uniquindio.nexuscultural.application.dto.request.CrearPedidoRequest;
import com.uniquindio.nexuscultural.application.dto.response.PedidoDetalleResponse;
import com.uniquindio.nexuscultural.application.usecase.CancelarPedidoPendienteUseCase;
import com.uniquindio.nexuscultural.application.usecase.CrearPedidoUseCase;
import com.uniquindio.nexuscultural.domain.entity.Pedido;
import com.uniquindio.nexuscultural.infraestructure.rest.mapper.PedidoMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final CrearPedidoUseCase crearPedidoUseCase;
    private final CancelarPedidoPendienteUseCase cancelarPedidoUseCase;
    private final PedidoMapper pedidoMapper;

    public PedidoController(CrearPedidoUseCase crearPedidoUseCase, CancelarPedidoPendienteUseCase cancelarPedidoUseCase,
                            PedidoMapper pedidoMapper) {
        this.crearPedidoUseCase = crearPedidoUseCase;
        this.cancelarPedidoUseCase = cancelarPedidoUseCase;
        this.pedidoMapper = pedidoMapper;
    }

    @PostMapping
    public ResponseEntity<PedidoDetalleResponse> crear(@Valid @RequestBody CrearPedidoRequest request) {
        Pedido pedido = crearPedidoUseCase.ejecutar(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(pedidoMapper.toDetalleResponse(pedido));
    }

    @PostMapping("/cancelar")
    public ResponseEntity<Void> cancelarPedido(@RequestBody CancelarPedidoRequest request) {
        cancelarPedidoUseCase.ejecutar(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/protegido")
    public ResponseEntity<String> endpointProtegido() {
        return ResponseEntity.ok("Acceso autorizado a ruta protegida de pedidos");
    }
}
