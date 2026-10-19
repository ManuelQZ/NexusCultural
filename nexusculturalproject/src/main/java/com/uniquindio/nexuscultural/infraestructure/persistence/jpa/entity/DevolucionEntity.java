package com.uniquindio.nexuscultural.infraestructure.persistence.jpa.entity;

import com.uniquindio.nexuscultural.domain.valueobject.EstadoDevolucion;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "devoluciones",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "unique_devoluciones_pedido_id",
                        columnNames = "pedido_id"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class DevolucionEntity {

    @Id
    private UUID id;

    @Column(name = "pedido_id", nullable = false, unique = true)
    private UUID pedidoId;

    @Column(nullable = false, length = 500)
    private String motivo;

    @Column(name = "fecha_solicitud", nullable = false)
    private LocalDateTime fechaSolicitud;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoDevolucion estado;
}

