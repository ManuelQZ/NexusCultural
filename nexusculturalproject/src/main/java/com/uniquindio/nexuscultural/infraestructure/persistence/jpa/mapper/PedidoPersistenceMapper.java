package com.uniquindio.nexuscultural.infraestructure.persistence.jpa.mapper;

import com.uniquindio.nexuscultural.domain.entity.Devolucion;
import com.uniquindio.nexuscultural.domain.entity.Pedido;
import com.uniquindio.nexuscultural.domain.valueobject.Precio;
import com.uniquindio.nexuscultural.infraestructure.persistence.jpa.entity.PedidoEntity;
import com.uniquindio.nexuscultural.infraestructure.persistence.jpa.entity.PrecioEmbeddable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PedidoPersistenceMapper {
    private final DevolucionPersistenceMapper devolucionMapper;

    public PedidoEntity toEntity(Pedido pedido) {
        PedidoEntity entity = new PedidoEntity();
        entity.setId(pedido.getId());
        entity.setCompradorId(pedido.getCompradorId());
        entity.setArticuloId(pedido.getArticuloId());
        entity.setCantidad(pedido.getCantidad());
        entity.setTotal(new PrecioEmbeddable(
                pedido.getTotal().monto(), pedido.getTotal().moneda()));
        entity.setEstado(pedido.getEstado());
        entity.setFechaCreacion(pedido.getFechaCreacion());
        entity.setFechaEntrega(pedido.getFechaEntrega());
        entity.setMetodoPago(pedido.getMetodoPago());
        entity.setReferenciaTransaccion(pedido.getReferenciaTransaccion());
        return entity;
    }

    public void actualizarEntity(Pedido pedido, PedidoEntity entity) {
        if (!pedido.getId().equals(entity.getId())) {
            throw new IllegalArgumentException("El ID del pedido no coincide con la entidad.");
        }
        entity.setEstado(pedido.getEstado());
        entity.setFechaEntrega(pedido.getFechaEntrega());
        entity.setMetodoPago(pedido.getMetodoPago());
        entity.setReferenciaTransaccion(pedido.getReferenciaTransaccion());
    }

    public Pedido toDomain(PedidoEntity entity, Devolucion devolucion) {
        return Pedido.reconstruir(
                entity.getId(), entity.getCompradorId(), entity.getArticuloId(),
                entity.getCantidad(),
                new Precio(entity.getTotal().getMonto(), entity.getTotal().getMoneda()),
                entity.getFechaCreacion(), entity.getEstado(), entity.getFechaEntrega(),
                entity.getMetodoPago(), entity.getReferenciaTransaccion(),
                devolucion);
    }

}
