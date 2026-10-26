package com.uniquindio.nexuscultural.infrastructure.persistence;

import com.uniquindio.nexuscultural.domain.entity.Pedido;
import com.uniquindio.nexuscultural.domain.repository.PedidoRepository;
import com.uniquindio.nexuscultural.domain.valueobject.Precio;
import com.uniquindio.nexuscultural.infraestructure.persistence.jpa.PedidoRepositoryJpa;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(PedidoRepositoryJpa.class) // Importa explicitamente el adaptador del puerto de dominio
class PedidoRepositoryDataJpaTest {

    @Autowired
    private PedidoRepository pedidoRepository; // Inyecta la interfaz de dominio, no la de Spring Data

    @Test
    @DisplayName("Punto 2: Debe guardar y recuperar una entidad usando @DataJpaTest y el Adaptador de Dominio")
    void deberiaGuardarYRecuperarPedidoConDataJpaTest() {
        // Arrange
        UUID compradorId = UUID.randomUUID();
        UUID articuloId = UUID.randomUUID();
        Pedido pedido = Pedido.crear(compradorId, articuloId, 2, new Precio(new BigDecimal(15000), "cop"), LocalDateTime.now());

        // Act
        pedidoRepository.guardar(pedido);
        Optional<Pedido> encontrado = pedidoRepository.obtenerPorId(pedido.getId());

        // Assert
        assertTrue(encontrado.isPresent());
        assertEquals(pedido.getId(), encontrado.get().getId());
        assertEquals(2, encontrado.get().getCantidad());
    }
}
