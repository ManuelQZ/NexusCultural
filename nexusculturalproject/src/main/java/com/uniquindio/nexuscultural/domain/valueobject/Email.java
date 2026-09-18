package com.uniquindio.nexuscultural.domain.valueobject;

import com.uniquindio.nexuscultural.domain.exception.ReglaDominioException;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.regex.Pattern;

@Getter
@EqualsAndHashCode
public class Email {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    private final String valor;

    public Email(String valor) {
        if (valor == null || valor.isBlank()) {
            throw new ReglaDominioException("El correo electrónico no puede estar vacío.");
        }
        String emailLimpio = valor.trim();
        if (!EMAIL_PATTERN.matcher(emailLimpio).matches()) {
            throw new ReglaDominioException("El formato del correo electrónico no es válido: " + valor);
        }
        this.valor = emailLimpio;
    }

    @Override
    public String toString() {
        return valor;
    }
}