package com.uniquindio.nexuscultural.domain.repository;

import com.uniquindio.nexuscultural.domain.entity.Devolucion;
import com.uniquindio.nexuscultural.domain.valueobject.EstadoDevolucion;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DevolucionRepository {
    Optional<Devolucion> obtenerPorId(UUID id);

    Optional<Devolucion> obtenerPorPedidoId(UUID pedidoId);

    List<Devolucion> obtenerPorEstado(EstadoDevolucion estado);
}
