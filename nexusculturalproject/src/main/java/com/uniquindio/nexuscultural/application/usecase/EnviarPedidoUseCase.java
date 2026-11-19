package com.uniquindio.nexuscultural.application.usecase;

import com.uniquindio.nexuscultural.domain.entity.Pedido;
import com.uniquindio.nexuscultural.domain.exception.RecursoNoEncontradoException;
import com.uniquindio.nexuscultural.domain.repository.PedidoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class EnviarPedidoUseCase {

    private final PedidoRepository pedidoRepository;

    public EnviarPedidoUseCase(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    @Transactional
    public Pedido ejecutar(UUID idPedido) {
        Pedido pedido = pedidoRepository.obtenerPorId(idPedido)
                .orElseThrow(() -> new RecursoNoEncontradoException("El pedido no existe."));
        pedido.enviar();
        pedidoRepository.guardar(pedido);
        return pedido;
    }
}
