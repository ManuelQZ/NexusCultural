package com.uniquindio.nexuscultural.infraestructure.rest;

import com.uniquindio.nexuscultural.application.dto.request.ActualizarHistoriaArticuloRequest;
import com.uniquindio.nexuscultural.application.dto.request.ActualizarInventarioRequest;
import com.uniquindio.nexuscultural.application.dto.request.CrearArticuloRequest;
import com.uniquindio.nexuscultural.application.dto.request.ReponerStockRequest;
import com.uniquindio.nexuscultural.application.dto.response.ArticuloDetalleResponse;
import com.uniquindio.nexuscultural.application.services.ArticuloService;
import com.uniquindio.nexuscultural.application.usecase.ActualizarHistoriaArticuloUseCase;
import com.uniquindio.nexuscultural.application.usecase.ActualizarInventarioUseCase;
import com.uniquindio.nexuscultural.domain.entity.Articulo;
import com.uniquindio.nexuscultural.infraestructure.rest.mapper.ArticuloMapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/articulos")
@Tag(name = "Artículos")
public class ArticuloController {
    private final ArticuloService articuloService;
    private final ArticuloMapper articuloMapper;
    private final ActualizarHistoriaArticuloUseCase actualizarHistoriaUseCase;
    private final ActualizarInventarioUseCase actualizarInventarioUseCase;

    public ArticuloController(ArticuloService articuloService, ArticuloMapper articuloMapper,
                              ActualizarHistoriaArticuloUseCase actualizarHistoriaArticuloUseCase,
                              ActualizarInventarioUseCase actualizarInventarioUseCase) {
        this.articuloService = articuloService;
        this.articuloMapper = articuloMapper;
        this.actualizarHistoriaUseCase = actualizarHistoriaArticuloUseCase;
        this.actualizarInventarioUseCase = actualizarInventarioUseCase;
    }

    /** POST /api/articulos — El artesano publica un nuevo artículo */
    @PostMapping
    public ResponseEntity<ArticuloDetalleResponse> publicar(@Valid @RequestBody CrearArticuloRequest request) {
        Articulo articulo = articuloService.publicarArticulo(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(articuloMapper.toResponse(articulo));
    }

    /** PUT /api/articulos/{id}/inventario — Ajuste exacto de inventario (caso de uso) */
    @PutMapping("/{id}/inventario")
    public ResponseEntity<Void> actualizarInventario(
            @PathVariable UUID id,
            @Valid @RequestBody ActualizarInventarioRequest request) {

        actualizarInventarioUseCase.ejecutar(new ActualizarInventarioRequest(
                id, request.idArtesano(), request.cantidad(), request.motivo()));
        return ResponseEntity.noContent().build();
    }

    /** PATCH /api/articulos/{id}/reponer — Aumenta el stock en una cantidad delta */
    @PatchMapping("/{id}/reponer")
    public ResponseEntity<ArticuloDetalleResponse> reponerStock(
            @PathVariable UUID id,
            @Valid @RequestBody ReponerStockRequest request) {

        Articulo articulo = articuloService.reponerStock(id, request);
        return ResponseEntity.ok(articuloMapper.toResponse(articulo));
    }

    /** PATCH /api/articulos/{id}/inactivar — Desactiva el artículo */
    @PatchMapping("/{id}/inactivar")
    public ResponseEntity<ArticuloDetalleResponse> inactivar(
            @PathVariable UUID id,
            @RequestParam UUID artesanoId) {

        Articulo articulo = articuloService.inactivarArticulo(id, artesanoId);
        return ResponseEntity.ok(articuloMapper.toResponse(articulo));
    }

    /** PATCH /api/articulos/{id}/reactivar — Vuelve a poner el artículo en venta */
    @PatchMapping("/{id}/reactivar")
    public ResponseEntity<ArticuloDetalleResponse> reactivar(
            @PathVariable UUID id,
            @RequestParam UUID artesanoId) {

        Articulo articulo = articuloService.reactivarArticulo(id, artesanoId);
        return ResponseEntity.ok(articuloMapper.toResponse(articulo));
    }


    /** PATCH /api/articulos/{id}/historia — El artesano dueño actualiza la historia del artículo */
    @PatchMapping("/{id}/historia")
    public ResponseEntity<ArticuloDetalleResponse> actualizarHistoria(
            @PathVariable UUID id,
            @RequestParam UUID artesanoId,
            @Valid @RequestBody ActualizarHistoriaArticuloRequest request) {

        Articulo articulo = actualizarHistoriaUseCase.ejecutar(id, artesanoId, request.historia());
        return ResponseEntity.ok(articuloMapper.toResponse(articulo));
    }
}
