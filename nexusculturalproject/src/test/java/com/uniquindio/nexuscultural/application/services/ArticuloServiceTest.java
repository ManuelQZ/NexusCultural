package com.uniquindio.nexuscultural.application.services;

import com.uniquindio.nexuscultural.application.dto.request.CrearArticuloRequest;
import com.uniquindio.nexuscultural.domain.entity.Articulo;
import com.uniquindio.nexuscultural.domain.exception.RecursoNoEncontradoException;
import com.uniquindio.nexuscultural.domain.exception.ReglaDominioException;
import com.uniquindio.nexuscultural.domain.repository.ArticuloRepository;
import com.uniquindio.nexuscultural.domain.valueobject.EstadoArticulo;
import com.uniquindio.nexuscultural.domain.valueobject.Grupo;
import com.uniquindio.nexuscultural.domain.valueobject.Precio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArticuloServiceTest {

    private static final String DESCRIPCION =
            "Pieza artesanal elaborada a mano con maderas de la región, historia y tradición cultural.";

    @Mock private ArticuloRepository articuloRepository;
    @InjectMocks private ArticuloService service;

    private CrearArticuloRequest request(String titulo, String descripcion) {
        return new CrearArticuloRequest(UUID.randomUUID(), titulo, descripcion, 5,
                new Precio(new BigDecimal("15000"), "COP"), Grupo.MADERA, List.of("a.jpg", "b.jpg"));
    }

    private Articulo articulo(UUID artesanoId) {
        return Articulo.crear(artesanoId, "Casa de muñecas", DESCRIPCION, 5, Grupo.MADERA,
                List.of("a.jpg", "b.jpg"), new Precio(new BigDecimal("15000"), "COP"), LocalDateTime.now());
    }

    @Test
    void publicarValidoDeberiaPersistirElArticulo() {
        when(articuloRepository.guardar(any(Articulo.class))).thenAnswer(inv -> inv.getArgument(0));

        Articulo resultado = service.publicarArticulo(request("Casa de muñecas", DESCRIPCION));

        assertEquals("Casa de muñecas", resultado.getTitulo());
        assertEquals(EstadoArticulo.DISPONIBLE, resultado.getEstado());
        assertEquals(5, resultado.getStock());
        verify(articuloRepository).guardar(any(Articulo.class));
    }

    @Test
    void publicarConTituloCortoDeberiaLanzarReglaDominioYNoGuardar() {
        assertThrows(ReglaDominioException.class,
                () -> service.publicarArticulo(request("Casa", DESCRIPCION)));

        verify(articuloRepository, never()).guardar(any());
    }

    @Test
    void publicarConDescripcionCortaDeberiaLanzarReglaDominioYNoGuardar() {
        assertThrows(ReglaDominioException.class,
                () -> service.publicarArticulo(request("Casa de muñecas", "Muy corta")));

        verify(articuloRepository, never()).guardar(any());
    }

    @Test
    void reponerStockDeberiaSumarYGuardar() {
        Articulo articulo = articulo(UUID.randomUUID());
        when(articuloRepository.buscarPorId(articulo.getId())).thenReturn(Optional.of(articulo));
        when(articuloRepository.guardar(articulo)).thenReturn(articulo);

        Articulo resultado = service.reponerStock(articulo.getId(),
                new com.uniquindio.nexuscultural.application.dto.request.ReponerStockRequest(10));

        assertEquals(15, resultado.getStock());
        verify(articuloRepository).guardar(articulo);
    }

    @Test
    void reponerStockDeArticuloInexistenteDeberiaLanzarRecursoNoEncontrado() {
        UUID id = UUID.randomUUID();
        when(articuloRepository.buscarPorId(id)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class,
                () -> service.reponerStock(id,
                        new com.uniquindio.nexuscultural.application.dto.request.ReponerStockRequest(10)));

        verify(articuloRepository, never()).guardar(any());
    }

    @Test
    void inactivarPorOtroArtesanoDeberiaLanzarReglaDominioYNoGuardar() {
        Articulo articulo = articulo(UUID.randomUUID());
        when(articuloRepository.buscarPorId(articulo.getId())).thenReturn(Optional.of(articulo));

        assertThrows(ReglaDominioException.class,
                () -> service.inactivarArticulo(articulo.getId(), UUID.randomUUID()));

        assertEquals(EstadoArticulo.DISPONIBLE, articulo.getEstado());
        verify(articuloRepository, never()).guardar(any());
    }

    @Test
    void inactivarYReactivarPorElDuenioDeberiaCambiarEstado() {
        UUID artesanoId = UUID.randomUUID();
        Articulo articulo = articulo(artesanoId);
        when(articuloRepository.buscarPorId(articulo.getId())).thenReturn(Optional.of(articulo));
        when(articuloRepository.guardar(articulo)).thenReturn(articulo);

        assertEquals(EstadoArticulo.INACTIVO, service.inactivarArticulo(articulo.getId(), artesanoId).getEstado());
        assertEquals(EstadoArticulo.DISPONIBLE, service.reactivarArticulo(articulo.getId(), artesanoId).getEstado());
    }
}
