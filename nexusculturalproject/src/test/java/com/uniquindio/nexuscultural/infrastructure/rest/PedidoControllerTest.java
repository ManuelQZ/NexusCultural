package com.uniquindio.nexuscultural.infrastructure.rest;

import com.uniquindio.nexuscultural.application.dto.response.PedidoDetalleResponse;
import com.uniquindio.nexuscultural.application.services.PedidoService;
import com.uniquindio.nexuscultural.application.usecase.CancelarPedidoPendienteUseCase;
import com.uniquindio.nexuscultural.application.usecase.CrearPedidoUseCase;
import com.uniquindio.nexuscultural.domain.entity.Pedido;
import com.uniquindio.nexuscultural.domain.exception.RecursoNoEncontradoException;
import com.uniquindio.nexuscultural.domain.exception.ReglaDominioException;
import com.uniquindio.nexuscultural.domain.repository.PedidoRepository;
import com.uniquindio.nexuscultural.domain.valueobject.Precio;
import com.uniquindio.nexuscultural.infraestructure.rest.PedidoController;
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
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(PedidoController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class, GlobalExceptionHandler.class})
@TestPropertySource(properties = "jwt.secret=clave-de-prueba-de-al-menos-32-caracteres-xx")
class PedidoControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockitoBean private CancelarPedidoPendienteUseCase cancelarPedidoUseCase;
    @MockitoBean private CrearPedidoUseCase crearPedidoUseCase;
    @MockitoBean private PedidoService pedidoService;
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

    // ---------- Confirmar pedido ----------

    private Pedido pedidoNuevo() {
        return Pedido.crear(UUID.randomUUID(), UUID.randomUUID(), 2,
                new Precio(new BigDecimal("15000"), "COP"), LocalDateTime.now());
    }

    private PedidoDetalleResponse respuesta(Pedido pedido, String estado) {
        return new PedidoDetalleResponse(pedido.getId(), pedido.getArticuloId(), pedido.getCompradorId(), 2,
                new BigDecimal("30000"), estado, pedido.getFechaCreacion(), null, null);
    }

    @Test
    @DisplayName("Confirmar pedido responde 200 con el pedido CONFIRMADO")
    void confirmarDeberiaResponder200() throws Exception {
        Pedido pedido = pedidoNuevo();
        when(pedidoService.confirmarPedido(pedido.getId())).thenReturn(pedido);
        when(pedidoMapper.toDetalleResponse(pedido)).thenReturn(respuesta(pedido, "CONFIRMADO"));

        mockMvc.perform(post("/api/pedidos/{id}/confirmar", pedido.getId())
                        .with(user("comprador@test.com").roles("COMPRADOR")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("CONFIRMADO"));
    }

    @Test
    @DisplayName("Confirmar pedido inexistente responde 404")
    void confirmarInexistenteDeberiaResponder404() throws Exception {
        UUID id = UUID.randomUUID();
        when(pedidoService.confirmarPedido(id)).thenThrow(new RecursoNoEncontradoException("Pedido no encontrado."));

        mockMvc.perform(post("/api/pedidos/{id}/confirmar", id)
                        .with(user("comprador@test.com").roles("COMPRADOR")))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Confirmar pedido que no está PENDIENTE responde 409")
    void confirmarEnEstadoInvalidoDeberiaResponder409() throws Exception {
        UUID id = UUID.randomUUID();
        when(pedidoService.confirmarPedido(id)).thenThrow(new ReglaDominioException("El pedido no está PENDIENTE"));

        mockMvc.perform(post("/api/pedidos/{id}/confirmar", id)
                        .with(user("comprador@test.com").roles("COMPRADOR")))
                .andExpect(status().isConflict());
    }

    // ---------- Procesar pago ----------

    private String cuerpoPago(String metodoPago) {
        return """
                {
                  "idPedido": "%s",
                  "idComprador": "%s",
                  "monto": { "monto": 30000, "moneda": "COP" },
                  "metodoPago": "%s",
                  "referenciaTransaccion": "REF-123"
                }
                """.formatted(UUID.randomUUID(), UUID.randomUUID(), metodoPago);
    }

    @Test
    @DisplayName("Procesar pago válido responde 200")
    void pagarDeberiaResponder200() throws Exception {
        Pedido pedido = pedidoNuevo();
        when(pedidoService.procesarPago(any())).thenReturn(pedido);
        when(pedidoMapper.toDetalleResponse(pedido)).thenReturn(respuesta(pedido, "CONFIRMADO"));

        mockMvc.perform(post("/api/pedidos/pago")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpoPago("TARJETA"))
                        .with(user("comprador@test.com").roles("COMPRADOR")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("CONFIRMADO"));
    }

    @Test
    @DisplayName("Procesar pago con regla de dominio rota responde 409")
    void pagarConReglaRotaDeberiaResponder409() throws Exception {
        when(pedidoService.procesarPago(any()))
                .thenThrow(new ReglaDominioException("El monto del pago no coincide con el total del pedido."));

        mockMvc.perform(post("/api/pedidos/pago")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpoPago("TARJETA"))
                        .with(user("comprador@test.com").roles("COMPRADOR")))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Procesar pago sin método de pago responde 400 y no invoca el servicio")
    void pagarSinMetodoDeberiaResponder400() throws Exception {
        mockMvc.perform(post("/api/pedidos/pago")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpoPago(""))
                        .with(user("comprador@test.com").roles("COMPRADOR")))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(pedidoService);
    }

    // ---------- Resolver devolución (solo ADMINISTRADOR) ----------

    @Test
    @DisplayName("Un COMPRADOR no puede resolver devoluciones (403)")
    void compradorNoPuedeResolverDevolucion() throws Exception {
        mockMvc.perform(patch("/api/pedidos/{id}/devolucion/resolver", UUID.randomUUID())
                        .param("aprobar", "true")
                        .with(user("comprador@test.com").roles("COMPRADOR")))
                .andExpect(status().isForbidden());

        verifyNoInteractions(pedidoService);
    }

    @Test
    @DisplayName("Un ADMINISTRADOR resuelve la devolución y responde 200")
    void administradorResuelveDevolucion() throws Exception {
        Pedido pedido = pedidoNuevo();
        when(pedidoService.resolverDevolucion(pedido.getId(), true)).thenReturn(pedido);
        when(pedidoMapper.toDetalleResponse(pedido)).thenReturn(respuesta(pedido, "DEVUELTO"));

        mockMvc.perform(patch("/api/pedidos/{id}/devolucion/resolver", pedido.getId())
                        .param("aprobar", "true")
                        .with(user("admin@test.com").roles("ADMINISTRADOR")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("DEVUELTO"));
    }
}
