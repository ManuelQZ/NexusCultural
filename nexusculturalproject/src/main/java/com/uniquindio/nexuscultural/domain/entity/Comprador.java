package com.uniquindio.nexuscultural.domain.entity;

import com.uniquindio.nexuscultural.domain.valueobject.Email;
import com.uniquindio.nexuscultural.domain.valueobject.RolUsuario;
import com.uniquindio.nexuscultural.domain.valueobject.UbicacionGeografica;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class Comprador extends Usuario {

    private UbicacionGeografica direccionEntrega;

    @Builder
    public Comprador(UUID id,
                     String nombreCompleto,
                     Email email,
                     LocalDateTime fechaRegistro,
                     UbicacionGeografica direccionEntrega) {
        super(id, nombreCompleto, email, RolUsuario.COMPRADOR, fechaRegistro);
        this.direccionEntrega = direccionEntrega;
    }

    public void actualizarDireccionEntrega(UbicacionGeografica nuevaDireccion) {
        this.direccionEntrega = nuevaDireccion;
    }
}