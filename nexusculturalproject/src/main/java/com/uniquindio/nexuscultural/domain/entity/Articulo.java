package com.uniquindio.nexuscultural.domain.entity;

import com.uniquindio.nexuscultural.domain.valueobject.EstadoArticulo;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;


@Getter
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true) /*Ignora todos los campos, solo incluye los
atributos que se marquen con .Include*/

public class Articulo {

    @EqualsAndHashCode.Include
    private final UUID id;

    private String titulo;
    private int Stock;
    private String descripcion;
    private EstadoArticulo estado;
    private final LocalDateTime fechaPublicacion;

    public void inactivar(){
        this.estado = EstadoArticulo.INACTIVO;
    }


}
