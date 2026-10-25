package com.uniquindio.nexuscultural.infraestructure.persistence.jpa;

import com.uniquindio.nexuscultural.domain.entity.Pedido;
import com.uniquindio.nexuscultural.domain.repository.PedidoRepository;
import com.uniquindio.nexuscultural.domain.valueobject.EstadoPedido;
import com.uniquindio.nexuscultural.infraestructure.persistence.jpa.entity.PedidoEntity;
import com.uniquindio.nexuscultural.infraestructure.persistence.jpa.mapper.PedidoPersistenceMapper;
import com.uniquindio.nexuscultural.infraestructure.rest.mapper.PedidoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class PedidoRepositoryJpa implements PedidoRepository {

    private final PedidoJpaDataRepository dataRepository;
    private final PedidoPersistenceMapper mapper;

    @Override
    public Optional<Pedido> obtenerPorId(String id) {
        return dataRepository.findById(UUID.fromString(id)).map(mapper::toDomain);
    }

    @Override
    @Transactional
    public void guardar(Pedido pedido) {
        PedidoEntity entity = dataRepository.findById(pedido.getId())
                .map(existente -> {
                    mapper.actualizarEntity(pedido, existente);
                    return existente;
                })
                .orElseGet(() -> mapper.toEntity(pedido));
        dataRepository.save(entity);
    }

    @Override
    public List<Pedido> obtenerPorCompradorId(String compradorId) {
        return dataRepository
                .findByCompradorIdOrderByFechaCreacionDesc(UUID.fromString(compradorId))
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public boolean existePedidoEntregado(String compradorId, String articuloId) {
        return dataRepository.existsByCompradorIdAndArticuloIdAndEstado(
                UUID.fromString(compradorId),
                UUID.fromString(articuloId),
                EstadoPedido.ENTREGADO);
    }



}
