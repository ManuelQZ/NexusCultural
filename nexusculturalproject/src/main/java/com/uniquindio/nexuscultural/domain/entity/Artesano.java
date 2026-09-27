package com.uniquindio.nexuscultural.domain.entity;

import com.uniquindio.nexuscultural.domain.exception.ReglaDominioException;
import com.uniquindio.nexuscultural.domain.valueobject.Email;
import com.uniquindio.nexuscultural.domain.valueobject.RolUsuario;
import com.uniquindio.nexuscultural.domain.valueobject.TrayectoriaCultural;
import com.uniquindio.nexuscultural.domain.valueobject.UbicacionGeografica;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class Artesano extends Usuario {

    private TrayectoriaCultural trayectoriaCultural;
    private UbicacionGeografica ubicacionGeografica;
    private boolean activo;

    @Builder
    public Artesano(UUID id,
                    String nombreCompleto,
                    Email email,
                    LocalDateTime fechaRegistro,
                    TrayectoriaCultural trayectoriaCultural,
                    UbicacionGeografica ubicacionGeografica,
                    Boolean activo) {
        super(id, nombreCompleto, email, RolUsuario.ARTESANO, fechaRegistro);
        this.trayectoriaCultural = trayectoriaCultural;
        this.ubicacionGeografica = ubicacionGeografica;
        this.activo = activo != null ? activo : true;
    }

    //Registro de un Artesano nuevo
    public static Artesano registrar(String nombreCompleto, Email email,
                                     TrayectoriaCultural trayectoria, UbicacionGeografica ubicacion) {
        if (trayectoria == null) {
            throw new ReglaDominioException("Regla Innegociable: El artesano debe registrar su trayectoria cultural para crear su ficha.");
        }
        if (ubicacion == null) {
            throw new ReglaDominioException("El artesano debe indicar su ubicación geográfica.");
        }
        return new Artesano(UUID.randomUUID(), nombreCompleto, email, LocalDateTime.now(), trayectoria, ubicacion, Boolean.TRUE);
    }

    //  Regla Innegociable 8: Artesano Maestro
    public boolean esArtesanoMaestro() {

        //Se debe implementar!
        //return this.Artesano.getEdad() >= 60;
        return false;
    }

    //  Regla Innegociable 7: Validación de Autocompra
    public void validarPuedeComprarArticulo(UUID artesanoIdPropietario) {
        verificarActivo();
        if (this.id.equals(artesanoIdPropietario)) {
            throw new ReglaDominioException("Regla Innegociable: Un artesano no puede comprar sus propios productos.");
        }
    }

    public void actualizarTrayectoria(TrayectoriaCultural nuevaTrayectoria) {
        if (nuevaTrayectoria == null) {
            throw new ReglaDominioException("La trayectoria cultural del artesano no puede ser nula.");
        }
        this.trayectoriaCultural = nuevaTrayectoria;
    }

    public void actualizarUbicacion(UbicacionGeografica nuevaUbicacion) {
        verificarActivo();
        if (nuevaUbicacion == null) {
            throw new ReglaDominioException("La ubicación geográfica no puede ser nula.");
        }
        this.ubicacionGeografica = nuevaUbicacion;
    }

    public void desactivarPerfil() {
        this.activo = false;
    }

    public void activarPerfil() {
        this.activo = true;
    }
}