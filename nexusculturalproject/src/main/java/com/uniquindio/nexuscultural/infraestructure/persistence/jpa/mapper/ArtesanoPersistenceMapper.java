package com.uniquindio.nexuscultural.infraestructure.persistence.jpa.mapper;

import com.uniquindio.nexuscultural.domain.entity.Artesano;
import com.uniquindio.nexuscultural.domain.valueobject.Email;
import com.uniquindio.nexuscultural.domain.valueobject.TrayectoriaCultural;
import com.uniquindio.nexuscultural.domain.valueobject.UbicacionGeografica;
import com.uniquindio.nexuscultural.infraestructure.persistence.jpa.entity.ArtesanoEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class ArtesanoPersistenceMapper {

    public ArtesanoEntity toEntity(Artesano domain) {
        if (domain == null) return null;
        ArtesanoEntity entity = new ArtesanoEntity();
        entity.setId(domain.getId());
        entity.setNombreCompleto(domain.getNombreCompleto());
        entity.setEmail(domain.getEmail().toString());
        entity.setFechaRegistro(domain.getFechaRegistro());
        entity.setActivo(domain.isActivo());
        entity.setRol(domain.getRol());

        if (domain.getTrayectoriaCultural() != null) {
            entity.setAnosExperiencia(domain.getTrayectoriaCultural().anosExperiencia());
            entity.setReconocimientos(new ArrayList<>(domain.getTrayectoriaCultural().reconocimientos()));
        }

        if (domain.getUbicacionGeografica() != null) {
            entity.setDepartamento(domain.getUbicacionGeografica().departamento());
            entity.setMunicipio(domain.getUbicacionGeografica().municipio());
            entity.setDireccion(domain.getUbicacionGeografica().direccion());
        }

        return entity;
    }

    public Artesano toDomain(ArtesanoEntity entity) {
        if (entity == null) return null;

        TrayectoriaCultural trayectoria = new TrayectoriaCultural(
                entity.getAnosExperiencia(),
                entity.getReconocimientos() != null ? entity.getReconocimientos() : new ArrayList<>()
        );

        UbicacionGeografica ubicacion = new UbicacionGeografica(
                entity.getDepartamento(),
                entity.getMunicipio(),
                entity.getDireccion()
        );

        return Artesano.builder()
                .id(entity.getId())
                .nombreCompleto(entity.getNombreCompleto())
                .email(new Email(entity.getEmail()))
                .fechaRegistro(entity.getFechaRegistro())
                .trayectoriaCultural(trayectoria)
                .ubicacionGeografica(ubicacion)
                .activo(entity.isActivo())
                .build();
    }
}

