package com.uniquindio.nexuscultural.application.usecase;

import com.uniquindio.nexuscultural.domain.entity.Pedido;
import com.uniquindio.nexuscultural.domain.exception.RecursoNoEncontradoException;
import com.uniquindio.nexuscultural.domain.repository.PedidoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
public class ConfirmarPedidoUseCase {
   /* private final PedidoRepository pedidoRepository;

    public ConfirmarPedidoUseCase(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    @Transactional
    public Pedido ejecutar(UUID pedidoId) {
        Pedido pedido = pedidoRepository.obtenerPorId(pedidoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("El pedido solicitado no existe"));

        // El agregado valida que el pedido esté PENDIENTE
        pedido.confirmar();

        pedidoRepository.guardar(pedido);
        return pedido;
    }*/
}
