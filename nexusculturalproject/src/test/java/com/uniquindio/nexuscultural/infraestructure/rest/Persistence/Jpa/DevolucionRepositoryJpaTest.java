package com.uniquindio.nexuscultural.infraestructure.rest.Persistence.Jpa;

import com.uniquindio.nexuscultural.domain.valueobject.EstadoDevolucion;
import com.uniquindio.nexuscultural.infraestructure.persistence.jpa.DevolucionJpaDataRepository;
import com.uniquindio.nexuscultural.infraestructure.persistence.jpa.entity.DevolucionEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
public class DevolucionRepositoryJpaTest {
    @Autowired
    private DevolucionJpaDataRepository dataRepository;

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
