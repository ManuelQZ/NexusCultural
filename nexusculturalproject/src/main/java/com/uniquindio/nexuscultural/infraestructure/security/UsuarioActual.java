package com.uniquindio.nexuscultural.infraestructure.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
import java.util.UUID;

/** Id del usuario que hizo la petición, tomado del token JWT (no del cuerpo de la petición). */
public final class UsuarioActual {

    private UsuarioActual() {
    }

    public static Optional<UUID> id() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getDetails() instanceof String texto) {
            try {
                return Optional.of(UUID.fromString(texto));
            } catch (IllegalArgumentException e) {
                return Optional.empty();
            }
        }
        return Optional.empty();
    }
}
