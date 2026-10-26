package com.uniquindio.nexuscultural.application.usecase;

import com.uniquindio.nexuscultural.application.dto.request.CancelarPedidoRequest;
import com.uniquindio.nexuscultural.domain.entity.Pedido;
import com.uniquindio.nexuscultural.domain.exception.RecursoNoEncontradoException;
import com.uniquindio.nexuscultural.domain.repository.PedidoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CancelarPedidoPendienteUseCase {

    private final PedidoRepository pedidoRepository;

    public CancelarPedidoPendienteUseCase(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    @Transactional
    public void ejecutar(CancelarPedidoRequest request) {
        Pedido pedido = pedidoRepository.obtenerPorId(request.idPedido())
                .orElseThrow(() -> new IllegalArgumentException("El pedido solicitado no existe"));

        if (!pedido.getCompradorId().equals(request.idComprador())) {
            throw new IllegalArgumentException("El comprador no es el propietario del pedido");
        }

        if (!pedido.getEstado().name().equals("PENDIENTE")) {
            throw new IllegalStateException("Un pedido completado no se puede cancelar");
        }
        pedido.cancelar(request.idComprador());
        pedidoRepository.guardar(pedido);
    }
}
