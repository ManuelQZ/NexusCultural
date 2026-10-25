package com.uniquindio.nexuscultural.domain.service;

import com.uniquindio.nexuscultural.domain.valueobject.Dimensiones;
import com.uniquindio.nexuscultural.domain.valueobject.Precio;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class CalculadoraDimensiones {
    public class CalculadoraPesoVolumen implements CalculadoraCostoEnvio {
        private static final BigDecimal DIVISOR_VOLUMETRICO = BigDecimal.valueOf(5000); // cm³ por kg
        private final BigDecimal tarifaPorKg;
        private final String moneda;

        public CalculadoraPesoVolumen(BigDecimal tarifaPorKg, String moneda) {
            this.tarifaPorKg = tarifaPorKg;
            this.moneda = moneda;
        }

        @Override
        public Precio calcular(Dimensiones d, int cantidad) {
            BigDecimal pesoVolumetrico = d.volumenCm3().divide(DIVISOR_VOLUMETRICO, 3, RoundingMode.HALF_UP);
            BigDecimal pesoCobrable = d.pesoKg().max(pesoVolumetrico); // se cobra el mayor
            BigDecimal costo = pesoCobrable
                    .multiply(BigDecimal.valueOf(cantidad))
                    .multiply(tarifaPorKg)
                    .setScale(0, RoundingMode.HALF_UP);
            return new Precio(costo, moneda);
        }
    }
}
