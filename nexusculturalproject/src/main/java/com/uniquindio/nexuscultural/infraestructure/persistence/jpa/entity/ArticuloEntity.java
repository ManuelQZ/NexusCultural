package com.uniquindio.nexuscultural.infraestructure.persistence.jpa.entity;

import com.uniquindio.nexuscultural.domain.valueobject.EstadoArticulo;
import com.uniquindio.nexuscultural.domain.valueobject.Grupo;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "articulos")
@Getter
@Setter
@NoArgsConstructor
public class ArticuloEntity {

    @Id
    private UUID id;

    @Column(name = "artesano_id", nullable = false)
    private UUID artesanoId;

    @Column(nullable = false)
    private String titulo;

    @Column(length = 2000)
    private String descripcion;

    @Column(nullable = false)
    private int stock;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoArticulo estado;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private Grupo grupo; // si Grupo es una clase y no un enum, cámbialo por su propia entidad o por un String

    @Embedded
    private PrecioEmbeddable precio;

    @Column(name = "fecha_publicacion", nullable = false)
    private LocalDateTime fechaPublicacion;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "articulo_imagenes", joinColumns = @JoinColumn(name = "articulo_id"))
    @Column(name = "imagen", nullable = false)
    private List<String> imagenes = new ArrayList<>();

    @Version
    private Long version;
}
