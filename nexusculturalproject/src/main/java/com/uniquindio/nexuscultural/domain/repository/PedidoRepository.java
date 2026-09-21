package com.uniquindio.nexuscultural.domain.repository;

import java.util.Optional;
import com.uniquindio.nexuscultural.domain.entity.Pedido;

public interface PedidoRepository {
    Optional<Compra> obtenerPorId(String id);
    void guardar(Compra compra);



}