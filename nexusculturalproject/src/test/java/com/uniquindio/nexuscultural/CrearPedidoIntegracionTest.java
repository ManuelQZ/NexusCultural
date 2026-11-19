package com.uniquindio.nexuscultural;

import com.uniquindio.nexuscultural.domain.entity.Articulo;
import com.uniquindio.nexuscultural.domain.repository.ArticuloRepository;
import com.uniquindio.nexuscultural.domain.valueobject.Grupo;
import com.uniquindio.nexuscultural.domain.valueobject.Precio;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional // Revierte los cambios en la BD H2 al finalizar cada test
class CrearPedidoIntegracionTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ArticuloRepository articuloRepository;

    @Test
    void deberiaCrearUnPedidoYQuedarPendiente() throws Exception {
        UUID compradorId = UUID.randomUUID();

        // Arrange: el artículo ya existe porque un artesano lo publicó
        Articulo articulo = articuloRepository.guardar(Articulo.crear(
                UUID.randomUUID(), "Casa de muñecas", "Artesanal", 10,
                Grupo.MADERA,
                List.of("img1.jpg", "img2.jpg"),
                new Precio(new BigDecimal("15000"), "COP"),
                LocalDateTime.now()));

        String cuerpo = """
                {
                  "compradorId": "%s",
                  "articuloId": "%s",
                  "cantidad": 2,
                  "precioUnitario": { "monto": 15000, "moneda": "COP" }
                }
                """.formatted(compradorId, articulo.getId());

        // Act + Assert
        mockMvc.perform(post("/api/pedidos")
                        .with(user("ana@test.com").roles("COMPRADOR"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpo))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.estado").value("PENDIENTE"))
                .andExpect(jsonPath("$.cantidad").value(2))
                .andExpect(jsonPath("$.total").value(30000))
                .andExpect(jsonPath("$.articuloId").value(articulo.getId().toString()));
    }

    @Test
    void articuloInexistenteDeberiaResponder404() throws Exception {
        String cuerpo = """
                {
                  "compradorId": "%s",
                  "articuloId": "%s",
                  "cantidad": 1,
                  "precioUnitario": { "monto": 15000, "moneda": "COP" }
                }
                """.formatted(UUID.randomUUID(), UUID.randomUUID());

        mockMvc.perform(post("/api/pedidos")
                        .with(user("ana@test.com").roles("COMPRADOR"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpo))
                .andExpect(status().isNotFound());
    }
}
