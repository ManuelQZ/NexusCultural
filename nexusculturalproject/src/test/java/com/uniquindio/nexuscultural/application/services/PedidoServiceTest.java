package com.uniquindio.nexuscultural.application.services;

import com.uniquindio.nexuscultural.application.dto.request.ProcesarPagoRequest;
import com.uniquindio.nexuscultural.domain.entity.Pedido;
import com.uniquindio.nexuscultural.domain.exception.RecursoNoEncontradoException;
import com.uniquindio.nexuscultural.domain.exception.ReglaDominioException;
import com.uniquindio.nexuscultural.domain.repository.ArticuloRepository;
import com.uniquindio.nexuscultural.domain.repository.PedidoRepository;
import com.uniquindio.nexuscultural.domain.valueobject.EstadoPedido;
import com.uniquindio.nexuscultural.domain.valueobject.Precio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @Mock private PedidoRepository pedidoRepository;
    @Mock private ArticuloRepository articuloRepository;
    @InjectMocks private PedidoService service;

    private final UUID compradorId = UUID.randomUUID();

    private Pedido pedidoPendiente() {
        return Pedido.crear(compradorId, UUID.randomUUID(), 2,
                new Precio(new BigDecimal("15000"), "COP"), LocalDateTime.now());
    }

    private ProcesarPagoRequest pago(Pedido pedido, UUID comprador, String monto) {
        return new ProcesarPagoRequest(pedido.getId(), comprador,
                new Precio(new BigDecimal(monto), "COP"), "TARJETA", "REF-123");
    }

    // ---- confirmar ----

    @Test
    void confirmarDeberiaCambiarEstadoYGuardar() {
        Pedido pedido = pedidoPendiente();
        when(pedidoRepository.obtenerPorId(pedido.getId())).thenReturn(Optional.of(pedido));

        Pedido resultado = service.confirmarPedido(pedido.getId());

        assertEquals(EstadoPedido.CONFIRMADO, resultado.getEstado());
        verify(pedidoRepository).guardar(pedido);
    }

    @Test
    void confirmarInexistenteDeberiaLanzarRecursoNoEncontrado() {
        UUID id = UUID.randomUUID();
        when(pedidoRepository.obtenerPorId(id)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> service.confirmarPedido(id));

        verify(pedidoRepository, never()).guardar(any());
    }

    @Test
    void confirmarPedidoYaConfirmadoDeberiaLanzarReglaDominio() {
        Pedido pedido = pedidoPendiente();
        pedido.confirmar();
        when(pedidoRepository.obtenerPorId(pedido.getId())).thenReturn(Optional.of(pedido));

        assertThrows(ReglaDominioException.class, () -> service.confirmarPedido(pedido.getId()));

        verify(pedidoRepository, never()).guardar(any());
    }

    // ---- pago ----

    @Test
    void pagoValidoDeberiaConfirmarYGuardar() {
        Pedido pedido = pedidoPendiente();
        when(pedidoRepository.obtenerPorId(pedido.getId())).thenReturn(Optional.of(pedido));

        Pedido resultado = service.procesarPago(pago(pedido, compradorId, "30000"));

        assertEquals(EstadoPedido.CONFIRMADO, resultado.getEstado());
        assertEquals("TARJETA", resultado.getMetodoPago());
        verify(pedidoRepository).guardar(pedido);
    }

    @Test
    void pagoDePedidoInexistenteDeberiaLanzarRecursoNoEncontrado() {
        Pedido pedido = pedidoPendiente();
        when(pedidoRepository.obtenerPorId(pedido.getId())).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class,
                () -> service.procesarPago(pago(pedido, compradorId, "30000")));

        verify(pedidoRepository, never()).guardar(any());
    }

    @Test
    void pagoDeOtroCompradorDeberiaLanzarReglaDominioYNoGuardar() {
        Pedido pedido = pedidoPendiente();
        when(pedidoRepository.obtenerPorId(pedido.getId())).thenReturn(Optional.of(pedido));

        assertThrows(ReglaDominioException.class,
                () -> service.procesarPago(pago(pedido, UUID.randomUUID(), "30000")));

        assertEquals(EstadoPedido.PENDIENTE, pedido.getEstado());
        verify(pedidoRepository, never()).guardar(any());
    }

    @Test
    void pagoConMontoIncorrectoDeberiaLanzarReglaDominioYNoGuardar() {
        Pedido pedido = pedidoPendiente();
        when(pedidoRepository.obtenerPorId(pedido.getId())).thenReturn(Optional.of(pedido));

        assertThrows(ReglaDominioException.class,
                () -> service.procesarPago(pago(pedido, compradorId, "100")));

        assertEquals(EstadoPedido.PENDIENTE, pedido.getEstado());
        verify(pedidoRepository, never()).guardar(any());
    }
}
