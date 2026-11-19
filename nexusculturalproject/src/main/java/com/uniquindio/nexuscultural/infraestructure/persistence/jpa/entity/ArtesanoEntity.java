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
@Table(name = "artesanos")
@Getter
@Setter
public class ArtesanoEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String nombreCompleto;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private LocalDateTime fechaRegistro;

    private int anosExperiencia;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "artesano_reconocimientos", joinColumns = @JoinColumn(name = "artesano_id"))
    @Column(name = "reconocimiento")
    private List<String> reconocimientos = new ArrayList<>();

    private String departamento;
    private String municipio;
    private String direccion;
    private boolean activo = true;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RolUsuario rol = RolUsuario.ARTESANO;
}

