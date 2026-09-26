package com.uniquindio.nexuscultural.application.usecase;

import com.uniquindio.nexuscultural.application.dto.DevolverProductoRequest;
import com.uniquindio.nexuscultural.domain.entity.Pedido;
import com.uniquindio.nexuscultural.domain.repository.PedidoRepository;

import java.time.LocalDateTime;
import java.util.UUID;

public class DevolverProductoUseCase {

    private final PedidoRepository pedidoRepository;

    public DevolverProductoUseCase(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    public void ejecutar(DevolverProductoRequest request) {
        // 1. Buscar el pedido en el repositorio
        Pedido pedido = pedidoRepository.obtenerPorId(request.idPedido().toString())
                .orElseThrow(() -> new IllegalArgumentException("El pedido no existe."));

        // 2. Validar que el comprador que solicita la devolución sea el dueño del pedido
        if (!pedido.getCompradorId().equals(request.idComprador())) {
            throw new IllegalStateException("Solo el comprador original puede solicitar la devolución de este pedido.");
        }

        // 3. Validar que el motivo sea proporcionado
        if (request.motivo() == null || request.motivo().trim().isEmpty()) {
            throw new IllegalArgumentException("El motivo de la devolución es obligatorio.");
        }

        // 4. Validar que la descripción del estado sea proporcionada
        if (request.descripcionEstado() == null || request.descripcionEstado().trim().isEmpty()) {
            throw new IllegalArgumentException("La descripción del estado del producto es obligatoria.");
        }

        // 5. Delegar la regla de negocio al Agregado (Domain Rule)
        pedido.solicitarDevolucion(request.idComprador(), request.motivo(), LocalDateTime.now());

        // 6. Persistir el estado actualizado en el repositorio
        pedidoRepository.guardar(pedido);
    }
}
