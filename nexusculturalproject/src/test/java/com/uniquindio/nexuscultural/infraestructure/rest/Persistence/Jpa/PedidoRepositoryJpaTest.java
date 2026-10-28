package com.uniquindio.nexuscultural.infraestructure.rest.Persistence.Jpa;


import com.uniquindio.nexuscultural.domain.entity.Pedido;
import com.uniquindio.nexuscultural.domain.repository.PedidoRepository;
import com.uniquindio.nexuscultural.domain.valueobject.EstadoPedido;
import com.uniquindio.nexuscultural.domain.valueobject.Precio;
import com.uniquindio.nexuscultural.infraestructure.persistence.jpa.PedidoJpaDataRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest

@Transactional
public class PedidoRepositoryJpaTest {

    @Autowired

    private PedidoRepository repository;

    @Autowired
    private PedidoJpaDataRepository dataRepository;

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

    @Test
    void deberiaContarPedidosPorEstadoUsandoGroupBy() {
        // Act - Consulta con GROUP BY sobre datos de data.sql
        var resultados = dataRepository.contarPedidosPorEstado();

        // Assert - Verificar que hay resultados
        assertNotNull(resultados);
        assertEquals(4, resultados.size()); // PENDIENTE, CONFIRMADO, ENTREGADO, EN_CAMINO

        // Verificar conteos específicos según data.sql
        long pendientes = resultados.stream()
                .filter(row -> row[0].equals(EstadoPedido.PENDIENTE))
                .mapToLong(row -> (Long) row[1])
                .sum();
        assertEquals(1L, pendientes);

        long entregados = resultados.stream()
                .filter(row -> row[0].equals(EstadoPedido.ENTREGADO))
                .mapToLong(row -> (Long) row[1])
                .sum();
        assertEquals(2L, entregados);
    }

}
