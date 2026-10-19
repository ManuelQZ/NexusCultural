package com.uniquindio.nexuscultural.infraestructure.rest.mapper;

import com.uniquindio.nexuscultural.application.dto.response.PedidoDetalleResponse;
import com.uniquindio.nexuscultural.domain.entity.Pedido;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor

public class PedidoMapper {
    private final DevolucionMapper devolucionMapper;

    public PedidoDetalleResponse toDetalleResponse(Pedido pedido) {
        return new PedidoDetalleResponse(
                pedido.getId(),
                pedido.getArticuloId(),
                pedido.getCompradorId(),
                pedido.getCantidad(),
                pedido.getTotal().monto(),
                pedido.getEstado().toString(),
                pedido.getFechaCreacion(),
                pedido.getFechaEntrega(),
                pedido.getDevolucion() == null
                        ? null
                        : devolucionMapper.toResponse(pedido.getDevolucion())
        );
    }
}
