package com.uniquindio.nexuscultural.infraestructure.rest.mapper;

import com.uniquindio.nexuscultural.application.dto.response.ArticuloDetalleResponse;
import com.uniquindio.nexuscultural.application.dto.response.PaginaResponse;
import com.uniquindio.nexuscultural.domain.entity.Articulo;
import com.uniquindio.nexuscultural.domain.valueobject.Pagina;
import com.uniquindio.nexuscultural.infraestructure.persistence.jpa.ArtesanoJpaDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ArticuloMapper {

    private final ArtesanoJpaDataRepository artesanoRepository;

    public ArticuloMapper(@Autowired(required = false) ArtesanoJpaDataRepository artesanoRepository) {
        this.artesanoRepository = artesanoRepository;
    }

    public ArticuloDetalleResponse toResponse(Articulo domain) {
        if (domain == null) return null;

        String departamento = null;
        String municipio = null;
        if (domain.getArtesanoId() != null && artesanoRepository != null) {
            try {
                var artesanoOpt = artesanoRepository.findById(domain.getArtesanoId());
                if (artesanoOpt.isPresent()) {
                    departamento = artesanoOpt.get().getDepartamento();
                    municipio = artesanoOpt.get().getMunicipio();
                }
            } catch (Exception ignored) {
            }
        }

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
                domain.getFechaPublicacion(),
                domain.getPrecioConDescuento() != null && domain.getPrecioConDescuento().monto() != null
                        ? domain.getPrecioConDescuento().monto().doubleValue() : null,
                domain.getDescuento() != null ? domain.getDescuento().porcentaje() : null,
                departamento,
                municipio
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
