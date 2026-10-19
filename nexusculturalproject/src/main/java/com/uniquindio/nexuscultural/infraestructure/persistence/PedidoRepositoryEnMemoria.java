package com.uniquindio.nexuscultural.infraestructure.persistence;

import java.util.*;

import com.uniquindio.nexuscultural.domain.entity.Pedido;
import com.uniquindio.nexuscultural.domain.repository.PedidoRepository;
import com.uniquindio.nexuscultural.domain.valueobject.EstadoPedido;

public class PedidoRepositoryEnMemoria implements PedidoRepository {

    private final Map<String, Pedido> pedidos = new HashMap<>();

    @Override
    public Optional<Pedido> obtenerPorId(String id) {
        return Optional.ofNullable(pedidos.get(id));
    }

    @Override
    public void guardar(Pedido pedido) {
        pedidos.put(pedido.getId().toString(), pedido);
    }

    @Override
    public List<Pedido> obtenerPorCompradorId(String compradorId) {
        return pedidos.values().stream()
                .filter(p -> p.getCompradorId().toString().equals(compradorId))
                .sorted(Comparator.comparing(Pedido::getFechaCreacion).reversed())
                .toList();
    }

    @Override
    public boolean existePedidoEntregado(String compradorId, String articuloId) {
        return pedidos.values().stream()
                .anyMatch(p -> p.getCompradorId().toString().equals(compradorId)
                        && p.getArticuloId().toString().equals(articuloId)
                        && p.getEstado() == EstadoPedido.ENTREGADO);
    }
}