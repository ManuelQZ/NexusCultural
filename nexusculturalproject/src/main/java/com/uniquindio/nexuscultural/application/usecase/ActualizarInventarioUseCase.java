package com.uniquindio.nexuscultural.application.usecase;

import com.uniquindio.nexuscultural.application.dto.ActualizarInventarioRequest;
import com.uniquindio.nexuscultural.domain.entity.Articulo;
import com.uniquindio.nexuscultural.domain.exception.ReglaDominioException;

import java.util.UUID;

public class ActualizarInventarioUseCase {

    private final ArticuloRepository articuloRepository;

    public ActualizarInventarioUseCase(ArticuloRepository articuloRepository) {
        this.articuloRepository = articuloRepository;
    }

    public void ejecutar(ActualizarInventarioRequest request) {
        // 1. Buscar el artículo en el repositorio
        Articulo articulo = articuloRepository.buscarPorId(request.idArticulo())
                .orElseThrow(() -> new IllegalArgumentException("El artículo no existe."));

        // 2. Validar que el artesano que actualiza el inventario sea el dueño del artículo
        if (!articulo.getArtesanoId().equals(request.idArtesano())) {
            throw new IllegalStateException("Solo el artesano dueño del artículo puede actualizar su inventario.");
        }

        // 3. Validar que la cantidad sea válida (no negativa)
        if (request.cantidad() < 0) {
            throw new IllegalArgumentException("La cantidad a actualizar no puede ser negativa.");
        }

        // 4. Validar que el motivo sea proporcionado
        if (request.motivo() == null || request.motivo().trim().isEmpty()) {
            throw new IllegalArgumentException("El motivo de la actualización de inventario es obligatorio.");
        }

        // 5. Delegar la regla de negocio al Agregado (Domain Rule)
        articulo.actualizarStock(request.cantidad(), request.motivo());

        // 6. Persistir el estado actualizado en el repositorio
        articuloRepository.guardar(articulo);
    }
}
