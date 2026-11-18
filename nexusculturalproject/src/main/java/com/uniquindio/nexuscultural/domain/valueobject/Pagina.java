package com.uniquindio.nexuscultural.domain.valueobject;

import java.util.List;

/** Resultado paginado del dominio (no depende de Spring). */
public record Pagina<T>(List<T> contenido, long totalElementos, int totalPaginas, int pagina, int tamano) {
}