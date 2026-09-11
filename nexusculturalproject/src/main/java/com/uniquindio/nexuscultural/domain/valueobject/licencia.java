package com.uniquindio.nexuscultural.domain.valueobject;

public enum licencia {
    PERSONAL (factorprecio 1),
    EMPRESARIAL (factorprecio 2),
    GRUPAL (factorprecio 3);

    private final int factorprecio;

    licencia(int factorprecio) {
        this.factorprecio = factorprecio;
    }

    public boolean permiteUsoEmpresarial() {
        return this == EMPRESARIAL || this == GRUPAL;
    }

    public int factorPrecio() {
        return factorprecio;
    }
}