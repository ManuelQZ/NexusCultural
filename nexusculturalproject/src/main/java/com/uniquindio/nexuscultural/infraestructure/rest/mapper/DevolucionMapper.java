package com.uniquindio.nexuscultural.infraestructure.rest.mapper;

import com.uniquindio.nexuscultural.application.dto.response.DevolucionResponse;
import com.uniquindio.nexuscultural.domain.entity.Devolucion;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor

public class DevolucionMapper {
    public DevolucionResponse toResponse(Devolucion devolucion) {
        return new DevolucionResponse(
                devolucion.getId(),
                devolucion.getPedidoId(),
                devolucion.getMotivo(),
                devolucion.getFechaSolicitud(),
                devolucion.getEstado().toString()
        );

    }
}
