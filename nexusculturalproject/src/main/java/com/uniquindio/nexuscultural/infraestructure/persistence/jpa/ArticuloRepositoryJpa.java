package com.uniquindio.nexuscultural.infraestructure.persistence.jpa;

import com.uniquindio.nexuscultural.domain.entity.Articulo;
import com.uniquindio.nexuscultural.domain.repository.ArticuloRepository;
import com.uniquindio.nexuscultural.infraestructure.persistence.jpa.entity.ArticuloEntity;
import com.uniquindio.nexuscultural.infraestructure.persistence.jpa.mapper.ArticuloPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ArticuloRepositoryJpa implements ArticuloRepository {

    private final ArticuloJpaDataRepository dataRepository;
    private final ArticuloPersistenceMapper mapper;

    @Override
    @Transactional
    public Articulo guardar(Articulo articulo) {
        ArticuloEntity entity = dataRepository.findById(articulo.getId())
                .map(existente -> {
                    mapper.actualizarEntity(articulo, existente);
                    return existente;
                })
                .orElseGet(() -> mapper.toEntity(articulo));
        return mapper.toDomain(dataRepository.save(entity));
    }

    @Override
    public Optional<Articulo> buscarPorId(UUID id) {
        return dataRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    @Transactional
    public void eliminar(UUID id) {
        dataRepository.deleteById(id);
    }
}
