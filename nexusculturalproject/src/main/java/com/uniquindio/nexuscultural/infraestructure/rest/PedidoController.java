package com.uniquindio.nexuscultural.infraestructure.rest;

import com.uniquindio.nexuscultural.application.dto.request.CancelarPedidoRequest;
import com.uniquindio.nexuscultural.application.dto.request.CrearPedidoRequest;
import com.uniquindio.nexuscultural.application.dto.request.DevolverProductoRequest;
import com.uniquindio.nexuscultural.application.dto.request.ProcesarPagoRequest;
import com.uniquindio.nexuscultural.application.dto.response.PedidoDetalleResponse;
import com.uniquindio.nexuscultural.application.services.PedidoService;
import com.uniquindio.nexuscultural.application.usecase.CancelarPedidoPendienteUseCase;
import com.uniquindio.nexuscultural.application.usecase.ConfirmarPedidoUseCase;
import com.uniquindio.nexuscultural.application.usecase.CrearPedidoUseCase;
import com.uniquindio.nexuscultural.application.usecase.ProcesarPagoPedidoUseCase;
import com.uniquindio.nexuscultural.domain.entity.Devolucion;
import com.uniquindio.nexuscultural.domain.entity.Pedido;
import com.uniquindio.nexuscultural.infraestructure.rest.mapper.PedidoMapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/pedidos")
@Tag(name = "Pedidos")
public class PedidoController {

    private final CrearPedidoUseCase crearPedidoUseCase;
    private final CancelarPedidoPendienteUseCase cancelarPedidoUseCase;
    private final PedidoService pedidoService;
    private final PedidoMapper pedidoMapper;

    public PedidoController(CrearPedidoUseCase crearPedidoUseCase,
                            CancelarPedidoPendienteUseCase cancelarPedidoUseCase,
                            PedidoService pedidoService,
                            PedidoMapper pedidoMapper) {
        this.crearPedidoUseCase = crearPedidoUseCase;
        this.cancelarPedidoUseCase = cancelarPedidoUseCase;
        this.pedidoService = pedidoService;
        this.pedidoMapper = pedidoMapper;
    }

    /** POST /api/pedidos — El comprador crea un pedido para un artículo */
    @PostMapping
    public ResponseEntity<PedidoDetalleResponse> crear(@Valid @RequestBody CrearPedidoRequest request) {
        Pedido pedido = crearPedidoUseCase.ejecutar(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(pedidoMapper.toDetalleResponse(pedido));
    }

    /** POST /api/pedidos/{id}/confirmar — Confirma un pedido pendiente */
    @PostMapping("/{id}/confirmar")
    public ResponseEntity<PedidoDetalleResponse> confirmar(@PathVariable UUID id) {
        return ResponseEntity.ok(pedidoMapper.toDetalleResponse(pedidoService.confirmarPedido(id)));
    }

    /** POST /api/pedidos/pago — Registra el pago de un pedido pendiente */
    @PostMapping("/pago")
    public ResponseEntity<PedidoDetalleResponse> procesarPago(@Valid @RequestBody ProcesarPagoRequest request) {
        return ResponseEntity.ok(pedidoMapper.toDetalleResponse(pedidoService.procesarPago(request)));
    }

    /** POST /api/pedidos/cancelar — El comprador cancela un pedido pendiente */
    @PostMapping("/cancelar")
    public ResponseEntity<Void> cancelarPedido(@RequestBody CancelarPedidoRequest request) {
        cancelarPedidoUseCase.ejecutar(request);
        return ResponseEntity.ok().build();
    }

    /** PATCH /api/pedidos/{id}/cancelar — Cancela el pedido y devuelve el stock al artículo */
    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<PedidoDetalleResponse> cancelar(
            @PathVariable UUID id,
            @RequestParam UUID solicitanteId) {
        return ResponseEntity.ok(pedidoMapper.toDetalleResponse(pedidoService.cancelarPedido(id, solicitanteId)));
    }

    /** PATCH /api/pedidos/{id}/devolucion/resolver — El administrador aprueba o rechaza la devolución */
    @PatchMapping("/{id}/devolucion/resolver")
    public ResponseEntity<PedidoDetalleResponse> resolverDevolucion(
            @PathVariable UUID id,
            @RequestParam boolean aprobar) {
        return ResponseEntity.ok(pedidoMapper.toDetalleResponse(pedidoService.resolverDevolucion(id, aprobar)));
    }

    @GetMapping("/protegido")
    public ResponseEntity<String> endpointProtegido() {
        return ResponseEntity.ok("Acceso autorizado a ruta protegida de pedidos");
    }
}
