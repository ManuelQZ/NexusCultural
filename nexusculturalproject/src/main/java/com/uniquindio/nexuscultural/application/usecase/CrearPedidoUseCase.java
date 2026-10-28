package com.uniquindio.nexuscultural.application.usecase;

import com.uniquindio.nexuscultural.application.dto.request.CrearPedidoRequest;
import com.uniquindio.nexuscultural.domain.entity.Articulo;
import com.uniquindio.nexuscultural.domain.entity.Pedido;
import com.uniquindio.nexuscultural.domain.exception.RecursoNoEncontradoException;
import com.uniquindio.nexuscultural.domain.repository.ArticuloRepository;
import com.uniquindio.nexuscultural.domain.repository.PedidoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CrearPedidoUseCase {

    private final PedidoRepository pedidoRepository;
    private final ArticuloRepository articuloRepository;

    public CrearPedidoUseCase(PedidoRepository pedidoRepository, ArticuloRepository articuloRepository) {
        this.pedidoRepository = pedidoRepository;
        this.articuloRepository = articuloRepository;
    }

    public Pedido ejecutar(CrearPedidoRequest request) {
        // 1. Verificar que el artículo existe y está disponible
        Articulo articulo = articuloRepository.buscarPorId(request.articuloId())
                .orElseThrow(() -> new RecursoNoEncontradoException("El artículo no existe."));

        // 2. Crear el pedido usando el factory method del dominio
        Pedido pedido = Pedido.crear(
                request.compradorId(),
                request.articuloId(),
                request.cantidad(),
                request.precioUnitario(),
                LocalDateTime.now()
        );

        // 3. Reducir el stock del artículo
        articulo.reducirStock(request.cantidad());
        articuloRepository.guardar(articulo);

        // 4. Guardar el pedido
        pedidoRepository.guardar(pedido);

        return pedido;
    }
}
