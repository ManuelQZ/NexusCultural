package com.uniquindio.nexuscultural.infraestructure.persistence.jpa;

import com.uniquindio.nexuscultural.domain.entity.Devolucion;
import com.uniquindio.nexuscultural.domain.repository.DevolucionRepository;
import com.uniquindio.nexuscultural.domain.valueobject.EstadoDevolucion;
import com.uniquindio.nexuscultural.infraestructure.persistence.jpa.mapper.DevolucionPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class DevolucionRepositoryJpa implements DevolucionRepository {
    private final DevolucionJpaDataRepository dataRepository;
    private final DevolucionPersistenceMapper mapper;

    @Override
    public Optional<Devolucion> obtenerPorId(UUID id) {
        return dataRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Devolucion> obtenerPorPedidoId(UUID pedidoId) {
        return dataRepository.findByPedidoId(pedidoId).map(mapper::toDomain);
    }

    @Override
    public List<Devolucion> obtenerPorEstado(EstadoDevolucion estado) {
        return dataRepository.findByEstadoOrderByFechaSolicitudAsc(estado).stream()
                .map(mapper::toDomain)
                .toList();
    }

}
