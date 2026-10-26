package com.uniquindio.nexuscultural;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional // Revierte los cambios en la BD H2 al finalizar cada test
class PedidoIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Punto 3: Recorrido completo End-to-End sin Mocks (Controller -> UseCase -> Domain -> Repo -> H2)")
    void deberiaEjecutarFlujoCompletoSinMocks() throws Exception {
        String jsonBody = """
            {
                "idPedido": "11111111-1111-1111-1111-111111111111",
                "idComprador": "22222222-2222-2222-2222-222222222222"
            }
            """;

        mockMvc.perform(post("/api/pedidos/cancelar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody)
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_COMPRADOR"))))
                .andExpect(status().isOk());
    }
}
