package com.uniquindio.nexuscultural.infraestructure.rest.Persistence.Jpa;

import com.uniquindio.nexuscultural.domain.entity.Articulo;
import com.uniquindio.nexuscultural.domain.repository.ArticuloRepository;
import com.uniquindio.nexuscultural.domain.valueobject.Grupo;
import com.uniquindio.nexuscultural.domain.valueobject.Pagina;
import com.uniquindio.nexuscultural.domain.valueobject.Precio;
import com.uniquindio.nexuscultural.infraestructure.persistence.jpa.ArticuloRepositoryJpa;
import com.uniquindio.nexuscultural.infraestructure.persistence.jpa.mapper.ArticuloPersistenceMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@Import({ArticuloRepositoryJpa.class, ArticuloPersistenceMapper.class})
public class ArticuloRepositoryJpaDataTest {
    @Autowired
    private ArticuloRepository articuloRepository; // la interfaz del dominio, no la de Spring Data

    private Articulo guardar(String titulo, Grupo grupo) {
        return articuloRepository.guardar(Articulo.crear(UUID.randomUUID(), titulo, "Descripción de prueba",
                5, grupo, List.of("a.jpg", "b.jpg"),
                new Precio(new BigDecimal("15000"), "COP"), LocalDateTime.now()));
    }

    // ---------- Con datos propios (no dependen de data.sql) ----------

    @Test
    @DisplayName("Pagina los resultados y calcula totales")
    void deberiaPaginarYCalcularTotales() {
        guardar("Vasija zzq 1", Grupo.CERAMICA);
        guardar("Vasija zzq 2", Grupo.CERAMICA);
        guardar("Mochila zzq", Grupo.TEJIDOS);

        Pagina<Articulo> pagina = articuloRepository.buscar("zzq", null, 0, 2);

        assertEquals(3, pagina.totalElementos());
        assertEquals(2, pagina.totalPaginas());
        assertEquals(2, pagina.contenido().size());
    }

    @Test
    @DisplayName("Filtra por grupo e ignora mayúsculas en la búsqueda")
    void deberiaFiltrarPorGrupoYTexto() {
        guardar("Vasija zzf 1", Grupo.CERAMICA);
        guardar("Mochila zzf", Grupo.TEJIDOS);

        Pagina<Articulo> pagina = articuloRepository.buscar("ZZF", Grupo.CERAMICA, 0, 10);

        assertEquals(1, pagina.totalElementos());
        assertEquals(Grupo.CERAMICA, pagina.contenido().get(0).getGrupo());
    }

    @Test
    @DisplayName("No muestra los artículos eliminados")
    void noDeberiaMostrarEliminados() {
        Articulo eliminado = guardar("Eliminado zze", Grupo.MADERA);
        guardar("Visible zze", Grupo.MADERA);
        eliminado.eliminar(false);
        articuloRepository.guardar(eliminado);

        Pagina<Articulo> pagina = articuloRepository.buscar("zze", null, 0, 10);

        assertEquals(1, pagina.totalElementos());
        assertEquals("Visible zze", pagina.contenido().get(0).getTitulo());
    }

    // ---------- Sobre el seed de data.sql ----------

    @Test
    @DisplayName("Seed: busca por texto en los datos de data.sql")
    void deberiaBuscarPorTextoSobreElSeed() {
        Pagina<Articulo> pagina = articuloRepository.buscar("vasija", null, 0, 10);

        assertEquals(1, pagina.totalElementos());
        assertEquals("Vasija de barro", pagina.contenido().get(0).getTitulo());
    }

    @Test
    @DisplayName("Seed: filtra por grupo")
    void deberiaFiltrarPorGrupoSobreElSeed() {
        Pagina<Articulo> pagina = articuloRepository.buscar("", Grupo.TEJIDOS, 0, 10);

        assertEquals(1, pagina.totalElementos());
        assertEquals("Mochila wayuu", pagina.contenido().get(0).getTitulo());
    }

    @Test
    @DisplayName("Seed: pagina de a 2 y calcula 2 páginas para 3 artículos")
    void deberiaPaginarElSeed() {
        Pagina<Articulo> primera = articuloRepository.buscar("", null, 0, 2);
        Pagina<Articulo> segunda = articuloRepository.buscar("", null, 1, 2);

        assertEquals(3, primera.totalElementos());
        assertEquals(2, primera.totalPaginas());
        assertEquals(2, primera.contenido().size());
        assertEquals(1, segunda.contenido().size());
    }
}
