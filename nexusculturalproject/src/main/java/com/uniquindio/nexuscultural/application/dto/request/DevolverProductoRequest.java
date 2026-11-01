package com.uniquindio.nexuscultural.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

// DTO inmutable que transporta los datos requeridos desde la capa externa
public record DevolverProductoRequest(
        @NotNull(message = "El ID del pedido es obligatorio")
        UUID idPedido,
        @NotNull(message = "El ID del comprador es obligatorio")
        UUID idComprador,
        @NotBlank(message = "El motivo de la devolución es obligatorio")
        String motivo,
        String descripcionEstado
) {}
