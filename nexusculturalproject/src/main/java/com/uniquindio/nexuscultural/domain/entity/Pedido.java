package com.uniquindio.nexuscultural.domain.entity;

import com.uniquindio.nexuscultural.domain.exception.ReglaDominioException;
import com.uniquindio.nexuscultural.domain.valueobject.EstadoPedido;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class Pedido {

    private static final int PLAZO_DEVOLUCION_DIAS = 8; //A discutir con el grupo

    private final UUID id;
    private final UUID compradorId;
    private final UUID articuloId;
    private final int cantidad;
    private final LocalDateTime fechaCreacion;

    private EstadoPedido estado;
    private LocalDateTime fechaEntrega;

    private Pedido(UUID id, UUID compradorId, UUID articuloId, int cantidad,
                   LocalDateTime fechaCreacion) {
        this.id = id;
        this.compradorId = compradorId;
        this.articuloId = articuloId;
        this.cantidad = cantidad;
        this.fechaCreacion = fechaCreacion;
        this.estado = EstadoPedido.PENDIENTE;

    }

    public static Pedido crear(UUID compradorId, UUID articuloId, int cantidad,
                               LocalDateTime ahora) {
        if (compradorId == null || articuloId == null) {
            throw new ReglaDominioException("El pedido necesita comprador y artículo.");
        }
        if (cantidad <= 0) {
            throw new ReglaDominioException("La cantidad debe ser mayor a cero.");
        }
        return new Pedido(UUID.randomUUID(), compradorId, articuloId, cantidad, ahora);
    }



    private void verificarEstado(EstadoPedido estadoEsperado){
        if(estado != estadoEsperado){
            throw new ReglaDominioException("No fue posible proceder, el sistema necesita que el pedido esté en estado" + estadoEsperado+ " y actualmente se encuentra en estado " + estado);
        }
    }

    private void confirmarPedido (){
        verificarEstado(EstadoPedido.PENDIENTE);
        estado = EstadoPedido.CONFIRMADO;
    }

    private void enviarPedido(){
        verificarEstado(EstadoPedido.PENDIENTE);
        estado = EstadoPedido.EN_CAMINO;

    }

    private void marcarEntregado(){
        verificarEstado(EstadoPedido.EN_CAMINO);
        estado = EstadoPedido.ENTREGADO;
        fechaEntrega = LocalDateTime.now();
    }

    public Devolucion solicitarDevolucion(UUID solicitanteId, String motivo, LocalDateTime ahora) {
        if (!compradorId.equals(solicitanteId)) {
            throw new ReglaDominioException(
                    "Solo el comprador del pedido puede solicitar la devolución.");
        }
        if (estado != EstadoPedido.ENTREGADO) {
            throw new ReglaDominioException(
                    "Solo se puede solicitar la devolución de un pedido entregado.");
        }
        if (ahora.isAfter(fechaEntrega.plusDays(PLAZO_DEVOLUCION_DIAS))) {
            throw new ReglaDominioException(
                    "El plazo de devolución (" + PLAZO_DEVOLUCION_DIAS + " días) ya venció.");
        }
        estado = EstadoPedido.EN_DEVOLUCION;
        return Devolucion.crear(id, motivo, ahora);
    }

    private void exigirEstado(EstadoPedido esperado) {
        if (estado != esperado) {
            throw new ReglaDominioException(
                    "Transición inválida: el pedido está " + estado + " y debía estar " + esperado + ".");
        }
    }

    public UUID getId() { return id; }
    public UUID getCompradorId() { return compradorId; }
    public UUID getArticuloId() { return articuloId; }
    public int getCantidad() { return cantidad; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public LocalDateTime getFechaEntrega() { return fechaEntrega; }
    public EstadoPedido getEstado() { return estado; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pedido otro)) return false;
        return id.equals(otro.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }








}
