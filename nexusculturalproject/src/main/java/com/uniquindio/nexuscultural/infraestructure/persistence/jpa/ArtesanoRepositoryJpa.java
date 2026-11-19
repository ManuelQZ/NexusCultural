package com.uniquindio.nexuscultural.infraestructure.persistence.jpa;

import com.uniquindio.nexuscultural.domain.entity.Artesano;
import com.uniquindio.nexuscultural.domain.repository.ArtesanoRepository;
import com.uniquindio.nexuscultural.domain.valueobject.Email;
import com.uniquindio.nexuscultural.infraestructure.persistence.jpa.entity.ArtesanoEntity;
import com.uniquindio.nexuscultural.infraestructure.persistence.jpa.mapper.ArtesanoPersistenceMapper;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Primary
@Repository
public class ArtesanoRepositoryJpa implements ArtesanoRepository {

    private final ArtesanoJpaDataRepository dataRepository;
    private final ArtesanoPersistenceMapper mapper;

    public ArtesanoRepositoryJpa(ArtesanoJpaDataRepository dataRepository,
                                ArtesanoPersistenceMapper mapper) {
        this.dataRepository = dataRepository;
        this.mapper = mapper;
    }

    @Override
    public Artesano guardar(Artesano artesano) {
        ArtesanoEntity entity = mapper.toEntity(artesano);
        return mapper.toDomain(dataRepository.save(entity));
    }

    @Override
    public Optional<Artesano> buscarPorId(UUID id) {
        return dataRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Artesano> buscarPorEmail(Email email) {
        return dataRepository.findByEmail(email.toString()).map(mapper::toDomain);
    }
}

