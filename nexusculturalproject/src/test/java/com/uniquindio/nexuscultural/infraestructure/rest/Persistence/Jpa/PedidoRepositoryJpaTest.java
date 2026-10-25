package com.uniquindio.nexuscultural.infraestructure.rest.Persistence.Jpa;


import com.uniquindio.nexuscultural.domain.entity.Pedido;
import com.uniquindio.nexuscultural.domain.repository.PedidoRepository;
import com.uniquindio.nexuscultural.domain.valueobject.EstadoPedido;
import com.uniquindio.nexuscultural.domain.valueobject.Precio;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest

@Transactional
public class PedidoRepositoryJpaTest {

    @Autowired

    private PedidoRepository repository;

    @Test
    void deberiaGuardarYRecuperarUnPedidoConSuTotalYEstado() {
        // Arrange
        UUID compradorId = UUID.randomUUID();
        UUID articuloId = UUID.randomUUID();
        LocalDateTime ahora = LocalDateTime.of(2026, 10, 25, 12, 0); // fija, sin nanosegundos
        Pedido pedido = Pedido.crear(compradorId, articuloId, 1,
                new Precio(new BigDecimal("15000"), "COP"), ahora);

        // Act
        repository.guardar(pedido);
        Pedido recuperado = repository.obtenerPorId(pedido.getId()).orElseThrow();

        // Assert
        assertEquals(pedido.getId(), recuperado.getId());
        assertEquals(compradorId, recuperado.getCompradorId());
        assertEquals(articuloId, recuperado.getArticuloId());
        assertEquals(1, recuperado.getCantidad());
        assertEquals(0, new BigDecimal("15000").compareTo(recuperado.getTotal().monto()));
        assertEquals("COP", recuperado.getTotal().moneda());
        assertEquals(EstadoPedido.PENDIENTE, recuperado.getEstado());
        assertEquals(ahora, recuperado.getFechaCreacion());
        assertNull(recuperado.getDevolucion());
    }

}
