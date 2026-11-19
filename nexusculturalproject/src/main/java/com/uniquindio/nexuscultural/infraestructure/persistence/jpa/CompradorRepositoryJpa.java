package com.uniquindio.nexuscultural.infraestructure.persistence.jpa;

import com.uniquindio.nexuscultural.domain.entity.Comprador;
import com.uniquindio.nexuscultural.domain.repository.CompradorRepository;
import com.uniquindio.nexuscultural.domain.valueobject.Email;
import com.uniquindio.nexuscultural.infraestructure.persistence.jpa.entity.CompradorEntity;
import com.uniquindio.nexuscultural.infraestructure.persistence.jpa.mapper.CompradorPersistenceMapper;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Primary
@Repository
public class CompradorRepositoryJpa implements CompradorRepository {

    private final CompradorJpaDataRepository dataRepository;
    private final CompradorPersistenceMapper mapper;

    public CompradorRepositoryJpa(CompradorJpaDataRepository dataRepository,
                                 CompradorPersistenceMapper mapper) {
        this.dataRepository = dataRepository;
        this.mapper = mapper;
    }

    @Override
    public Comprador guardar(Comprador comprador) {
        CompradorEntity entity = mapper.toEntity(comprador);
        return mapper.toDomain(dataRepository.save(entity));
    }

    @Override
    public Optional<Comprador> buscarPorId(UUID id) {
        return dataRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Comprador> buscarPorEmail(Email email) {
        return dataRepository.findByEmail(email.toString()).map(mapper::toDomain);
    }
}

