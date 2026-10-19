package com.uniquindio.nexuscultural.domain.repository;

import java.util.List;
import java.util.Optional;
import com.uniquindio.nexuscultural.domain.entity.Pedido;

public interface PedidoRepository {
    Optional<Pedido> obtenerPorId(String id);
    void guardar(Pedido pedido);
    List<Pedido> obtenerPorCompradorId(String compradorId);
    boolean existePedidoEntregado(String compradorId, String articuloId);
}