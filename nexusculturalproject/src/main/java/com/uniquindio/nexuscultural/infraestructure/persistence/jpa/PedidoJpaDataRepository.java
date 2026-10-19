package com.uniquindio.nexuscultural.infraestructure.persistence.jpa;

import com.uniquindio.nexuscultural.domain.valueobject.EstadoPedido;
import com.uniquindio.nexuscultural.infraestructure.persistence.jpa.entity.PedidoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PedidoJpaDataRepository extends JpaRepository<PedidoEntity, UUID> {
    List<PedidoEntity> findByCompradorIdOrderByFechaCreacionDesc(UUID compradorId);

    boolean existsByCompradorIdAndArticuloIdAndEstado(
            UUID compradorId, UUID articuloId, EstadoPedido estado);
}
