package com.uniquindio.nexuscultural.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.uniquindio.nexuscultural.domain.entity.Pedido;

public interface PedidoRepository {
    Optional<Pedido> obtenerPorId(UUID id);
    void guardar(Pedido pedido);
    List<Pedido> obtenerPorCompradorId(UUID compradorId);
    boolean existePedidoEntregado(UUID compradorId, UUID articuloId);
}