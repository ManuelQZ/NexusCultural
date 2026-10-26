package com.uniquindio.nexuscultural.application.usecase;

import com.uniquindio.nexuscultural.application.dto.request.CancelarPedidoRequest;
import com.uniquindio.nexuscultural.domain.entity.Pedido;
import com.uniquindio.nexuscultural.domain.exception.RecursoNoEncontradoException;
import com.uniquindio.nexuscultural.domain.repository.PedidoRepository;

public class CancelarPedidoPendienteUseCase {

    private final PedidoRepository pedidoRepository;

    public CancelarPedidoPendienteUseCase(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    public void ejecutar(CancelarPedidoRequest request) {
        Pedido pedido = pedidoRepository.buscarPorId(request.idPedido())
                .orElseThrow(() -> new IllegalArgumentException("El pedido solicitado no existe"));

        if (!pedido.getCompradorId().equals(request.idComprador())) {
            throw new IllegalArgumentException("El comprador no es el propietario del pedido");
        }

        if (!pedido.getEstado().name().equals("PENDIENTE")) {
            throw new IllegalStateException("Un pedido completado no se puede cancelar");
        }

        pedidoRepository.guardar(pedido);
    }
}
