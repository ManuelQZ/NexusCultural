package com.uniquindio.nexuscultural.application.dto.request;

import com.uniquindio.nexuscultural.domain.valueobject.Precio;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

// DTO inmutable que transporta los datos requeridos desde la capa externa
public record ProcesarPagoRequest(
        @NotNull(message = "El ID del pedido es obligatorio")
        UUID idPedido,
        @NotNull(message = "El ID del comprador es obligatorio")
        UUID idComprador,
        @NotNull(message = "El monto es obligatorio")
        Precio monto,
        @NotBlank(message = "El método de pago es obligatorio")
        String metodoPago,
        @NotBlank(message = "La referencia de transacción es obligatoria")
        String referenciaTransaccion
) {}
