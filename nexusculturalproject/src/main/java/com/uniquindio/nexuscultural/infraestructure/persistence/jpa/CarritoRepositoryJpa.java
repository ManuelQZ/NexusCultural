package com.uniquindio.nexuscultural.infraestructure.persistence.jpa;

import com.uniquindio.nexuscultural.domain.entity.CarritoItem;
import com.uniquindio.nexuscultural.domain.exception.ReglaDominioException;
import com.uniquindio.nexuscultural.domain.repository.CarritoRepository;
import com.uniquindio.nexuscultural.infraestructure.persistence.jpa.entity.CarritoItemEntity;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Primary
@Repository
public class CarritoRepositoryJpa implements CarritoRepository {

    private final CarritoItemJpaDataRepository dataRepository;

    public CarritoRepositoryJpa(CarritoItemJpaDataRepository dataRepository) {
        this.dataRepository = dataRepository;
    }

    @Override
    @Transactional
    public CarritoItem guardar(CarritoItem item) {
        CarritoItemEntity entity = CarritoItemEntity.builder()
                .id(item.getId() != null ? item.getId() : UUID.randomUUID())
                .compradorId(item.getCompradorId())
                .articuloId(item.getArticuloId())
                .cantidad(item.getCantidad())
                .build();
        CarritoItemEntity guardado = dataRepository.save(entity);
        return CarritoItem.reconstruir(guardado.getId(), guardado.getCompradorId(), guardado.getArticuloId(), guardado.getCantidad());
    }

    @Override
    public List<CarritoItem> buscarPorComprador(UUID compradorId) {
        return dataRepository.findByCompradorId(compradorId).stream()
                .map(e -> CarritoItem.reconstruir(e.getId(), e.getCompradorId(), e.getArticuloId(), e.getCantidad()))
                .toList();
    }

    @Override
    public Optional<CarritoItem> buscarPorId(UUID itemId) {
        return dataRepository.findById(itemId)
                .map(e -> CarritoItem.reconstruir(e.getId(), e.getCompradorId(), e.getArticuloId(), e.getCantidad()));
    }

    @Override
    @Transactional
    public void eliminar(UUID itemId) {
        dataRepository.deleteById(itemId);
    }

    @Override
    @Transactional
    public void eliminarPorComprador(UUID compradorId) {
        dataRepository.deleteByCompradorId(compradorId);
    }

    @Override
    @Transactional
    public CarritoItem actualizarCantidad(UUID itemId, int nuevaCantidad) {
        CarritoItemEntity entity = dataRepository.findById(itemId)
                .orElseThrow(() -> new ReglaDominioException("Ítem del carrito no encontrado: " + itemId));
        entity.setCantidad(nuevaCantidad);
        CarritoItemEntity guardado = dataRepository.save(entity);
        return CarritoItem.reconstruir(guardado.getId(), guardado.getCompradorId(), guardado.getArticuloId(), guardado.getCantidad());
    }
}

