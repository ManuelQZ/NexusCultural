package com.uniquindio.nexuscultural.domain.valueobject;

import java.util.List;

public record Pagina<T>(List<T> contenido,
                        long totalElementos,
                        int totalPaginas,
                        int pagina,
                        int tamano) {
}
