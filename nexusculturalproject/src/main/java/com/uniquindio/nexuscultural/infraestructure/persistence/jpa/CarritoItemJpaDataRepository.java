package com.uniquindio.nexuscultural.infraestructure.persistence.jpa;

import com.uniquindio.nexuscultural.infraestructure.persistence.jpa.entity.CarritoItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CarritoItemJpaDataRepository extends JpaRepository<CarritoItemEntity, UUID> {

    List<CarritoItemEntity> findByCompradorId(UUID compradorId);

    Optional<CarritoItemEntity> findByCompradorIdAndArticuloId(UUID compradorId, UUID articuloId);

    @Modifying
    @Query("DELETE FROM CarritoItemEntity c WHERE c.compradorId = :compradorId")
    void deleteByCompradorId(@Param("compradorId") UUID compradorId);
}

