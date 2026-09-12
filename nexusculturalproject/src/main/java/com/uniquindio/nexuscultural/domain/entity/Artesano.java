package com.uniquindio.nexuscultural.domain.entity;

import com.uniquindio.nexuscultural.domain.valueobject.EstadoArticulo;
import jakarta.validation.constraints.Email;
import lombok.Builder;
import lombok.Getter;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true) /*Ignora todos los campos, solo incluye los
atributos que se marquen con .Include*/

public class Artesano {

    @EqualsAndHashCode.Include
    private final UUID id; //ID único e irrepetible

    private String nombreCompleto;
    private Email email;
    private final LocalDateTime fechaRegistro;


}
