package com.uniquindio.nexuscultural.infraestructure.rest.mapper;

import com.uniquindio.nexuscultural.application.dto.response.ArticuloDetalleResponse;
import com.uniquindio.nexuscultural.application.dto.response.PaginaResponse;
import com.uniquindio.nexuscultural.domain.entity.Articulo;
import com.uniquindio.nexuscultural.domain.valueobject.EstadoArticulo;
import com.uniquindio.nexuscultural.domain.valueobject.Grupo;
import com.uniquindio.nexuscultural.domain.valueobject.Pagina;
import com.uniquindio.nexuscultural.domain.valueobject.Precio;
import com.uniquindio.nexuscultural.infraestructure.persistence.jpa.entity.ArticuloEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ArticuloMapper {

    public ArticuloDetalleResponse toResponse(Articulo domain) {
        if (domain == null) return null;

        return new ArticuloDetalleResponse(
                domain.getId() != null ? domain.getId().toString() : null,
                domain.getArtesanoId() != null ? domain.getArtesanoId().toString() : null,
                domain.getTitulo(),
                domain.getDescripcion(),
                domain.getPrecio() != null && domain.getPrecio().monto() != null
                        ? domain.getPrecio().monto().doubleValue() : 0.0,
                domain.getPrecio() != null ? domain.getPrecio().moneda() : "COP",
                domain.getStock(),
                domain.getEstado() != null ? domain.getEstado().name() : null,
                domain.getGrupo() != null ? domain.getGrupo().name() : null,
                domain.getImagenes(),
                domain.getFechaPublicacion()
        );
    }
    public PaginaResponse<ArticuloDetalleResponse> toPaginaResponse(Pagina<Articulo> pagina) {
        return new PaginaResponse<>(
                pagina.contenido().stream().map(this::toResponse).toList(),
                pagina.pagina(),
                pagina.tamano(),
                pagina.totalElementos(),
                pagina.totalPaginas());
    }
}
