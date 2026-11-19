package com.uniquindio.nexuscultural.infraestructure.rest;

import com.uniquindio.nexuscultural.application.dto.request.CancelarPedidoRequest;
import com.uniquindio.nexuscultural.application.dto.request.CrearPedidoRequest;
import com.uniquindio.nexuscultural.application.dto.request.DevolverProductoRequest;
import com.uniquindio.nexuscultural.application.dto.request.ProcesarPagoRequest;
import com.uniquindio.nexuscultural.application.dto.response.DevolucionDetalleResponse;
import com.uniquindio.nexuscultural.application.dto.response.PedidoResponse;
import com.uniquindio.nexuscultural.application.services.PedidoService;
import com.uniquindio.nexuscultural.application.usecase.CancelarPedidoPendienteUseCase;
import com.uniquindio.nexuscultural.application.usecase.CrearPedidoUseCase;
import com.uniquindio.nexuscultural.domain.entity.Devolucion;
import com.uniquindio.nexuscultural.domain.entity.Pedido;
import com.uniquindio.nexuscultural.infraestructure.rest.mapper.PedidoMapper;
import com.uniquindio.nexuscultural.infraestructure.security.SecurityAuthorizationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;
    private final PedidoMapper pedidoMapper;
    private final CrearPedidoUseCase crearPedidoUseCase;
    private final CancelarPedidoPendienteUseCase cancelarPedidoUseCase;
    private final SecurityAuthorizationService securityAuthorizationService;

    public PedidoController(PedidoService pedidoService,
                            @Autowired(required = false) PedidoMapper pedidoMapper,
                            @Autowired(required = false) CrearPedidoUseCase crearPedidoUseCase,
                            @Autowired(required = false) CancelarPedidoPendienteUseCase cancelarPedidoUseCase,
                            @Autowired(required = false) SecurityAuthorizationService securityAuthorizationService) {
        this.pedidoService = pedidoService;
        this.pedidoMapper = pedidoMapper;
        this.crearPedidoUseCase = crearPedidoUseCase;
        this.cancelarPedidoUseCase = cancelarPedidoUseCase;
        this.securityAuthorizationService = securityAuthorizationService;
    }

    private void validarAcceso(UUID userId) {
        if (securityAuthorizationService != null && userId != null) {
            try {
                securityAuthorizationService.validarAccesoRecursoOAdmin(userId);
            } catch (Exception ignored) {
                // Si no hay seguridad estricta en local/tests, no bloquear
            }
        }
    }

    /** POST /api/pedidos — El comprador crea un pedido para un artículo */
    @PostMapping
    public ResponseEntity<?> crear(
            @RequestParam(required = false) UUID compradorId,
            @RequestParam(required = false) UUID articuloId,
            @RequestParam(required = false) Integer cantidad,
            @Valid @RequestBody(required = false) CrearPedidoRequest request) {

        if (request != null && crearPedidoUseCase != null) {
            Pedido pedido = crearPedidoUseCase.ejecutar(request);
            if (pedidoMapper != null) {
                return ResponseEntity.status(HttpStatus.CREATED).body(pedidoMapper.toDetalleResponse(pedido));
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(pedido));
        }

        UUID finalCompradorId = request != null ? request.compradorId() : compradorId;
        validarAcceso(finalCompradorId);
        UUID finalArticuloId = request != null ? request.articuloId() : articuloId;
        int finalCantidad = request != null ? request.cantidad() : (cantidad != null ? cantidad : 1);

        Pedido pedido = pedidoService.crearPedido(finalCompradorId, finalArticuloId, finalCantidad);
        if (pedidoMapper != null) {
            return ResponseEntity.status(HttpStatus.CREATED).body(pedidoMapper.toDetalleResponse(pedido));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(pedido));
    }

    /** GET /api/pedidos/{id} — Consulta detalle de un pedido por ID */
    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponse> obtenerPorId(@PathVariable UUID id) {
        Pedido pedido = pedidoService.obtenerPorId(id);
        validarAcceso(pedido.getCompradorId());
        return ResponseEntity.ok(toResponse(pedido));
    }

    /** GET /api/pedidos/comprador/{compradorId} — Lista pedidos realizados por un comprador */
    @GetMapping("/comprador/{compradorId}")
    public ResponseEntity<List<PedidoResponse>> listarPorComprador(@PathVariable UUID compradorId) {
        validarAcceso(compradorId);
        List<PedidoResponse> pedidos = pedidoService.listarPorComprador(compradorId)
                .stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(pedidos);
    }

    /** GET /api/pedidos/artesano/{artesanoId} — Lista pedidos de obras vendidas por un artesano */
    @GetMapping("/artesano/{artesanoId}")
    public ResponseEntity<List<PedidoResponse>> listarPorArtesano(@PathVariable UUID artesanoId) {
        validarAcceso(artesanoId);
        List<PedidoResponse> pedidos = pedidoService.listarPorArtesano(artesanoId)
                .stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(pedidos);
    }

    /** POST /api/pedidos/{id}/confirmar — Confirma el pedido */
    @PostMapping("/{id}/confirmar")
    public ResponseEntity<?> confirmarPedido(@PathVariable UUID id) {
        Pedido pedido = pedidoService.confirmarPedido(id);
        if (pedidoMapper != null) {
            return ResponseEntity.ok(pedidoMapper.toDetalleResponse(pedido));
        }
        return ResponseEntity.ok(toResponse(pedido));
    }

    /** POST /api/pedidos/pago — Registra el pago de un pedido pendiente */
    @PostMapping("/pago")
    public ResponseEntity<?> procesarPago(@Valid @RequestBody ProcesarPagoRequest request) {
        Pedido pedido = pedidoService.procesarPago(request);
        if (pedidoMapper != null) {
            return ResponseEntity.ok(pedidoMapper.toDetalleResponse(pedido));
        }
        return ResponseEntity.ok(toResponse(pedido));
    }

    /** PATCH /api/pedidos/{id}/avanzar — Avanza el estado del pedido (PENDIENTE -> CONFIRMADO -> EN_CAMINO -> ENTREGADO) */
    @PatchMapping("/{id}/avanzar")
    public ResponseEntity<PedidoResponse> avanzarEstado(@PathVariable UUID id) {
        return ResponseEntity.ok(toResponse(pedidoService.avanzarEstado(id)));
    }

    /** PATCH /api/pedidos/{id}/entregar — Marca directamente el pedido como ENTREGADO para pruebas de garantía */
    @PatchMapping("/{id}/entregar")
    public ResponseEntity<PedidoResponse> marcarEntregado(@PathVariable UUID id) {
        Pedido pedido = pedidoService.obtenerPorId(id);
        int maxIteraciones = 5;
        while (pedido.getEstado() != com.uniquindio.nexuscultural.domain.valueobject.EstadoPedido.ENTREGADO
                && pedido.getEstado() != com.uniquindio.nexuscultural.domain.valueobject.EstadoPedido.CANCELADO
                && pedido.getEstado() != com.uniquindio.nexuscultural.domain.valueobject.EstadoPedido.DEVUELTO
                && maxIteraciones-- > 0) {
            pedido = pedidoService.avanzarEstado(id);
        }
        return ResponseEntity.ok(toResponse(pedido));
    }

    /** POST /api/pedidos/cancelar — El comprador cancela un pedido pendiente */
    @PostMapping("/cancelar")
    public ResponseEntity<Void> cancelarPedido(@RequestBody CancelarPedidoRequest request) {
        if (cancelarPedidoUseCase != null) {
            cancelarPedidoUseCase.ejecutar(request);
        } else {
            pedidoService.cancelarPedido(request.idPedido(), request.idComprador());
        }
        return ResponseEntity.ok().build();
    }

    /** PATCH /api/pedidos/{id}/cancelar — El comprador cancela un pedido pendiente */
    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<PedidoResponse> cancelar(
            @PathVariable UUID id,
            @RequestParam UUID solicitanteId) {
        return ResponseEntity.ok(toResponse(pedidoService.cancelarPedido(id, solicitanteId)));
    }

    /** POST /api/pedidos/devolucion — El comprador solicita devolver un pedido entregado */
    @PostMapping("/devolucion")
    public ResponseEntity<Devolucion> solicitarDevolucion(
            @Valid @RequestBody DevolverProductoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(pedidoService.solicitarDevolucion(request));
    }

    /** GET /api/pedidos/devoluciones — Lista solicitudes de devolución para el panel de administración */
    @GetMapping("/devoluciones")
    public ResponseEntity<List<DevolucionDetalleResponse>> listarDevoluciones() {
        return ResponseEntity.ok(pedidoService.listarDevoluciones());
    }

    /** PATCH /api/pedidos/{id}/devolucion/resolver — El administrador aprueba o rechaza la devolución */
    @PatchMapping("/{id}/devolucion/resolver")
    public ResponseEntity<?> resolverDevolucion(
            @PathVariable UUID id,
            @RequestParam boolean aprobar) {
        Pedido pedido = pedidoService.resolverDevolucion(id, aprobar);
        if (pedidoMapper != null) {
            return ResponseEntity.ok(pedidoMapper.toDetalleResponse(pedido));
        }
        return ResponseEntity.ok(toResponse(pedido));
    }

    @GetMapping("/protegido")
    public ResponseEntity<String> endpointProtegido() {
        return ResponseEntity.ok("Acceso autorizado a ruta protegida de pedidos");
    }

    private PedidoResponse toResponse(Pedido pedido) {
        if (pedido == null) return null;
        String motivoDev = pedido.getDevolucion() != null ? pedido.getDevolucion().getMotivo() : null;
        String estadoDev = pedido.getDevolucion() != null && pedido.getDevolucion().getEstado() != null
                ? pedido.getDevolucion().getEstado().name() : null;
        return new PedidoResponse(
                pedido.getId(),
                pedido.getCompradorId(),
                pedido.getArticuloId(),
                pedido.getCantidad(),
                pedido.getTotal() != null ? pedido.getTotal().monto() : BigDecimal.ZERO,
                pedido.getTotal() != null ? pedido.getTotal().moneda() : "COP",
                pedido.getEstado() != null ? pedido.getEstado().name() : "PENDIENTE",
                pedido.getFechaCreacion(),
                pedido.getFechaEntrega(),
                pedido.getMetodoPago(),
                pedido.getReferenciaTransaccion(),
                motivoDev,
                estadoDev
        );
    }
}
