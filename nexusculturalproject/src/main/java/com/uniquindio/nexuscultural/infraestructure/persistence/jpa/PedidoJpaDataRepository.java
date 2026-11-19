package com.uniquindio.nexuscultural.infraestructure.persistence.jpa;

import com.uniquindio.nexuscultural.domain.valueobject.EstadoPedido;
import com.uniquindio.nexuscultural.infraestructure.persistence.jpa.entity.PedidoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PedidoJpaDataRepository extends JpaRepository<PedidoEntity, UUID> {

    List<PedidoEntity> findByCompradorIdOrderByFechaCreacionDesc(UUID compradorId);

    boolean existsByCompradorIdAndArticuloIdAndEstado(
            UUID compradorId, UUID articuloId, EstadoPedido estado);

    @Query("SELECT p FROM PedidoEntity p WHERE p.articuloId IN (SELECT a.id FROM ArticuloEntity a WHERE a.artesanoId = :artesanoId)")
    List<PedidoEntity> findByArtesanoId(@Param("artesanoId") UUID artesanoId);

    Optional<PedidoEntity> findByReferenciaTransaccion(String referenciaTransaccion);

    @Query("SELECT p FROM PedidoEntity p WHERE p.id IN (SELECT d.pedidoId FROM DevolucionEntity d)")
    List<PedidoEntity> findPedidosConDevolucion();

    @Query("SELECT p.estado, COUNT(p) FROM PedidoEntity p GROUP BY p.estado")
    List<Object[]> contarPedidosPorEstado();
}
