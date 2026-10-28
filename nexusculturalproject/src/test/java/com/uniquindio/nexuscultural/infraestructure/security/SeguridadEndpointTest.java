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

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
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
    // TRES PRUEBAS PRINCIPALES REQUERIDAS:
    // 1) Sin token (401)
    // 2) Token del rol equivocado (403)
    // 3) Token del rol correcto (200)
    // =========================================================================

    @Test
    @DisplayName("1. Petición a ruta protegida sin token debería responder 401 Unauthorized")
    void sinTokenDeberiaResponder401() throws Exception {
        // La ruta POST /api/articulos requiere autenticación y rol ARTESANO
        mockMvc.perform(post("/api/articulos"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("2. Petición con token del rol equivocado debería responder 403 Forbidden")
    void conTokenDeRolEquivocadoDeberiaResponder403() throws Exception {
        // Generamos un token válido para un COMPRADOR
        String tokenComprador = tokenProvider
                .generarToken(UUID.randomUUID(), "comprador@test.com", "COMPRADOR")
                .token();

        // El COMPRADOR intenta publicar un artículo (ruta solo permitida a ARTESANO)
        mockMvc.perform(post("/api/articulos")
                        .header("Authorization", "Bearer " + tokenComprador))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("3. Petición con token del rol correcto debería responder 200 OK")
    void conTokenDeRolCorrectoDeberiaResponder200() throws Exception {
        // Generamos un token válido para un ARTESANO
        String tokenArtesano = tokenProvider
                .generarToken(UUID.randomUUID(), "artesano@test.com", "ARTESANO")
                .token();

        // El ARTESANO publica un artículo exitosamente en su ruta autorizada
        mockMvc.perform(post("/api/articulos")
                        .header("Authorization", "Bearer " + tokenArtesano))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje", is("Artículo publicado exitosamente")));
    }

    // =========================================================================
    // PRUEBAS DE LA SEGUNDA RUTA PROTEGIDA (SOLO COMPRADOR SOLICITA REEMBOLSO):
    // =========================================================================

    @Test
    @DisplayName("Petición a ruta de devoluciones sin token responde 401")
    void sinTokenEnDevolucionesDeberiaResponder401() throws Exception {
        mockMvc.perform(post("/api/devoluciones"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Petición a ruta de devoluciones con rol ARTESANO (equivocado) responde 403")
    void artesanoEnDevolucionesDeberiaResponder403() throws Exception {
        String tokenArtesano = tokenProvider
                .generarToken(UUID.randomUUID(), "artesano@test.com", "ARTESANO")
                .token();

        // El ARTESANO intenta solicitar devolución (ruta solo permitida a COMPRADOR)
        mockMvc.perform(post("/api/devoluciones")
                        .header("Authorization", "Bearer " + tokenArtesano))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Petición a ruta de devoluciones con rol COMPRADOR (correcto) responde 200")
    void compradorEnDevolucionesDeberiaResponder200() throws Exception {
        String tokenComprador = tokenProvider
                .generarToken(UUID.randomUUID(), "comprador@test.com", "COMPRADOR")
                .token();

        mockMvc.perform(post("/api/devoluciones")
                        .header("Authorization", "Bearer " + tokenComprador))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje", is("Solicitud de reembolso registrada exitosamente")));
    }

    // =========================================================================
    // PRUEBA DE TOKEN INVÁLIDO
    // =========================================================================

    @Test
    @DisplayName("Petición con token malformado o falso responde 401")
    void conTokenInvalidoDeberiaResponder401() throws Exception {
        mockMvc.perform(post("/api/articulos")
                        .header("Authorization", "Bearer token-invalido-o-falso"))
                .andExpect(status().isUnauthorized());
    }

    // =========================================================================
    // PRUEBAS DE FLUJO OTP (LOGIN SIN CONTRASEÑA, EL OTP HACE DE CONTRASEÑA)
    // =========================================================================

    @Test
    @DisplayName("Flujo completo de OTP: solicitar y verificar retorna token")
    void flujoOtpCompleto() throws Exception {
        String email = "comprador@test.com";

        // 1. Solicitar código OTP al correo
        SolicitarOtpRequest solicitarRequest = new SolicitarOtpRequest(email);
        mockMvc.perform(post("/api/usuarios/solicitar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(solicitarRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exitoso", is(true)));

        // Obtener el código generado
        String codigoGenerado = codigoOtpRepository.buscarUltimoPorEmail(email)
                .orElseThrow()
                .codigo();

        // 2. Verificar código OTP y obtener token JWT
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
    @DisplayName("Autenticar usuario donde el OTP hace de contraseña retorna token")
    void autenticarConOtpComoContrasena() throws Exception {
        String email = "artesano@test.com";

        // 1. Solicitar OTP
        SolicitarOtpRequest solicitarRequest = new SolicitarOtpRequest(email);
        mockMvc.perform(post("/api/usuarios/solicitar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(solicitarRequest)))
                .andExpect(status().isOk());

        String codigoGenerado = codigoOtpRepository.buscarUltimoPorEmail(email)
                .orElseThrow()
                .codigo();

        // 2. Autenticar usando AutenticarUsuarioRequest (usuario + contrasena=OTP)
        AutenticarUsuarioRequest authRequest = new AutenticarUsuarioRequest(email, codigoGenerado);
        mockMvc.perform(post("/api/usuarios/autenticar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", notNullValue()))
                .andExpect(jsonPath("$.rol", is("ARTESANO")));
    }

    // =========================================================================
    // REGISTRO SIN CONTRASEÑAS (REGISTRO POR ROL: ARTESANO Y COMPRADOR)
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
