package com.uniquindio.nexuscultural.invariantes;

import com.uniquindio.nexuscultural.domain.entity.Pedido;
import com.uniquindio.nexuscultural.domain.exception.ReglaDominioException;
import com.uniquindio.nexuscultural.domain.valueobject.EstadoPedido;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PedidoAgregadoTest {

    @Test
    @DisplayName("Pedido PENDIENTE no permite devolución")
    void testInvariantePedidoPendiente() {
        UUID compradorId = UUID.randomUUID();
        UUID articuloId = UUID.randomUUID();
        LocalDateTime ahora = LocalDateTime.now();

        Pedido pedido = Pedido.crear(compradorId, articuloId, 1, ahora);
        EstadoPedido estadoOriginal = pedido.getEstado();

        assertThrows(ReglaDominioException.class, () -> {
            pedido.solicitarDevolucion(compradorId, "Producto defectuoso", ahora);
        });

        assertEquals(estadoOriginal, pedido.getEstado());
    }

    @Test
    @DisplayName("Solo el comprador puede solicitar devolución")
    void testInvarianteAutorizacionDevolucion() throws Exception {
        UUID compradorId = UUID.randomUUID();
        UUID articuloId = UUID.randomUUID();
        UUID otroUsuarioId = UUID.randomUUID();
        LocalDateTime ahora = LocalDateTime.now();

        Pedido pedido = Pedido.crear(compradorId, articuloId, 1, ahora);
        
        Field estadoField = Pedido.class.getDeclaredField("estado");
        estadoField.setAccessible(true);
        estadoField.set(pedido, EstadoPedido.ENTREGADO);
        
        Field fechaEntregaField = Pedido.class.getDeclaredField("fechaEntrega");
        fechaEntregaField.setAccessible(true);
        fechaEntregaField.set(pedido, ahora.minusDays(1));

        EstadoPedido estadoOriginal = pedido.getEstado();

        assertThrows(ReglaDominioException.class, () -> {
            pedido.solicitarDevolucion(otroUsuarioId, "Producto defectuoso", ahora);
        });

        assertEquals(estadoOriginal, pedido.getEstado());
    }

    @Test
    @DisplayName("No permite devolución fuera del plazo")
    void testInvariantePlazoDevolucion() throws Exception {
        UUID compradorId = UUID.randomUUID();
        UUID articuloId = UUID.randomUUID();
        LocalDateTime ahora = LocalDateTime.now();

        Pedido pedido = Pedido.crear(compradorId, articuloId, 1, ahora);
        
        Field estadoField = Pedido.class.getDeclaredField("estado");
        estadoField.setAccessible(true);
        estadoField.set(pedido, EstadoPedido.ENTREGADO);
        
        Field fechaEntregaField = Pedido.class.getDeclaredField("fechaEntrega");
        fechaEntregaField.setAccessible(true);
        fechaEntregaField.set(pedido, ahora.minusDays(10));

        EstadoPedido estadoOriginal = pedido.getEstado();

        assertThrows(ReglaDominioException.class, () -> {
            pedido.solicitarDevolucion(compradorId, "Producto defectuoso", ahora);
        });

        assertEquals(estadoOriginal, pedido.getEstado());
    }

    @Test
    @DisplayName("No permite crear pedido con cantidad inválida")
    void testInvarianteCreacionPedido() {
        UUID compradorId = UUID.randomUUID();
        UUID articuloId = UUID.randomUUID();
        LocalDateTime ahora = LocalDateTime.now();

        assertThrows(ReglaDominioException.class, () -> {
            Pedido.crear(compradorId, articuloId, 0, ahora);
        });
    }
}
