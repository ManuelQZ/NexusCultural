package com.uniquindio.nexuscultural.infraestructure.persistence.jpa.mapper;

import com.uniquindio.nexuscultural.domain.entity.Administrador;
import com.uniquindio.nexuscultural.domain.valueobject.Email;
import com.uniquindio.nexuscultural.infraestructure.persistence.jpa.entity.AdministradorEntity;
import org.springframework.stereotype.Component;

@Component
public class AdministradorPersistenceMapper {

    public AdministradorEntity toEntity(Administrador domain) {
        if (domain == null) return null;
        AdministradorEntity entity = new AdministradorEntity();
        entity.setId(domain.getId());
        entity.setNombreCompleto(domain.getNombreCompleto());
        entity.setEmail(domain.getEmail().toString());
        entity.setFechaRegistro(domain.getFechaRegistro());
        entity.setNivelAcceso(domain.getNivelAcceso());
        entity.setRol(domain.getRol());
        return entity;
    }

    public Administrador toDomain(AdministradorEntity entity) {
        if (entity == null) return null;
        return Administrador.builder()
                .id(entity.getId())
                .nombreCompleto(entity.getNombreCompleto())
                .email(new Email(entity.getEmail()))
                .fechaRegistro(entity.getFechaRegistro())
                .nivelAcceso(entity.getNivelAcceso())
                .build();
    }
}

