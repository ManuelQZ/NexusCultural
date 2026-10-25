package com.uniquindio.nexuscultural.infraestructure.persistence.jpa;

import com.uniquindio.nexuscultural.domain.valueobject.EstadoDevolucion;
import com.uniquindio.nexuscultural.infraestructure.persistence.jpa.entity.DevolucionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DevolucionJpaDataRepository extends JpaRepository<DevolucionEntity, UUID> {
    Optional<DevolucionEntity> findByPedidoId(UUID pedidoId);

    List<DevolucionEntity> findByEstadoOrderByFechaSolicitudAsc(EstadoDevolucion estado);
}
