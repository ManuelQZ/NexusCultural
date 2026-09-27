package com.uniquindio.nexuscultural.domain.entity;

import com.uniquindio.nexuscultural.domain.exception.ReglaDominioException;
import com.uniquindio.nexuscultural.domain.valueobject.Email;
import com.uniquindio.nexuscultural.domain.valueobject.RolUsuario;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public abstract class Usuario {

    @EqualsAndHashCode.Include
    protected final UUID id;

    protected String nombreCompleto;
    protected Email email;
    protected RolUsuario rol;
    protected final LocalDateTime fechaRegistro;
    protected boolean activo;

    protected Usuario(UUID id, String nombreCompleto, Email email, RolUsuario rol, LocalDateTime fechaRegistro) {
        if (nombreCompleto == null || nombreCompleto.isBlank()) {
            throw new ReglaDominioException("El nombre del usuario no puede estar vacío.");
        }
        if (email == null) {
            throw new ReglaDominioException("El email del usuario es obligatorio.");
        }
        this.id = id != null ? id : UUID.randomUUID();
        this.nombreCompleto = nombreCompleto;
        this.email = email;
        this.rol = rol;
        this.fechaRegistro = fechaRegistro != null ? fechaRegistro : LocalDateTime.now();
        this.activo = true;
    }

    public void actualizarPerfil(String nuevoNombre, Email nuevoEmail) {
        verificarActivo();
        if (nuevoNombre == null || nuevoNombre.isBlank()) {
            throw new ReglaDominioException("El nuevo nombre no puede estar vacío.");
        }
        if (nuevoEmail == null) {
            throw new ReglaDominioException("El nuevo email no puede ser nulo.");
        }
        this.nombreCompleto = nuevoNombre;
        this.email = nuevoEmail;
    }

    public void desactivarCuenta() {
        if (!this.activo) {
            throw new ReglaDominioException("La cuenta ya se encuentra inactiva.");
        }
        this.activo = false;
    }

    public void activarCuenta() {
        if (this.activo) {
            throw new ReglaDominioException("La cuenta ya se encuentra activa.");
        }
        this.activo = true;
    }

    protected void verificarActivo() {
        if (!this.activo) {
            throw new ReglaDominioException("El usuario se encuentra inactivo y no puede realizar esta acción.");
        }
    }

    public boolean esAdministrador() {
        return RolUsuario.ADMIN.equals(this.rol);
    }

    public boolean esArtesano() {
        return RolUsuario.ARTESANO.equals(this.rol);
    }

    public boolean esComprador() {
        return RolUsuario.COMPRADOR.equals(this.rol);
    }
}