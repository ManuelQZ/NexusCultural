package com.uniquindio.nexuscultural.domain.valueobject;

import com.uniquindio.nexuscultural.domain.exception.ReglaDominioException;

import java.math.BigDecimal;

public record Dimensiones(BigDecimal pesoKg, BigDecimal volumenCm3) {

    public Dimensiones {
        if (pesoKg == null || pesoKg.signum() <= 0 || volumenCm3 == null || volumenCm3.signum() <= 0) {
            throw new ReglaDominioException("El peso y el volumen deben ser mayores a cero.");
        }
    }
}
