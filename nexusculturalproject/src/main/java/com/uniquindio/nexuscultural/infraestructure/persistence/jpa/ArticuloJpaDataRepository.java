package com.uniquindio.nexuscultural.infraestructure.persistence.jpa;

import com.uniquindio.nexuscultural.domain.valueobject.EstadoArticulo;
import com.uniquindio.nexuscultural.infraestructure.persistence.jpa.entity.ArticuloEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ArticuloJpaDataRepository extends JpaRepository<ArticuloEntity, UUID> {
    List<ArticuloEntity> findByArtesanoId(UUID artesanoId);

    List<ArticuloEntity> findByEstado(EstadoArticulo estado);

}
