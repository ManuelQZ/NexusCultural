package com.uniquindio.nexuscultural.infraestructure.persistence.jpa;

import com.uniquindio.nexuscultural.domain.entity.Devolucion;
import com.uniquindio.nexuscultural.domain.entity.Pedido;
import com.uniquindio.nexuscultural.domain.repository.PedidoRepository;
import com.uniquindio.nexuscultural.domain.valueobject.EstadoPedido;
import com.uniquindio.nexuscultural.infraestructure.persistence.jpa.entity.DevolucionEntity;
import com.uniquindio.nexuscultural.infraestructure.persistence.jpa.entity.PedidoEntity;
import com.uniquindio.nexuscultural.infraestructure.persistence.jpa.mapper.DevolucionPersistenceMapper;
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
    private final DevolucionJpaDataRepository devolucionDataRepository;
    private final PedidoPersistenceMapper pedidoMapper;
    private final DevolucionPersistenceMapper devolucionMapper;

    @Override
    public Optional<Pedido> obtenerPorId(UUID id) {
        return dataRepository.findById(id).map(this::aDominio);
    }

    @Override
    @Transactional
    public void guardar(Pedido pedido) {
        PedidoEntity entity = dataRepository.findById(pedido.getId())
                .map(existente -> {
                    pedidoMapper.actualizarEntity(pedido, existente);
                    return existente;
                })
                .orElseGet(() -> pedidoMapper.toEntity(pedido));
        dataRepository.save(entity);

        Devolucion devolucion = pedido.getDevolucion();
        if (devolucion != null) {
            if (!pedido.getId().equals(devolucion.getPedidoId())) {
                throw new IllegalArgumentException("La devolución debe pertenecer al mismo pedido.");
            }
            DevolucionEntity devEntity = devolucionDataRepository.findById(devolucion.getId())
                    .map(existente -> {
                        devolucionMapper.actualizarEntity(devolucion, existente);
                        return existente;
                    })
                    .orElseGet(() -> devolucionMapper.toEntity(devolucion));
            devolucionDataRepository.save(devEntity);
        }
    }

    @Override
    public List<Pedido> obtenerPorCompradorId(UUID compradorId) {
        return dataRepository.findByCompradorIdOrderByFechaCreacionDesc(compradorId)
                .stream()
                .map(this::aDominio)
                .toList();
    }

    @Override
    public boolean existePedidoEntregado(UUID compradorId, UUID articuloId) {
        return dataRepository.existsByCompradorIdAndArticuloIdAndEstado(
                compradorId, articuloId, EstadoPedido.ENTREGADO);
    }

    private Pedido aDominio(PedidoEntity entity) {
        Devolucion devolucion = devolucionDataRepository.findByPedidoId(entity.getId())
                .map(devolucionMapper::toDomain)
                .orElse(null);
        return pedidoMapper.toDomain(entity, devolucion);
    }



}
