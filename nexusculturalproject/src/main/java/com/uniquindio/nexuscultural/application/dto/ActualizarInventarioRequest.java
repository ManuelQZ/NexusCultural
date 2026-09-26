package com.uniquindio.nexuscultural.application.dto;

import java.util.UUID;

// DTO inmutable que transporta los datos requeridos desde la capa externa
public record ActualizarInventarioRequest(
        UUID idArticulo,
        UUID idArtesano,
        int cantidad,
        String motivo
) {}
