package com.uniquindio.nexuscultural.application.usecase;

import com.uniquindio.nexuscultural.application.dto.request.ProcesarPagoRequest;
import com.uniquindio.nexuscultural.domain.entity.Pedido;
import com.uniquindio.nexuscultural.domain.repository.PedidoRepository;

public class ProcesarPagoPedidoUseCase {

    private final PedidoRepository pedidoRepository;

    public ProcesarPagoPedidoUseCase(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    public void ejecutar(ProcesarPagoRequest request) {
        // 1. Buscar el pedido en el repositorio
        Pedido pedido = pedidoRepository.obtenerPorId(request.idPedido().toString())
                .orElseThrow(() -> new IllegalArgumentException("El pedido no existe."));

        // 2. Validar que el comprador que realiza el pago sea el dueño del pedido
        if (!pedido.getCompradorId().equals(request.idComprador())) {
            throw new IllegalStateException("Solo el comprador original puede procesar el pago de este pedido.");
        }

        // 3. Validar que el monto del pago coincida con el total del pedido
        if (request.monto().compareTo(pedido.getTotal()) != 0) {
            throw new IllegalStateException("El monto del pago no coincide con el total del pedido.");
        }

        // 4. Validar que el método de pago sea válido
        if (request.metodoPago() == null || request.metodoPago().trim().isEmpty()) {
            throw new IllegalArgumentException("El método de pago es obligatorio.");
        }

        // 5. Validar que la referencia de transacción sea única y no nula
        if (request.referenciaTransaccion() == null || request.referenciaTransaccion().trim().isEmpty()) {
            throw new IllegalArgumentException("La referencia de transacción es obligatoria.");
        }

        // 6. Delegar la regla de negocio al Agregado (Domain Rule)
        pedido.procesarPago(request.monto(), request.metodoPago(), request.referenciaTransaccion());

        // 7. Persistir el estado actualizado en el repositorio
        pedidoRepository.guardar(pedido);
    }
}
