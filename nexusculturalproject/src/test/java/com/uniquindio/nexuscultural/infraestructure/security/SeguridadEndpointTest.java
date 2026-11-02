package com.uniquindio.nexuscultural.infraestructure.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uniquindio.nexuscultural.application.dto.request.AutenticarUsuarioRequest;
import com.uniquindio.nexuscultural.application.dto.request.RegistrarArtesanoRequest;
import com.uniquindio.nexuscultural.application.dto.request.RegistrarCompradorRequest;
import com.uniquindio.nexuscultural.application.dto.request.SolicitarOtpRequest;
import com.uniquindio.nexuscultural.application.dto.request.ValidarOtpRequest;
import com.uniquindio.nexuscultural.domain.repository.CodigoOtpRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class SeguridadEndpointTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtTokenProviderAdapter tokenProvider;

    @Autowired
    private CodigoOtpRepository codigoOtpRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // =========================================================================
    // PUNTO 1: PROBAR UN ENDPOINT CON SEGURIDAD (401, 403 y 200)
    // Ruta protegida: /api/pedidos/protegido (requiere rol COMPRADOR / ADMIN)
    // =========================================================================

    @Test
    @DisplayName("Punto 1.1: Petición a ruta protegida sin token responde 401 Unauthorized")
    void sinTokenEnRutaProtegidaDeberiaResponder401() throws Exception {
        mockMvc.perform(get("/api/pedidos/protegido"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Punto 1.2: Petición a ruta protegida con token de rol equivocado responde 403 Forbidden")
    void conTokenDeRolEquivocadoEnRutaProtegidaDeberiaResponder403() throws Exception {
        String tokenArtesano = tokenProvider
                .generarToken(UUID.randomUUID(), "artesano@test.com", "ARTESANO")
                .token();

        mockMvc.perform(get("/api/pedidos/protegido")
                        .header("Authorization", "Bearer " + tokenArtesano))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Punto 1.3: Petición a ruta protegida con token de rol correcto responde 200 OK")
    void conTokenDeRolCorrectoEnRutaProtegidaDeberiaResponder200() throws Exception {
        String tokenComprador = tokenProvider
                .generarToken(UUID.randomUUID(), "comprador@test.com", "COMPRADOR")
                .token();

        mockMvc.perform(get("/api/pedidos/protegido")
                        .header("Authorization", "Bearer " + tokenComprador))
                .andExpect(status().isOk());
    }

    // =========================================================================
    // PRUEBAS GENERALES DE SEGURIDAD Y ENDPOINTS (MAIN)
    // =========================================================================

    @Test
    @DisplayName("Petición sin token a ruta protegida responde 401")
    void sinTokenDeberiaResponder401() throws Exception {
        mockMvc.perform(get("/api/pedidos/mis-pedidos"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Petición con rol incorrecto responde 403")
    void conRolIncorrectoDeberiaResponder403() throws Exception {
        mockMvc.perform(get("/api/administradores/algo")
                        .with(user("comprador@test.com").roles("COMPRADOR")))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Petición con rol correcto no debe ser bloqueada por seguridad")
    void conRolCorrectoNoDeberiaSerBloqueado() throws Exception {
        mockMvc.perform(get("/api/compradores/algo")
                        .with(user("comprador@test.com").roles("COMPRADOR")))
                .andExpect(status().is(allOf(not(401), not(403))));
    }

    @Test
    @DisplayName("Petición con token válido de comprador no debe ser bloqueada por seguridad")
    void conTokenValidoDeCompradorNoDeberiaSerBloqueado() throws Exception {
        String token = tokenProvider
                .generarToken(UUID.randomUUID(), "comprador@test.com", "COMPRADOR")
                .token();

        mockMvc.perform(get("/api/compradores/algo")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().is(allOf(not(401), not(403))));
    }

    @Test
    @DisplayName("Petición con token de rol no autorizado a admin responde 403")
    void conTokenValidoDeRolIncorrectoDeberiaResponder403() throws Exception {
        String token = tokenProvider
                .generarToken(UUID.randomUUID(), "comprador@test.com", "COMPRADOR")
                .token();

        mockMvc.perform(get("/api/administradores/algo")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Petición con token inválido o malformado responde 401")
    void conTokenInvalidoDeberiaResponder401() throws Exception {
        mockMvc.perform(get("/api/compradores/algo")
                        .header("Authorization", "Bearer token-falso"))
                .andExpect(status().isUnauthorized());
    }

    // =========================================================================
    // PRUEBAS DE RUTAS PROTEGIDAS POR TOKEN (DOMINIO)
    // =========================================================================

    @Test
    @DisplayName("Petición POST a artículos sin token responde 401")
    void sinTokenEnArticulosDeberiaResponder401() throws Exception {
        mockMvc.perform(post("/api/articulos"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Petición POST a artículos con rol COMPRADOR responde 403")
    void conTokenDeRolEquivocadoEnArticulosDeberiaResponder403() throws Exception {
        String tokenComprador = tokenProvider
                .generarToken(UUID.randomUUID(), "comprador@test.com", "COMPRADOR")
                .token();

        mockMvc.perform(post("/api/articulos")
                        .header("Authorization", "Bearer " + tokenComprador))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Petición POST a devoluciones sin token responde 401")
    void sinTokenEnDevolucionesDeberiaResponder401() throws Exception {
        mockMvc.perform(post("/api/devoluciones"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Petición POST a devoluciones con rol ARTESANO responde 403")
    void artesanoEnDevolucionesDeberiaResponder403() throws Exception {
        String tokenArtesano = tokenProvider
                .generarToken(UUID.randomUUID(), "artesano@test.com", "ARTESANO")
                .token();

        mockMvc.perform(post("/api/devoluciones")
                        .header("Authorization", "Bearer " + tokenArtesano))
                .andExpect(status().isForbidden());
    }

    // =========================================================================
    // PRUEBAS DE FLUJO OTP (LOGIN SIN CONTRASEÑA)
    // =========================================================================

    @Test
    @DisplayName("Flujo completo de OTP: solicitar y verificar retorna token JWT")
    void flujoOtpCompleto() throws Exception {
        String email = "comprador@test.com";

        SolicitarOtpRequest solicitarRequest = new SolicitarOtpRequest(email);
        mockMvc.perform(post("/api/usuarios/solicitar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(solicitarRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exitoso", is(true)));

        String codigoGenerado = codigoOtpRepository.buscarUltimoPorEmail(email)
                .orElseThrow()
                .codigo();

        ValidarOtpRequest validarRequest = new ValidarOtpRequest(email, codigoGenerado);
        mockMvc.perform(post("/api/usuarios/verificar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validarRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", notNullValue()))
                .andExpect(jsonPath("$.tipo", is("Bearer")))
                .andExpect(jsonPath("$.rol", is("COMPRADOR")))
                .andExpect(jsonPath("$.expiracionEnSegundos", notNullValue()));
    }

    @Test
    @DisplayName("Autenticar usuario donde el OTP hace de contraseña retorna token JWT")
    void autenticarConOtpComoContrasena() throws Exception {
        String email = "artesano@test.com";

        SolicitarOtpRequest solicitarRequest = new SolicitarOtpRequest(email);
        mockMvc.perform(post("/api/usuarios/solicitar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(solicitarRequest)))
                .andExpect(status().isOk());

        String codigoGenerado = codigoOtpRepository.buscarUltimoPorEmail(email)
                .orElseThrow()
                .codigo();

        AutenticarUsuarioRequest authRequest = new AutenticarUsuarioRequest(email, codigoGenerado);
        mockMvc.perform(post("/api/usuarios/autenticar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", notNullValue()))
                .andExpect(jsonPath("$.rol", is("ARTESANO")));
    }

    // =========================================================================
    // REGISTRO DE USUARIOS SIN CONTRASEÑA
    // =========================================================================

    @Test
    @DisplayName("Registro de comprador sin contraseña responde 201 Created")
    void registroCompradorSinContrasena() throws Exception {
        RegistrarCompradorRequest request = new RegistrarCompradorRequest(
                "Carlos Gómez",
                "carlos.gomez@test.com",
                "Quindío",
                "Armenia",
                "Calle 15 # 10-20"
        );

        mockMvc.perform(post("/api/compradores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email", is("carlos.gomez@test.com")))
                .andExpect(jsonPath("$.rol", is("COMPRADOR")));
    }

    @Test
    @DisplayName("Registro de artesano sin contraseña responde 201 Created")
    void registroArtesanoSinContrasena() throws Exception {
        RegistrarArtesanoRequest request = new RegistrarArtesanoRequest(
                "María Tejedora",
                "maria.tejedora@test.com",
                8,
                List.of("Medalla Artesanal 2024"),
                "Quindío",
                "Salento",
                "Carrera 6 # 3-12"
        );

        mockMvc.perform(post("/api/artesanos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email", is("maria.tejedora@test.com")));
    }
}