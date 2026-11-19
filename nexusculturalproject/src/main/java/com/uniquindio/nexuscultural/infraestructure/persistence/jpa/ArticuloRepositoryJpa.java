package com.uniquindio.nexuscultural.infraestructure.persistence.jpa;

import com.uniquindio.nexuscultural.domain.entity.Articulo;
import com.uniquindio.nexuscultural.domain.repository.ArticuloRepository;
import com.uniquindio.nexuscultural.domain.valueobject.EstadoArticulo;
import com.uniquindio.nexuscultural.domain.valueobject.Grupo;
import com.uniquindio.nexuscultural.domain.valueobject.Pagina;
import com.uniquindio.nexuscultural.infraestructure.persistence.jpa.entity.ArticuloEntity;
import com.uniquindio.nexuscultural.infraestructure.persistence.jpa.mapper.ArticuloPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Primary
@Repository
@RequiredArgsConstructor
public class ArticuloRepositoryJpa implements ArticuloRepository {

    private final ArticuloJpaDataRepository dataRepository;
    private final ArticuloPersistenceMapper mapper;

    @Override
    @Transactional
    public Articulo guardar(Articulo articulo) {
        ArticuloEntity entity = dataRepository.findById(articulo.getId())
                .map(existente -> {
                    mapper.actualizarEntity(articulo, existente);
                    return existente;
                })
                .orElseGet(() -> mapper.toEntity(articulo));
        return mapper.toDomain(dataRepository.save(entity));
    }

    @Override
    public Optional<Articulo> buscarPorId(UUID id) {
        return dataRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    @Transactional
    public void eliminar(UUID id) {
        dataRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Articulo> listarDisponibles() {
        return dataRepository.findByEstado(EstadoArticulo.DISPONIBLE).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Articulo> buscarPorTitulo(String query) {
        return dataRepository.findByTituloContainingIgnoreCaseAndEstado(query, EstadoArticulo.DISPONIBLE).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Articulo> buscarPorGrupo(Grupo grupo) {
        return dataRepository.findByGrupoAndEstado(grupo, EstadoArticulo.DISPONIBLE).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Articulo> listarPorArtesano(UUID artesanoId) {
        return dataRepository.findByArtesanoId(artesanoId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Articulo> listarTodos() {
        return dataRepository.findAllByOrderByFechaPublicacionDesc().stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Pagina<Articulo> buscar(String texto, Grupo grupo, int pagina, int tamano) {
        String filtro = (texto == null) ? "" : texto.trim();

        Page<ArticuloEntity> resultado = dataRepository.buscar(
                filtro, grupo, EstadoArticulo.ELIMINADO,
                PageRequest.of(pagina, tamano, Sort.by("fechaPublicacion").descending()));

        return new Pagina<>(
                resultado.getContent().stream().map(mapper::toDomain).toList(),
                resultado.getTotalElements(),
                resultado.getTotalPages(),
                pagina,
                tamano);
    }

    @Override
    @Transactional(readOnly = true)
    public Pagina<Articulo> buscarPaginado(String texto, Grupo grupo, int pagina, int tamano) {
        Pageable pageable = PageRequest.of(pagina, tamano, Sort.by(Sort.Direction.DESC, "fechaPublicacion"));
        Page<ArticuloEntity> resultado = dataRepository.buscar(
                texto == null ? "" : texto.trim(), grupo, EstadoArticulo.ELIMINADO, pageable);
        return new Pagina<>(
                resultado.getContent().stream().map(mapper::toDomain).toList(),
                resultado.getTotalElements(), resultado.getTotalPages(), pagina, tamano);
    }
}
