package com.uniquindio.nexuscultural.application.dto.request;

import com.uniquindio.nexuscultural.domain.valueobject.Precio;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record CrearPedidoRequest(
        @NotNull(message = "El ID del comprador es obligatorio")
        UUID compradorId,

        @NotNull(message = "El ID del artículo es obligatorio")
        UUID articuloId,

        @Positive(message = "La cantidad debe ser mayor a cero")
        int cantidad,

        @NotNull(message = "El precio unitario es obligatorio")
        Precio precioUnitario
) {}
