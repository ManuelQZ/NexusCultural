package com.uniquindio.nexuscultural.infraestructure.persistence;

import java.util.*;

import com.uniquindio.nexuscultural.domain.entity.Pedido;
import com.uniquindio.nexuscultural.domain.repository.PedidoRepository;
import com.uniquindio.nexuscultural.domain.valueobject.EstadoPedido;

public class PedidoRepositoryEnMemoria implements PedidoRepository {

    private final Map<UUID, Pedido> pedidos = new HashMap<>();

    @Override
    public Optional<Pedido> obtenerPorId(UUID id) {
        return Optional.ofNullable(pedidos.get(id));
    }

    @Override
    public void guardar(Pedido pedido) {
        pedidos.put(pedido.getId(), pedido);
    }

    @Override
    public List<Pedido> obtenerPorCompradorId(UUID compradorId) {
        return pedidos.values().stream()
                .filter(p -> p.getCompradorId().equals(compradorId))
                .sorted(Comparator.comparing(Pedido::getFechaCreacion).reversed())
                .toList();
    }

    @Override
    public boolean existePedidoEntregado(UUID compradorId, UUID articuloId) {
        return pedidos.values().stream()
                .anyMatch(p -> p.getCompradorId().equals(compradorId)
                        && p.getArticuloId().equals(articuloId)
                        && p.getEstado() == EstadoPedido.ENTREGADO);
    }
}