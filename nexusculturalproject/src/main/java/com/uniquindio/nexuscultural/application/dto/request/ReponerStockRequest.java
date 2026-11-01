package com.uniquindio.nexuscultural.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

// Mapea a Articulo.reponerStock()
public record ReponerStockRequest(
        @Positive(message = "Ingrese una cantidad mayor o igual a 1")
        int stock

) {
}
