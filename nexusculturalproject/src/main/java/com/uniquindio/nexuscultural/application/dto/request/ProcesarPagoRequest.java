package com.uniquindio.nexuscultural.application.dto.request;

import java.math.BigDecimal;
import java.util.UUID;

// DTO inmutable que transporta los datos requeridos desde la capa externa
public record ProcesarPagoRequest(
        UUID idPedido,
        UUID idComprador,
        BigDecimal monto,
        String metodoPago,
        String referenciaTransaccion
) {}
