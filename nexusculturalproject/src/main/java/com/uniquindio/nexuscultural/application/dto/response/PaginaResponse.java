package com.uniquindio.nexuscultural.application.dto.response;

import java.util.List;

public record PaginaResponse<T>(List<T> contenido,
                                int pagina,
                                int tamano,
                                long totalElementos,
                                int totalPaginas) {
}
