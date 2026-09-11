package com.uniquindio.nexuscultural.domain.valueobject;

import com.uniquindio.nexuscultural.domain.exception.ReglaDominioException;

public record Precio (double valor, string moneda) {

    public Precio {
     if (valor < 0) {
         throw new ReglaDominioException("El valor no puede ser menor a 0");
     }
    }

    public precio conGrupo(Licencia licencia) {

    }
}