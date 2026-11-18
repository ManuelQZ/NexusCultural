package com.uniquindio.nexuscultural.infraestructure.persistence.jpa.entity;

import com.uniquindio.nexuscultural.domain.valueobject.RolUsuario;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "compradores")
@Getter
@Setter
public class CompradorEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String nombreCompleto;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private LocalDateTime fechaRegistro;

    private String departamento;
    private String municipio;
    private String direccion;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "comprador_favoritos", joinColumns = @JoinColumn(name = "comprador_id"))
    @Column(name = "articulo_id")
    private List<UUID> favoritos = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RolUsuario rol = RolUsuario.COMPRADOR;
}

