package com.uniquindio.nexuscultural.domain.service;

import com.uniquindio.nexuscultural.domain.valueobject.Dimensiones;
import com.uniquindio.nexuscultural.domain.valueobject.Precio;

import java.math.BigDecimal;
import java.math.RoundingMode;

public interface CalculadoraCostoEnvio {
    Precio calcular(Dimensiones dimensionesUnitarias, int cantidad);
}


