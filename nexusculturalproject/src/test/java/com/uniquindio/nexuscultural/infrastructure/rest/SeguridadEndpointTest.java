package com.uniquindio.nexuscultural.infrastructure.rest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SeguridadEndpointTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Punto 1.1: Peticion a ruta protegida sin token JWT responde 401 Unauthorized")
    void sinTokenDeberiaResponder401() throws Exception {
        mockMvc.perform(get("/api/pedidos/protegido"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Punto 1.2: Peticion con rol incorrecto responde 403 Forbidden")
    void conRolIncorrectoDeberiaResponder403() throws Exception {
        mockMvc.perform(get("/api/pedidos/protegido")
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_INVITADO"))))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Punto 1.3: Peticion con rol correcto en ruta protegida responde 200 OK")
    void conRolCorrectoDeberiaResponder200() throws Exception {
        mockMvc.perform(get("/api/pedidos/protegido")
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_COMPRADOR"))))
                .andExpect(status().isOk());
    }
}
