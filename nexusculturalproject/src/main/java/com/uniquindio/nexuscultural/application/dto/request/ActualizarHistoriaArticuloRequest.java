package com.uniquindio.nexuscultural.application.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ActualizarHistoriaArticuloRequest(@NotBlank(message = "La historia del artículo es obligatoria")
                                                String historia) {
}
