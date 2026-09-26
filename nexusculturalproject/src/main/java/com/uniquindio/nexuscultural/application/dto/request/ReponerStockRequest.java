package com.uniquindio.nexuscultural.application.dto.request;

import jakarta.validation.constraints.NotBlank;

// Mapea a Articulo.reponerStock()
public record ReponerStockRequest(
        @NotBlank(message = "Ingrese una cantidad mayor o igual a 1")
        int stock

) {
}
