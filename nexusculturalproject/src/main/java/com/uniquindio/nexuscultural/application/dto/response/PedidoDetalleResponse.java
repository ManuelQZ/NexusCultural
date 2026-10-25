package com.uniquindio.nexuscultural.application.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record PedidoDetalleResponse(UUID id,
                                    UUID articuloId,
                                    UUID compradorId,
                                    int cantidad,
                                    BigDecimal total,
                                    String estado,
                                    LocalDateTime fechaCreacion,
                                    LocalDateTime fechaEntrega,
                                    DevolucionResponse devolucion
) {
}
