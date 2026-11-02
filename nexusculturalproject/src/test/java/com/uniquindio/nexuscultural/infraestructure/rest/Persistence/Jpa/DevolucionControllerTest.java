package com.uniquindio.nexuscultural.infraestructure.rest.Persistence.Jpa;

import com.uniquindio.nexuscultural.application.dto.response.DevolucionResponse;
import com.uniquindio.nexuscultural.application.usecase.DevolverProductoUseCase;
import com.uniquindio.nexuscultural.application.usecase.ObtenerDevolucionPorPedidoUseCase;
import com.uniquindio.nexuscultural.domain.entity.Devolucion;
import com.uniquindio.nexuscultural.domain.exception.RecursoNoEncontradoException;
import com.uniquindio.nexuscultural.domain.exception.ReglaDominioException;
import com.uniquindio.nexuscultural.domain.valueobject.EstadoDevolucion;
import com.uniquindio.nexuscultural.infraestructure.rest.DevolucionController;
import com.uniquindio.nexuscultural.infraestructure.rest.exception.GlobalExceptionHandler;
import com.uniquindio.nexuscultural.infraestructure.rest.mapper.DevolucionMapper;
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

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DevolucionController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class, GlobalExceptionHandler.class})
@TestPropertySource(properties = "jwt.secret=clave-de-prueba-de-al-menos-32-caracteres-xx")
class DevolucionControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockitoBean private DevolverProductoUseCase devolverProductoUseCase;
    @MockitoBean private ObtenerDevolucionPorPedidoUseCase obtenerDevolucionUseCase;
    @MockitoBean private DevolucionMapper devolucionMapper;

    private String cuerpo(UUID pedidoId, String motivo) {
        return """
                {
                  "idPedido": "%s",
                  "idComprador": "%s",
                  "motivo": "%s",
                  "descripcionEstado": "Llegó roto"
                }
                """.formatted(pedidoId, UUID.randomUUID(), motivo);
    }

    @Test
    @DisplayName("Solicitar devolución válida responde 201 (ya no es un mock)")
    void solicitarDeberiaResponder201() throws Exception {
        UUID pedidoId = UUID.randomUUID();
        Devolucion devolucion = Devolucion.reconstruir(UUID.randomUUID(), pedidoId,
                "Producto dañado", LocalDateTime.now(), EstadoDevolucion.EN_PROCESO);
        DevolucionResponse respuesta = new DevolucionResponse(devolucion.getId(), pedidoId,
                "Producto dañado", devolucion.getFechaSolicitud(), "EN_PROCESO");

        when(obtenerDevolucionUseCase.ejecutar(pedidoId)).thenReturn(devolucion);
        when(devolucionMapper.toResponse(devolucion)).thenReturn(respuesta);

        mockMvc.perform(post("/api/devoluciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpo(pedidoId, "Producto dañado"))
                        .with(user("comprador@test.com").roles("COMPRADOR")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.estado").value("EN_PROCESO"))
                .andExpect(jsonPath("$.motivo").value("Producto dañado"));

        verify(devolverProductoUseCase).ejecutar(any());
        verify(obtenerDevolucionUseCase).ejecutar(pedidoId);
    }

    @Test
    @DisplayName("Solicitar devolución sin motivo responde 400 y no invoca los casos de uso")
    void solicitarSinMotivoDeberiaResponder400() throws Exception {
        mockMvc.perform(post("/api/devoluciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpo(UUID.randomUUID(), ""))
                        .with(user("comprador@test.com").roles("COMPRADOR")))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(devolverProductoUseCase, obtenerDevolucionUseCase);
    }

    @Test
    @DisplayName("Regla de negocio rota responde 409 y no consulta la devolución")
    void solicitarConReglaRotaDeberiaResponder409() throws Exception {
        doThrow(new ReglaDominioException("El plazo de devolución (8 días) ya venció."))
                .when(devolverProductoUseCase).ejecutar(any());

        mockMvc.perform(post("/api/devoluciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpo(UUID.randomUUID(), "Producto dañado"))
                        .with(user("comprador@test.com").roles("COMPRADOR")))
                .andExpect(status().isConflict());

        verifyNoInteractions(obtenerDevolucionUseCase);
    }

    @Test
    @DisplayName("Si la devolución no quedó registrada responde 404")
    void devolucionNoRegistradaDeberiaResponder404() throws Exception {
        when(obtenerDevolucionUseCase.ejecutar(any()))
                .thenThrow(new RecursoNoEncontradoException("El pedido no tiene una devolución registrada"));

        mockMvc.perform(post("/api/devoluciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpo(UUID.randomUUID(), "Producto dañado"))
                        .with(user("comprador@test.com").roles("COMPRADOR")))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Un ARTESANO no puede solicitar devoluciones (403)")
    void artesanoNoPuedeSolicitar() throws Exception {
        mockMvc.perform(post("/api/devoluciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpo(UUID.randomUUID(), "Producto dañado"))
                        .with(user("artesano@test.com").roles("ARTESANO")))
                .andExpect(status().isForbidden());

        verifyNoInteractions(devolverProductoUseCase, obtenerDevolucionUseCase);
    }
}
