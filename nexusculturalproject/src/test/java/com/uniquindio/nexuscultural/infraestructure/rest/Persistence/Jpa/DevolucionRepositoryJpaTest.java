package com.uniquindio.nexuscultural.infraestructure.rest.Persistence.Jpa;

import com.uniquindio.nexuscultural.domain.entity.Devolucion;
import com.uniquindio.nexuscultural.domain.repository.DevolucionRepository;
import com.uniquindio.nexuscultural.domain.valueobject.EstadoDevolucion;
import com.uniquindio.nexuscultural.infraestructure.persistence.jpa.DevolucionJpaDataRepository;
import com.uniquindio.nexuscultural.infraestructure.persistence.jpa.DevolucionRepositoryJpa;
import com.uniquindio.nexuscultural.infraestructure.persistence.jpa.entity.DevolucionEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@Import(DevolucionRepositoryJpa.class)
public class DevolucionRepositoryJpaTest {

    @Autowired
    private DevolucionRepository devolucionRepository;

    @Autowired
    private DevolucionJpaDataRepository dataRepository;

    @Test
    void deberiaBuscarDevolucionPorEstado() {
        // Arrange - Usar el adaptador del dominio, no directamente el repositorio JPA
        // Act - Consultar por estado usando el puerto del dominio
        var devoluciones = devolucionRepository.obtenerPorEstado(EstadoDevolucion.EN_PROCESO);

        // Assert - Verificar que funciona el mapper y el adaptador
        assertEquals(1, devoluciones.size());
        assertEquals(EstadoDevolucion.EN_PROCESO, devoluciones.get(0).getEstado());
    }

    @Test
    void noPermiteDosDevolucionesActivasParaElMismoPedido() {
        UUID pedidoId = UUID.randomUUID();

        DevolucionEntity primera = new DevolucionEntity();
        primera.setId(UUID.randomUUID());
        primera.setPedidoId(pedidoId);
        primera.setMotivo("Producto defectuoso");
        primera.setFechaSolicitud(LocalDateTime.now());
        primera.setEstado(EstadoDevolucion.EN_PROCESO);
        dataRepository.saveAndFlush(primera);

        DevolucionEntity segunda = new DevolucionEntity();
        segunda.setId(UUID.randomUUID());
        segunda.setPedidoId(pedidoId);
        segunda.setMotivo("Otra razón");
        segunda.setFechaSolicitud(LocalDateTime.now());
        segunda.setEstado(EstadoDevolucion.EN_PROCESO);

        assertThrows(DataIntegrityViolationException.class,
                () -> dataRepository.saveAndFlush(segunda));
    }
}
