package com.uniquindio.nexuscultural.domain.repository;

import com.uniquindio.nexuscultural.domain.entity.Articulo;
import com.uniquindio.nexuscultural.domain.valueobject.Grupo;
import com.uniquindio.nexuscultural.domain.valueobject.Pagina;

import java.util.Optional;
import java.util.UUID;

public interface ArticuloRepository {

    Articulo guardar(Articulo articulo);

    Optional<Articulo> buscarPorId(UUID id);

    void eliminar(UUID id);

    Pagina<Articulo> buscar(String texto, Grupo grupo, int pagina, int tamano);
}
