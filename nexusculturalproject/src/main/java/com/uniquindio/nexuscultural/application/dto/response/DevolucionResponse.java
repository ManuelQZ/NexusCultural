package com.uniquindio.nexuscultural.application.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record DevolucionResponse(UUID id,
                                 UUID pedidoId,
                                 String motivo,
                                 LocalDateTime fechaSolicitud,
                                 String estado) {
}
