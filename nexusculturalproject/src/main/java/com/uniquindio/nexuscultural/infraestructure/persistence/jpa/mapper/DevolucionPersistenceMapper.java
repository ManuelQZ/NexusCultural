package com.uniquindio.nexuscultural.infraestructure.persistence.jpa.mapper;

import com.uniquindio.nexuscultural.domain.entity.Devolucion;
import com.uniquindio.nexuscultural.infraestructure.persistence.jpa.entity.DevolucionEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DevolucionPersistenceMapper {
    public DevolucionEntity toEntity(Devolucion devolucion) {
        DevolucionEntity entity = new DevolucionEntity();
        entity.setId(devolucion.getId());
        entity.setPedidoId(devolucion.getPedidoId());
        entity.setMotivo(devolucion.getMotivo());
        entity.setEstado(devolucion.getEstado());
        entity.setFechaSolicitud(devolucion.getFechaSolicitud());
        return entity;
    }

    public void actualizarEntity(Devolucion devolucion, DevolucionEntity entity) {
        entity.setMotivo(devolucion.getMotivo());
        entity.setEstado(devolucion.getEstado());
    }

    public Devolucion toDomain(DevolucionEntity entity) {
        return Devolucion.reconstruir(
                entity.getId(),
                entity.getPedidoId(),
                entity.getMotivo(),
                entity.getFechaSolicitud(),
                entity.getEstado());
    }
}
