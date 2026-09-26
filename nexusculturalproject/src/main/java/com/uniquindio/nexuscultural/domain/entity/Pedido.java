package com.uniquindio.nexuscultural.domain.entity;

import com.uniquindio.nexuscultural.domain.exception.ReglaDominioException;
import com.uniquindio.nexuscultural.domain.valueobject.EstadoPedido;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class Pedido {

    private static final int PLAZO_DEVOLUCION_DIAS = 8; // A discutir con el grupo

    private final UUID id;
    private final UUID compradorId;
    private final UUID articuloId;
    private final int cantidad;
    private final LocalDateTime fechaCreacion;

    private EstadoPedido estado;
    private LocalDateTime fechaEntrega;
    private Devolucion devolucion; /*Solo existe para que no el pedido tenga una instancia de devolución
    en caso de que no se haga, siempre será null*/

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

    public void confirmar() {
        verificarEstado(EstadoPedido.PENDIENTE);
        estado = EstadoPedido.CONFIRMADO;
    }

    public void enviar() {
        verificarEstado(EstadoPedido.CONFIRMADO);
        estado = EstadoPedido.EN_CAMINO;
    }

    public void marcarEntregado(LocalDateTime ahora) {
        verificarEstado(EstadoPedido.EN_CAMINO);
        estado = EstadoPedido.ENTREGADO;
        fechaEntrega = ahora;
    }

    /**
     * Invariante 1 del agregado: un pedido no puede tener más de una
     * devolución activa al mismo tiempo.
     */
    public Devolucion solicitarDevolucion(UUID solicitanteId, String motivo, LocalDateTime ahora) {
        if (!compradorId.equals(solicitanteId)) {
            throw new ReglaDominioException("Solo el comprador del pedido puede solicitar la devolución.");
        }
        if (estado != EstadoPedido.ENTREGADO) {
            throw new ReglaDominioException("Solo se puede solicitar la devolución de un pedido entregado.");
        }
        if (devolucion != null) {
            throw new ReglaDominioException("Este pedido ya tiene una devolución en proceso.");
        }
        if (ahora.isAfter(fechaEntrega.plusDays(PLAZO_DEVOLUCION_DIAS))) {
            throw new ReglaDominioException("El plazo de devolución (" + PLAZO_DEVOLUCION_DIAS + " días) ya venció.");
        }
        devolucion = Devolucion.crear(id, motivo, ahora);
        estado = EstadoPedido.EN_DEVOLUCION;
        return devolucion;
    }

    /**
     * Invariante 2 del agregado: la Devolucion nunca se resuelve directamente;
     * siempre a través de la raíz, para que el estado del Pedido quede consistente
     * con el de su Devolucion.
     */
    public void resolverDevolucion(boolean aprobar) {
        if (devolucion == null) {
            throw new ReglaDominioException("Este pedido no tiene una devolución en proceso.");
        }
        if (aprobar) {
            devolucion.aprobar();
            estado = EstadoPedido.DEVUELTO;
        } else {
            devolucion.rechazar();
            estado = EstadoPedido.ENTREGADO; // vuelve al estado anterior a la solicitud
        }
    }

    private void verificarEstado(EstadoPedido estadoEsperado) {
        if (estado != estadoEsperado) {
            throw new ReglaDominioException("No fue posible proceder, el sistema necesita que el pedido esté en estado "
                    + estadoEsperado + " y actualmente se encuentra en estado " + estado + ".");
        }
    }

    public UUID getId() { return id; }
    public UUID getCompradorId() { return compradorId; }
    public UUID getArticuloId() { return articuloId; }
    public int getCantidad() { return cantidad; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public LocalDateTime getFechaEntrega() { return fechaEntrega; }
    public EstadoPedido getEstado() { return estado; }
    public Devolucion getDevolucion() { return devolucion; }

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
