package com.uniquindio.nexuscultural.infraestructure.persistence.jpa;

import com.uniquindio.nexuscultural.infraestructure.persistence.jpa.entity.CompradorEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CompradorJpaDataRepository extends JpaRepository<CompradorEntity, UUID> {
    Optional<CompradorEntity> findByEmail(String email);
    boolean existsByEmail(String email);
}

