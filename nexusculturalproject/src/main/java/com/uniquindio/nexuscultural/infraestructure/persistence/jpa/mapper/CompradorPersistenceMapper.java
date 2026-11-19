package com.uniquindio.nexuscultural.infraestructure.persistence.jpa.mapper;

import com.uniquindio.nexuscultural.domain.entity.Comprador;
import com.uniquindio.nexuscultural.domain.valueobject.Email;
import com.uniquindio.nexuscultural.domain.valueobject.UbicacionGeografica;
import com.uniquindio.nexuscultural.infraestructure.persistence.jpa.entity.CompradorEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class CompradorPersistenceMapper {

    public CompradorEntity toEntity(Comprador domain) {
        if (domain == null) return null;
        CompradorEntity entity = new CompradorEntity();
        entity.setId(domain.getId());
        entity.setNombreCompleto(domain.getNombreCompleto());
        entity.setEmail(domain.getEmail().toString());
        entity.setFechaRegistro(domain.getFechaRegistro());
        entity.setRol(domain.getRol());

        if (domain.getDireccionEntrega() != null) {
            entity.setDepartamento(domain.getDireccionEntrega().departamento());
            entity.setMunicipio(domain.getDireccionEntrega().municipio());
            entity.setDireccion(domain.getDireccionEntrega().direccion());
        }

        if (domain.getFavoritos() != null) {
            entity.setFavoritos(new ArrayList<>(domain.getFavoritos()));
        }

        return entity;
    }

    public Comprador toDomain(CompradorEntity entity) {
        if (entity == null) return null;

        UbicacionGeografica direccion = new UbicacionGeografica(
                entity.getDepartamento(),
                entity.getMunicipio(),
                entity.getDireccion()
        );

        return Comprador.builder()
                .id(entity.getId())
                .nombreCompleto(entity.getNombreCompleto())
                .email(new Email(entity.getEmail()))
                .fechaRegistro(entity.getFechaRegistro())
                .direccionEntrega(direccion)
                .favoritos(entity.getFavoritos() != null ? new ArrayList<>(entity.getFavoritos()) : new ArrayList<>())
                .build();
    }
}

