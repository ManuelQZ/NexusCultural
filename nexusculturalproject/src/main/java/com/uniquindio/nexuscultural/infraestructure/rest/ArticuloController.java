package com.uniquindio.nexuscultural.infraestructure.rest;

import com.uniquindio.nexuscultural.application.dto.request.ActualizarHistoriaArticuloRequest;
import com.uniquindio.nexuscultural.application.dto.request.ActualizarInventarioRequest;
import com.uniquindio.nexuscultural.application.dto.request.AplicarDescuentoRequest;
import com.uniquindio.nexuscultural.application.dto.request.CrearArticuloRequest;
import com.uniquindio.nexuscultural.application.dto.request.ReponerStockRequest;
import com.uniquindio.nexuscultural.application.dto.response.ArticuloDetalleResponse;
import com.uniquindio.nexuscultural.application.dto.response.PaginaResponse;
import com.uniquindio.nexuscultural.application.services.ArticuloService;
import com.uniquindio.nexuscultural.application.usecase.ActualizarHistoriaArticuloUseCase;
import com.uniquindio.nexuscultural.application.usecase.BuscarArticulosUseCase;
import com.uniquindio.nexuscultural.domain.entity.Articulo;
import com.uniquindio.nexuscultural.domain.valueobject.Grupo;
import com.uniquindio.nexuscultural.infraestructure.rest.mapper.ArticuloMapper;
import com.uniquindio.nexuscultural.infraestructure.security.UsuarioActual;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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
    private final com.uniquindio.nexuscultural.application.usecase.ActualizarInventarioUseCase actualizarInventarioUseCase;
    private final BuscarArticulosUseCase buscarArticulosUseCase;

    public ArticuloController(ArticuloService articuloService,
                              ArticuloMapper articuloMapper,
                              @Autowired(required = false) ActualizarHistoriaArticuloUseCase actualizarHistoriaUseCase,
                              @Autowired(required = false) com.uniquindio.nexuscultural.application.usecase.ActualizarInventarioUseCase actualizarInventarioUseCase,
                              @Autowired(required = false) BuscarArticulosUseCase buscarArticulosUseCase) {
        this.articuloService = articuloService;
        this.articuloMapper = articuloMapper;
        this.actualizarHistoriaUseCase = actualizarHistoriaUseCase;
        this.actualizarInventarioUseCase = actualizarInventarioUseCase;
        this.buscarArticulosUseCase = buscarArticulosUseCase;
    }

    /** POST /api/articulos — El artesano publica un nuevo artículo */
    @PostMapping
    public ResponseEntity<ArticuloDetalleResponse> publicar(@Valid @RequestBody CrearArticuloRequest request) {
        UUID yo = UsuarioActual.id().orElse(request.artesanoId());
        CrearArticuloRequest reqEfectivo = new CrearArticuloRequest(
                yo, request.titulo(), request.descripcion(), request.stock(),
                request.precio(), request.grupo(), request.imagenes());
        Articulo articulo = articuloService.publicarArticulo(reqEfectivo);
        return ResponseEntity.status(HttpStatus.CREATED).body(articuloMapper.toResponse(articulo));
    }

    /** PUT /api/articulos/{id} — El artesano actualiza los datos generales de su obra */
    @PutMapping("/{id}")
    public ResponseEntity<ArticuloDetalleResponse> actualizar(
            @PathVariable UUID id,
            @Valid @RequestBody CrearArticuloRequest request) {
        Articulo articulo = articuloService.actualizarArticulo(id, request);
        return ResponseEntity.ok(articuloMapper.toResponse(articulo));
    }

    /** PUT /api/articulos/{id}/inventario — Ajuste exacto de inventario */
    @PutMapping("/{id}/inventario")
    public ResponseEntity<Void> actualizarInventario(
            @PathVariable UUID id,
            @Valid @RequestBody ActualizarInventarioRequest request) {
        UUID yo = UsuarioActual.id().orElse(request.idArtesano());
        ActualizarInventarioRequest reqEfectivo = new ActualizarInventarioRequest(
                id, yo, request.cantidad(), request.motivo());
        if (actualizarInventarioUseCase != null) {
            actualizarInventarioUseCase.ejecutar(reqEfectivo);
        } else {
            articuloService.actualizarInventario(reqEfectivo);
        }
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

    /** PATCH /api/articulos/{id}/inactivar — Desactiva el artículo (artesano o moderación) */
    @PatchMapping("/{id}/inactivar")
    public ResponseEntity<ArticuloDetalleResponse> inactivar(
            @PathVariable UUID id,
            @RequestParam(required = false) UUID artesanoId) {
        UUID yo = UsuarioActual.id().orElse(artesanoId);
        Articulo articulo = articuloService.inactivarArticulo(id, yo);
        return ResponseEntity.ok(articuloMapper.toResponse(articulo));
    }

    /** PATCH /api/articulos/{id}/reactivar — Vuelve a poner el artículo en venta */
    @PatchMapping("/{id}/reactivar")
    public ResponseEntity<ArticuloDetalleResponse> reactivar(
            @PathVariable UUID id,
            @RequestParam(required = false) UUID artesanoId) {
        UUID yo = UsuarioActual.id().orElse(artesanoId);
        Articulo articulo = articuloService.reactivarArticulo(id, yo);
        return ResponseEntity.ok(articuloMapper.toResponse(articulo));
    }

    /** PATCH /api/articulos/{id}/historia — El artesano dueño actualiza la historia del artículo */
    @PatchMapping("/{id}/historia")
    public ResponseEntity<ArticuloDetalleResponse> actualizarHistoria(
            @PathVariable UUID id,
            @RequestParam(required = false) UUID artesanoId,
            @Valid @RequestBody ActualizarHistoriaArticuloRequest request) {
        UUID yo = UsuarioActual.id().orElse(artesanoId);
        if (actualizarHistoriaUseCase != null) {
            Articulo articulo = actualizarHistoriaUseCase.ejecutar(id, yo, request.historia());
            return ResponseEntity.ok(articuloMapper.toResponse(articulo));
        }
        return ResponseEntity.notFound().build();
    }

    /** POST /api/articulos/{id}/descuento — Aplica un descuento al artículo */
    @PostMapping("/{id}/descuento")
    public ResponseEntity<ArticuloDetalleResponse> aplicarDescuento(
            @PathVariable UUID id,
            @Valid @RequestBody AplicarDescuentoRequest request) {
        Articulo articulo = articuloService.aplicarDescuento(id, request);
        return ResponseEntity.ok(articuloMapper.toResponse(articulo));
    }

    /** DELETE /api/articulos/{id}/descuento — Remueve el descuento del artículo */
    @DeleteMapping("/{id}/descuento")
    public ResponseEntity<ArticuloDetalleResponse> removerDescuento(
            @PathVariable UUID id,
            @RequestParam UUID artesanoId) {
        Articulo articulo = articuloService.removerDescuento(id, artesanoId);
        return ResponseEntity.ok(articuloMapper.toResponse(articulo));
    }

    /** DELETE /api/articulos/{id} — El artesano elimina su artículo del catálogo */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable UUID id,
            @RequestParam(required = false) UUID artesanoId) {
        UUID yo = UsuarioActual.id().orElse(artesanoId);
        articuloService.eliminarArticulo(id, yo);
        return ResponseEntity.noContent().build();
    }

    /** GET /api/articulos?texto=&grupo=&pagina=0&tamano=10 — Catálogo paginado (público) */
    @GetMapping
    public ResponseEntity<?> listar(
            @RequestParam(required = false) String texto,
            @RequestParam(required = false) Grupo grupo,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamano) {
        if (buscarArticulosUseCase != null) {
            return ResponseEntity.ok(articuloMapper.toPaginaResponse(
                    buscarArticulosUseCase.ejecutar(texto, grupo, pagina, tamano)));
        }
        return ResponseEntity.ok(articuloService.publicarArticulo(null));
    }
}
