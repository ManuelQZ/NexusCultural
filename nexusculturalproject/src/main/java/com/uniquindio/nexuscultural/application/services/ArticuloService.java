package com.uniquindio.nexuscultural.application.services;

import com.uniquindio.nexuscultural.application.dto.request.ActualizarInventarioRequest;
import com.uniquindio.nexuscultural.application.dto.request.CrearArticuloRequest;
import com.uniquindio.nexuscultural.application.dto.request.ReponerStockRequest;
import com.uniquindio.nexuscultural.domain.entity.Articulo;
import com.uniquindio.nexuscultural.domain.exception.RecursoNoEncontradoException;
import com.uniquindio.nexuscultural.domain.exception.ReglaDominioException;
import com.uniquindio.nexuscultural.domain.repository.ArticuloRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional
public class ArticuloService {
    private final ArticuloRepository articuloRepository;

    public ArticuloService(ArticuloRepository articuloRepository) {
        this.articuloRepository = articuloRepository;
    }

    /**
     * Publica un nuevo artículo en el catálogo.
     * Valida moderación (título ≥ 5 chars, descripción ≥ 50 chars) antes de crear.
     */
    public Articulo publicarArticulo(CrearArticuloRequest request) {
        // Moderación: validaciones de contenido antes de crear
        if (request.titulo() == null || request.titulo().trim().length() < 5) {
            throw new ReglaDominioException("El título debe tener al menos 5 caracteres.");
        }
        if (request.descripcion() == null || request.descripcion().trim().length() < 50) {
            throw new ReglaDominioException("La descripción debe tener al menos 50 caracteres.");
        }

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
        Articulo guardado = articuloRepository.guardar(articulo);

        return guardado;
    }

    public Articulo actualizarInventario(ActualizarInventarioRequest request) {
        Articulo articulo = articuloRepository.buscarPorId(request.idArticulo())
                .orElseThrow(() -> new RecursoNoEncontradoException("Artículo no encontrado."));
        articulo.actualizarInventario(request.idArtesano(), request.cantidad(), request.motivo());
        return articuloRepository.guardar(articulo);
    }

    public Articulo reponerStock(UUID articuloId, ReponerStockRequest request) {
        Articulo articulo = articuloRepository.buscarPorId(articuloId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Artículo no encontrado."));
        articulo.reponerStock(request.stock());
        return articuloRepository.guardar(articulo);
    }

    public Articulo inactivarArticulo(UUID articuloId, UUID artesanoId) {
        Articulo articulo = articuloRepository.buscarPorId(articuloId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Artículo no encontrado."));
        if (!articulo.getArtesanoId().equals(artesanoId)) {
            throw new ReglaDominioException("Solo el artesano dueño puede inactivar el artículo.");
        }
        articulo.inactivar();
        return articuloRepository.guardar(articulo);
    }

    public Articulo reactivarArticulo(UUID articuloId, UUID artesanoId) {
        Articulo articulo = articuloRepository.buscarPorId(articuloId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Artículo no encontrado."));
        if (!articulo.getArtesanoId().equals(artesanoId)) {
            throw new ReglaDominioException("Solo el artesano dueño puede reactivar el artículo.");
        }
        articulo.reactivar();
        return articuloRepository.guardar(articulo);
    }

}
