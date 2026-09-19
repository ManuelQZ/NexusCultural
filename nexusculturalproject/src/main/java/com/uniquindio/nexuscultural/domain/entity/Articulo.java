package com.uniquindio.nexuscultural.domain.entity;

import com.uniquindio.nexuscultural.domain.exception.ReglaDominioException;
import com.uniquindio.nexuscultural.domain.valueobject.EstadoArticulo;
import com.uniquindio.nexuscultural.domain.valueobject.EstadoDevolucion;
import com.uniquindio.nexuscultural.domain.valueobject.Grupo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Articulo {

    private static final int MINIMO_IMAGENES = 2;

    private final UUID id;
    private final UUID artesanoId;          // referencia por ID, no por objeto
    private final Grupo grupo;
    private final LocalDateTime fechaPublicacion;
    private final List<String> imagenes;    // el dominio solo conoce referencias (URLs/keys)
    private String titulo;
    private String descripcion;
    private int stock;
    private EstadoArticulo estado;

    private Articulo(UUID id, UUID artesanoId, String titulo, String descripcion,
                     int stock, Grupo grupo, List<String> imagenes,
                     LocalDateTime fechaPublicacion) {
        this.id = id;
        this.artesanoId = artesanoId;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.stock = stock;
        this.grupo = grupo;
        this.imagenes = new ArrayList<>(imagenes);
        this.fechaPublicacion = fechaPublicacion;
        this.estado = EstadoArticulo.DISPONIBLE;
    }

    public UUID getId() { return id; }
    public UUID getArtesanoId() { return artesanoId; }
    public String getTitulo() { return titulo; }
    public String getDescripcion() { return descripcion; }
    public int getStock() { return stock; }
    public EstadoArticulo getEstado() { return estado; }
    public Grupo getGrupo() { return grupo; }
    public LocalDateTime getFechaPublicacion() { return fechaPublicacion; }
    public List<String> getImagenes() { return Collections.unmodifiableList(imagenes); }


    public static Articulo crear(UUID artesanoId, String titulo, String descripcion,
                                 int stockInicial, Grupo grupo, List<String> imagenes,
                                 LocalDateTime ahora) {
        if (artesanoId == null) {
            throw new ReglaDominioException("El artículo debe pertenecer a un artesano.");
        }
        if (titulo == null || titulo.isBlank()) {
            throw new ReglaDominioException("El artículo debe tener un título.");
        }
        if (grupo == null) {
            throw new ReglaDominioException("El artículo debe pertenecer a un grupo.");
        }
        if (stockInicial < 1) {
            throw new ReglaDominioException("Un artículo nuevo debe tener al menos 1 unidad en stock.");
        }
        if (imagenes == null || imagenes.size() < MINIMO_IMAGENES) {
            throw new ReglaDominioException(
                    "Un artículo debe tener mínimo " + MINIMO_IMAGENES + " imágenes.");
        }
        return new Articulo(UUID.randomUUID(), artesanoId, titulo, descripcion,
                stockInicial, grupo, imagenes, ahora);
    }

    /** Se llama al confirmar un pedido. Si llega a 0 el artículo queda AGOTADO. */
    public void reducirStock(int cantidad) {
        if (cantidad <= 0) {
            throw new ReglaDominioException("La cantidad debe ser mayor a cero.");
        }
        if (estado != EstadoArticulo.DISPONIBLE) {
            throw new ReglaDominioException("El artículo no está disponible para la venta.");
        }
        if (cantidad > stock) {
            throw new ReglaDominioException("Stock insuficiente: el stock no puede ser negativo.");
        }
        stock -= cantidad;
        if (stock == 0) {
            estado = EstadoArticulo.AGOTADO;
        }
    }

    public void reponerStock(int cantidad) {
        if (cantidad <= 0) {
            throw new ReglaDominioException("La cantidad a reponer debe ser mayor a cero.");
        }
        if (estado == EstadoArticulo.ELIMINADO) {
            throw new ReglaDominioException("No se puede reponer stock de un artículo eliminado.");
        }
        stock += cantidad;
        if (estado == EstadoArticulo.AGOTADO) {
            estado = EstadoArticulo.DISPONIBLE;
        }
    }

    /**
     * Soft delete. El dominio no consulta pedidos (no conoce la BD):
     * el caso de uso averigua si hay pedidos activos y se lo informa aquí.
     */
    public void eliminar(boolean sujetoADevolucion) {
        if (estado == EstadoArticulo.ELIMINADO) {
            throw new ReglaDominioException("El artículo ya fue eliminado.");
        }
        if (sujetoADevolucion) {
            throw new ReglaDominioException(
                    "No se puede eliminar un artículo con pedidos activos o sujetos a devolución.");
        }
        estado = EstadoArticulo.ELIMINADO;
    }

    public void inactivar(){
        estado = EstadoArticulo.INACTIVO;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Articulo otro)) return false;
        return id.equals(otro.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
