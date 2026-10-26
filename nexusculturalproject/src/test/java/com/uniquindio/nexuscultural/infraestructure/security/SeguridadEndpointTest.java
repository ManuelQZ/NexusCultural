package com.uniquindio.nexuscultural.infraestructure.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc; // Spring Boot 4
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest

@AutoConfigureMockMvc
public class SeguridadEndpointTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtTokenProviderAdapter tokenProvider;

    @Test
    void sinTokenDeberiaResponder401() throws Exception {
        // Cae en anyRequest().authenticated()
        mockMvc.perform(get("/api/pedidos/mis-pedidos"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void conRolIncorrectoDeberiaResponder403() throws Exception {
        // /api/administradores/** exige ADMINISTRADOR
        mockMvc.perform(get("/api/administradores/algo")
                .with(user("comprador@test.com").roles("COMPRADOR")))
                .andExpect(status().isForbidden());
    }

    @Test
    void conRolCorrectoDeberiaResponder200() throws Exception {
        mockMvc.perform(get("/api/pedidos/protegido")
                        .with(user("comprador@test.com").roles("COMPRADOR")))
                .andExpect(status().isOk());
    }

    @Test
    void conRolCorrectoNoDeberiaSerBloqueado() throws Exception {
        // /api/compradores/** exige COMPRADOR o ADMINISTRADOR
        mockMvc.perform(get("/api/compradores/algo")
                        .with(user("comprador@test.com").roles("COMPRADOR")))
                .andExpect(status().is(allOf(not(401), not(403))));
    }

    @Test
    void conTokenValidoDeCompradorNoDeberiaSerBloqueado() throws Exception {
        String token = tokenProvider
                .generarToken(UUID.randomUUID(), "comprador@test.com", "COMPRADOR")
                .token(); // ajusta al nombre real del campo en AuthTokenResponse

        mockMvc.perform(get("/api/compradores/algo")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().is(allOf(not(401), not(403))));
    }

    @Test
    void conTokenValidoDeRolIncorrectoDeberiaResponder403() throws Exception {
        String token = tokenProvider
                .generarToken(UUID.randomUUID(), "comprador@test.com", "COMPRADOR")
                .token();

        mockMvc.perform(get("/api/administradores/algo")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void conTokenInvalidoDeberiaResponder401() throws Exception {
        mockMvc.perform(get("/api/compradores/algo")
                        .header("Authorization", "Bearer token-falso"))
                .andExpect(status().isUnauthorized());
    }

}
