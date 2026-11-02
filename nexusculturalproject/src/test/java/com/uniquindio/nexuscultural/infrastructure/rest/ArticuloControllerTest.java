package com.uniquindio.nexuscultural.infrastructure.rest;

import com.uniquindio.nexuscultural.application.dto.response.ArticuloDetalleResponse;
import com.uniquindio.nexuscultural.application.services.ArticuloService;
import com.uniquindio.nexuscultural.application.usecase.ActualizarHistoriaArticuloUseCase;
import com.uniquindio.nexuscultural.application.usecase.ActualizarInventarioUseCase;
import com.uniquindio.nexuscultural.domain.entity.Articulo;
import com.uniquindio.nexuscultural.domain.exception.RecursoNoEncontradoException;
import com.uniquindio.nexuscultural.domain.exception.ReglaDominioException;
import com.uniquindio.nexuscultural.domain.valueobject.Grupo;
import com.uniquindio.nexuscultural.domain.valueobject.Precio;
import com.uniquindio.nexuscultural.infraestructure.rest.ArticuloController;
import com.uniquindio.nexuscultural.infraestructure.rest.exception.GlobalExceptionHandler;
import com.uniquindio.nexuscultural.infraestructure.rest.mapper.ArticuloMapper;
import com.uniquindio.nexuscultural.infraestructure.security.JwtAuthenticationFilter;
import com.uniquindio.nexuscultural.infraestructure.security.SecurityConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ArticuloController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class, GlobalExceptionHandler.class})
@TestPropertySource(properties = "jwt.secret=clave-de-prueba-de-al-menos-32-caracteres-xx")
class ArticuloControllerTest {

    private static final String DESCRIPCION =
            "Pieza artesanal elaborada a mano con maderas de la región, historia y tradición cultural.";

    @Autowired private MockMvc mockMvc;

    @MockitoBean private ArticuloService articuloService;
    @MockitoBean private ArticuloMapper articuloMapper;
    @MockitoBean private ActualizarHistoriaArticuloUseCase actualizarHistoriaUseCase;
    @MockitoBean private ActualizarInventarioUseCase actualizarInventarioUseCase;

    private String cuerpo(int stock, String imagenes) {
        return """
                {
                  "artesanoId": "%s",
                  "titulo": "Casa de muñecas",
                  "descripcion": "%s",
                  "stock": %d,
                  "precio": { "monto": 15000, "moneda": "COP" },
                  "grupo": "MADERA",
                  "imagenes": %s
                }
                """.formatted(UUID.randomUUID(), DESCRIPCION, stock, imagenes);
    }

    private Articulo articulo() {
        return Articulo.crear(UUID.randomUUID(), "Casa de muñecas", DESCRIPCION, 5, Grupo.MADERA,
                List.of("a.jpg", "b.jpg"), new Precio(new BigDecimal("15000"), "COP"), LocalDateTime.now());
    }

    private ArticuloDetalleResponse respuesta(Articulo a, int stock, String estado) {
        return new ArticuloDetalleResponse(a.getId().toString(), a.getArtesanoId().toString(),
                a.getTitulo(), a.getDescripcion(), 15000.0, "COP", stock, estado, "MADERA",
                a.getImagenes(), a.getFechaPublicacion());
    }

