package com.uniquindio.nexuscultural.infraestructure.persistence.jpa;

import com.uniquindio.nexuscultural.domain.valueobject.EstadoArticulo;
import com.uniquindio.nexuscultural.domain.valueobject.Grupo;
import com.uniquindio.nexuscultural.infraestructure.persistence.jpa.entity.ArticuloEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ArticuloJpaDataRepository extends JpaRepository<ArticuloEntity, UUID> {
    List<ArticuloEntity> findByArtesanoId(UUID artesanoId);

    List<ArticuloEntity> findByEstado(EstadoArticulo estado);

    @Query("""
        SELECT a FROM ArticuloEntity a
        WHERE a.estado <> :excluido
          AND (:grupo IS NULL OR a.grupo = :grupo)
          AND (LOWER(a.titulo) LIKE LOWER(CONCAT('%', :texto, '%'))
               OR LOWER(a.descripcion) LIKE LOWER(CONCAT('%', :texto, '%')))
        """)
    Page<ArticuloEntity> buscar(@Param("texto") String texto,
                                @Param("grupo") Grupo grupo,
                                @Param("excluido") EstadoArticulo excluido,
                                Pageable pageable);

}
