package com.uniquindio.nexuscultural.application.usecase;

import com.uniquindio.nexuscultural.domain.entity.Articulo;
import com.uniquindio.nexuscultural.domain.exception.RecursoNoEncontradoException;
import com.uniquindio.nexuscultural.domain.exception.ReglaDominioException;
import com.uniquindio.nexuscultural.domain.repository.ArticuloRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ActualizarHistoriaArticuloUseCase {

    private final ArticuloRepository articuloRepository;

    public ActualizarHistoriaArticuloUseCase(ArticuloRepository articuloRepository) {
        this.articuloRepository = articuloRepository;
    }

    @Transactional
    public Articulo ejecutar(UUID articuloId, UUID artesanoId, String historia) {
        Articulo articulo = articuloRepository.buscarPorId(articuloId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Artículo no encontrado."));

        articulo.actualizarHistoria(artesanoId, historia);

        return articuloRepository.guardar(articulo);
    }
}
