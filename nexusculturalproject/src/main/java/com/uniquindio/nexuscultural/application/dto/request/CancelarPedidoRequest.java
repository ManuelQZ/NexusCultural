package com.uniquindio.nexuscultural.application.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CancelarPedidoRequest(
        @NotNull(message = "El ID del pedido es obligatorio")
        UUID idPedido,

        @NotNull(message = "El ID del comprador es obligatorio")
        UUID idComprador
) {}
