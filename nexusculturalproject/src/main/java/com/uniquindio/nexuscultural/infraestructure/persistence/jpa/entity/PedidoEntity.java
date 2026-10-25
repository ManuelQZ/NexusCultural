package com.uniquindio.nexuscultural.infraestructure.persistence.jpa.entity;

import com.uniquindio.nexuscultural.domain.valueobject.EstadoPedido;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "pedidos")
@Getter
@Setter
@NoArgsConstructor
public class PedidoEntity {

    @Id
    private UUID id;

    @Column(name = "comprador_id", nullable = false)
    private UUID compradorId;

    @Column(name = "articulo_id", nullable = false)
    private UUID articuloId;

    @Column(nullable = false)
    private int cantidad;

    @Embedded
    private PrecioEmbeddable total;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoPedido estado;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_entrega")
    private LocalDateTime fechaEntrega;

    @Column(name = "metodo_pago")
    private String metodoPago;

    @Column(name = "referencia_transaccion")
    private String referenciaTransaccion;

    @Version
    private Long version;
}

