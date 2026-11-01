package com.uniquindio.nexuscultural.application.usecase;

import com.uniquindio.nexuscultural.domain.entity.Devolucion;
import com.uniquindio.nexuscultural.domain.exception.RecursoNoEncontradoException;
import com.uniquindio.nexuscultural.domain.repository.DevolucionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ObtenerDevolucionPorPedidoUseCase {
    private final DevolucionRepository devolucionRepository;

    public ObtenerDevolucionPorPedidoUseCase(DevolucionRepository devolucionRepository) {
        this.devolucionRepository = devolucionRepository;
    }

    @Transactional(readOnly = true)
    public Devolucion ejecutar(UUID pedidoId) {
        return devolucionRepository.obtenerPorPedidoId(pedidoId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "El pedido no tiene una devolución registrada"));
    }

}
