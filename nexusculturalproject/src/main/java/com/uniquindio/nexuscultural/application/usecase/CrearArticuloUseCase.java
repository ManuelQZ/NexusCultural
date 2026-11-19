package com.uniquindio.nexuscultural.application.usecase;

import com.uniquindio.nexuscultural.application.dto.request.CrearArticuloRequest;
import com.uniquindio.nexuscultural.domain.entity.Articulo;
import com.uniquindio.nexuscultural.domain.repository.ArticuloRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CrearArticuloUseCase {

    private final ArticuloRepository articuloRepository;

    public CrearArticuloUseCase(ArticuloRepository articuloRepository) {
        this.articuloRepository = articuloRepository;
    }

    public Articulo ejecutar(CrearArticuloRequest request) {
        Articulo articulo = Articulo.crear(
                request.artesanoId(),
                request.titulo(),
                request.descripcion(),
                request.stock(),
                request.grupo(),
                request.imagenes(),
                request.precio(),
                LocalDateTime.now()
        );
        return articuloRepository.guardar(articulo);
    }
}
