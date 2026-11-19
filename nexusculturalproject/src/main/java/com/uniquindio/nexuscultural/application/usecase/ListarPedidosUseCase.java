package com.uniquindio.nexuscultural.application.usecase;

import com.uniquindio.nexuscultural.domain.entity.Pedido;
import com.uniquindio.nexuscultural.domain.repository.PedidoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ListarPedidosUseCase {

    private final PedidoRepository pedidoRepository;

    public ListarPedidosUseCase(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    public List<Pedido> ejecutar(UUID compradorId) {
        return pedidoRepository.obtenerPorCompradorId(compradorId);
    }
}