package com.uniquindio.nexuscultural.infrastructure.rest;

import com.uniquindio.nexuscultural.application.dto.request.CancelarPedidoRequest;
import com.uniquindio.nexuscultural.application.usecase.CancelarPedidoPendienteUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final CancelarPedidoPendienteUseCase cancelarPedidoUseCase;

    public PedidoController(CancelarPedidoPendienteUseCase cancelarPedidoUseCase) {
        this.cancelarPedidoUseCase = cancelarPedidoUseCase;
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