    @Test
    @DisplayName("Publicar artículo válido responde 201 (ya no es un mock)")
    void publicarDeberiaResponder201() throws Exception {
        Articulo articulo = articulo();
        when(articuloService.publicarArticulo(any())).thenReturn(articulo);
        when(articuloMapper.toResponse(articulo)).thenReturn(respuesta(articulo, 5, "DISPONIBLE"));

        mockMvc.perform(post("/api/articulos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpo(5, "[\"a.jpg\",\"b.jpg\"]"))
                        .with(user("artesano@test.com").roles("ARTESANO")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.estado").value("DISPONIBLE"))
                .andExpect(jsonPath("$.stock").value(5));

        verify(articuloService).publicarArticulo(any());
    }

    @Test
    @DisplayName("Publicar con una sola imagen responde 400 y no invoca el servicio")
    void publicarConUnaImagenDeberiaResponder400() throws Exception {
        mockMvc.perform(post("/api/articulos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpo(5, "[\"a.jpg\"]"))
                        .with(user("artesano@test.com").roles("ARTESANO")))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(articuloService);
    }

    @Test
    @DisplayName("Regla de negocio rota al publicar responde 409")
    void publicarConReglaRotaDeberiaResponder409() throws Exception {
        when(articuloService.publicarArticulo(any()))
                .thenThrow(new ReglaDominioException("El título debe tener al menos 5 caracteres."));

        mockMvc.perform(post("/api/articulos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpo(5, "[\"a.jpg\",\"b.jpg\"]"))
                        .with(user("artesano@test.com").roles("ARTESANO")))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Un COMPRADOR no puede publicar (403)")
    void compradorNoPuedePublicar() throws Exception {
        mockMvc.perform(post("/api/articulos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpo(5, "[\"a.jpg\",\"b.jpg\"]"))
                        .with(user("comprador@test.com").roles("COMPRADOR")))
                .andExpect(status().isForbidden());

        verifyNoInteractions(articuloService);
    }

    @Test
    @DisplayName("Reponer stock responde 200")
    void reponerStockDeberiaResponder200() throws Exception {
        Articulo articulo = articulo();
        when(articuloService.reponerStock(eq(articulo.getId()), any())).thenReturn(articulo);
        when(articuloMapper.toResponse(articulo)).thenReturn(respuesta(articulo, 15, "DISPONIBLE"));

        mockMvc.perform(patch("/api/articulos/{id}/reponer", articulo.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"stock\": 10}")
                        .with(user("artesano@test.com").roles("ARTESANO")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stock").value(15));
    }

    @Test
    @DisplayName("Reponer stock con cantidad 0 responde 400")
    void reponerStockConCeroDeberiaResponder400() throws Exception {
        mockMvc.perform(patch("/api/articulos/{id}/reponer", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"stock\": 0}")
                        .with(user("artesano@test.com").roles("ARTESANO")))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(articuloService);
    }

    @Test
    @DisplayName("Inactivar artículo inexistente responde 404")
    void inactivarInexistenteDeberiaResponder404() throws Exception {
        UUID id = UUID.randomUUID();
        UUID artesanoId = UUID.randomUUID();
        when(articuloService.inactivarArticulo(id, artesanoId))
                .thenThrow(new RecursoNoEncontradoException("Artículo no encontrado."));

        mockMvc.perform(patch("/api/articulos/{id}/inactivar", id)
                        .param("artesanoId", artesanoId.toString())
                        .with(user("artesano@test.com").roles("ARTESANO")))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Un COMPRADOR no puede inactivar artículos (403)")
    void compradorNoPuedeInactivar() throws Exception {
        mockMvc.perform(patch("/api/articulos/{id}/inactivar", UUID.randomUUID())
                        .param("artesanoId", UUID.randomUUID().toString())
                        .with(user("comprador@test.com").roles("COMPRADOR")))
                .andExpect(status().isForbidden());

        verifyNoInteractions(articuloService);
    }
    @Test
    @DisplayName("Actualizar inventario responde 204 e invoca el caso de uso con el id de la ruta")
    void actualizarInventarioDeberiaResponder204() throws Exception {
        UUID id = UUID.randomUUID();
        String cuerpo = """
            {
              "idArticulo": "%s",
              "idArtesano": "%s",
              "cantidad": 10,
              "motivo": "Conteo físico"
            }
            """.formatted(UUID.randomUUID(), UUID.randomUUID());

        mockMvc.perform(put("/api/articulos/{id}/inventario", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpo)
                        .with(user("artesano@test.com").roles("ARTESANO")))
                .andExpect(status().isNoContent());

        verify(actualizarInventarioUseCase).ejecutar(argThat(r -> r.idArticulo().equals(id)));
    }

    @Test
    @DisplayName("Actualizar historia responde 200")
    void actualizarHistoriaDeberiaResponder200() throws Exception {
        Articulo articulo = articulo();
        UUID artesanoId = articulo.getArtesanoId();
        when(actualizarHistoriaUseCase.ejecutar(eq(articulo.getId()), eq(artesanoId), any()))
                .thenReturn(articulo);
        when(articuloMapper.toResponse(articulo)).thenReturn(respuesta(articulo, 5, "DISPONIBLE"));

        mockMvc.perform(patch("/api/articulos/{id}/historia", articulo.getId())
                        .param("artesanoId", artesanoId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"historia\": \"Hecha por maestros ceramistas de la región.\"}")
                        .with(user("artesano@test.com").roles("ARTESANO")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("DISPONIBLE"));
    }
}
