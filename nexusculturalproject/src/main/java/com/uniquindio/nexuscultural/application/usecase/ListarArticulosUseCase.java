package com.uniquindio.nexuscultural.application.usecase;

import com.uniquindio.nexuscultural.domain.entity.Articulo;
import com.uniquindio.nexuscultural.domain.repository.ArticuloRepository;
import com.uniquindio.nexuscultural.domain.valueobject.Grupo;
import com.uniquindio.nexuscultural.domain.valueobject.Pagina;
import org.springframework.stereotype.Service;

@Service
public class ListarArticulosUseCase {

    private static final int TAMANO_MAXIMO = 50;

    private final ArticuloRepository articuloRepository;

    public ListarArticulosUseCase(ArticuloRepository articuloRepository) {
        this.articuloRepository = articuloRepository;
    }

    public Pagina<Articulo> ejecutar(String texto, Grupo grupo, int pagina, int tamano) {
        if (pagina < 0) {
            throw new IllegalArgumentException("La página no puede ser negativa.");
        }
        if (tamano < 1 || tamano > TAMANO_MAXIMO) {
            throw new IllegalArgumentException("El tamaño de página debe estar entre 1 y " + TAMANO_MAXIMO + ".");
        }
        return articuloRepository.buscarPaginado(texto, grupo, pagina, tamano);
    }
}
