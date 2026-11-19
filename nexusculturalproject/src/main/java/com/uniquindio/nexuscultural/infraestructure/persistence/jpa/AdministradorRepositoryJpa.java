package com.uniquindio.nexuscultural.infraestructure.persistence.jpa;

import com.uniquindio.nexuscultural.domain.entity.Administrador;
import com.uniquindio.nexuscultural.domain.repository.AdministradorRepository;
import com.uniquindio.nexuscultural.domain.valueobject.Email;
import com.uniquindio.nexuscultural.infraestructure.persistence.jpa.entity.AdministradorEntity;
import com.uniquindio.nexuscultural.infraestructure.persistence.jpa.mapper.AdministradorPersistenceMapper;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Primary
@Repository
public class AdministradorRepositoryJpa implements AdministradorRepository {

    private final AdministradorJpaDataRepository dataRepository;
    private final AdministradorPersistenceMapper mapper;

    public AdministradorRepositoryJpa(AdministradorJpaDataRepository dataRepository,
                                     AdministradorPersistenceMapper mapper) {
        this.dataRepository = dataRepository;
        this.mapper = mapper;
    }

    @Override
    public Administrador guardar(Administrador administrador) {
        AdministradorEntity entity = mapper.toEntity(administrador);
        return mapper.toDomain(dataRepository.save(entity));
    }

    @Override
    public Optional<Administrador> buscarPorId(UUID id) {
        return dataRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Administrador> buscarPorEmail(Email email) {
        return dataRepository.findByEmail(email.toString()).map(mapper::toDomain);
    }

    @Override
    public List<Administrador> listarTodos() {
        return dataRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public void eliminar(UUID id) {
        dataRepository.deleteById(id);
    }
}

