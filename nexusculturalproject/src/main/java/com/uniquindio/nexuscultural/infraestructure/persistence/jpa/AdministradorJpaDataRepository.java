package com.uniquindio.nexuscultural.infraestructure.persistence.jpa;

import com.uniquindio.nexuscultural.infraestructure.persistence.jpa.entity.AdministradorEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AdministradorJpaDataRepository extends JpaRepository<AdministradorEntity, UUID> {
    Optional<AdministradorEntity> findByEmail(String email);
    boolean existsByEmail(String email);
}

