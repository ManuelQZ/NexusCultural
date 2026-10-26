package com.uniquindio.nexuscultural.infraestructure.persistence.jpa.mapper;

import com.uniquindio.nexuscultural.domain.entity.Articulo;
import com.uniquindio.nexuscultural.domain.valueobject.Precio;
import com.uniquindio.nexuscultural.infraestructure.persistence.jpa.entity.ArticuloEntity;
import com.uniquindio.nexuscultural.infraestructure.persistence.jpa.entity.PrecioEmbeddable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
@RequiredArgsConstructor
public class ArticuloPersistenceMapper {
    public ArticuloEntity toEntity(Articulo articulo) {
        ArticuloEntity entity = new ArticuloEntity();
        entity.setId(articulo.getId());
        entity.setArtesanoId(articulo.getArtesanoId());
        entity.setGrupo(articulo.getGrupo());
        entity.setFechaPublicacion(articulo.getFechaPublicacion());
        entity.setImagenes(new ArrayList<>(articulo.getImagenes()));
        copiarCamposMutables(articulo, entity);
        return entity;
    }

    public void actualizarEntity(Articulo articulo, ArticuloEntity entity) {
        if (!articulo.getId().equals(entity.getId())) {
            throw new IllegalArgumentException("El ID del artículo no coincide con la entidad.");
        }
        copiarCamposMutables(articulo, entity);
    }

    public Articulo toDomain(ArticuloEntity entity) {
        return Articulo.reconstruir(
                entity.getId(),
                entity.getArtesanoId(),
                entity.getTitulo(),
                entity.getDescripcion(),
                entity.getStock(),
                entity.getEstado(),
                entity.getGrupo(),
                new ArrayList<>(entity.getImagenes()),
                new Precio(entity.getPrecio().getMonto(), entity.getPrecio().getMoneda()),
                entity.getFechaPublicacion());
    }

    private void copiarCamposMutables(Articulo articulo, ArticuloEntity entity) {
        entity.setTitulo(articulo.getTitulo());
        entity.setDescripcion(articulo.getDescripcion());
        entity.setStock(articulo.getStock());
        entity.setEstado(articulo.getEstado());
        entity.setPrecio(new PrecioEmbeddable(
                articulo.getPrecio().monto(), articulo.getPrecio().moneda()));
    }
}
