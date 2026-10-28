package com.uniquindio.nexuscultural.infrastructure.rest;

import com.uniquindio.nexuscultural.application.usecase.CancelarPedidoPendienteUseCase;
import com.uniquindio.nexuscultural.application.usecase.CrearPedidoUseCase;
import com.uniquindio.nexuscultural.domain.exception.RecursoNoEncontradoException;
import com.uniquindio.nexuscultural.domain.exception.ReglaDominioException;
import com.uniquindio.nexuscultural.domain.repository.PedidoRepository;
import com.uniquindio.nexuscultural.infraestructure.rest.exception.GlobalExceptionHandler;
import com.uniquindio.nexuscultural.infraestructure.rest.mapper.PedidoMapper;
import com.uniquindio.nexuscultural.infraestructure.security.JwtAuthenticationFilter;
import com.uniquindio.nexuscultural.infraestructure.security.SecurityConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PedidoController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class, GlobalExceptionHandler.class})
@TestPropertySource(properties = "jwt.secret=clave-de-prueba-de-al-menos-32-caracteres-xx")
class PedidoControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockitoBean private CancelarPedidoPendienteUseCase cancelarPedidoUseCase;
    @MockitoBean private CrearPedidoUseCase crearPedidoUseCase;
    @MockitoBean private PedidoMapper pedidoMapper;
    @MockitoBean private PedidoRepository pedidoRepository;

    @Test
    @DisplayName("Punto 4.1: Recurso no encontrado responde 404 y NUNCA guarda")
    void deberiaResponder404YNoGuardar() throws Exception {
        doThrow(new RecursoNoEncontradoException("El pedido solicitado no existe"))
                .when(cancelarPedidoUseCase).ejecutar(any());

        mockMvc.perform(post("/api/pedidos/cancelar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idPedido\":\"00000000-0000-0000-0000-000000000000\",\"idComprador\":\"11111111-1111-1111-1111-111111111111\"}")
                        .with(user("comprador@test.com").roles("COMPRADOR")))
                .andExpect(status().isNotFound());

        verify(pedidoRepository, never()).guardar(any());
    }

    @Test
    @DisplayName("Punto 4.2: Regla de negocio rota responde 409 y NUNCA guarda")
    void deberiaResponder409YNoGuardar() throws Exception {
        doThrow(new ReglaDominioException("Un pedido completado no se puede cancelar"))
                .when(cancelarPedidoUseCase).ejecutar(any());

        mockMvc.perform(post("/api/pedidos/cancelar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idPedido\":\"11111111-1111-1111-1111-111111111111\",\"idComprador\":\"22222222-2222-2222-2222-222222222222\"}")
                        .with(user("comprador@test.com").roles("COMPRADOR")))
                .andExpect(status().isConflict());

        verify(pedidoRepository, never()).guardar(any());
    }
}
