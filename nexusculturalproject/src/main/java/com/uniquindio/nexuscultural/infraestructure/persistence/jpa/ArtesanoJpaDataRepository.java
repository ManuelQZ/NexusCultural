package com.uniquindio.nexuscultural.infraestructure.persistence.jpa;

import com.uniquindio.nexuscultural.infraestructure.persistence.jpa.entity.ArtesanoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ArtesanoJpaDataRepository extends JpaRepository<ArtesanoEntity, UUID> {
    Optional<ArtesanoEntity> findByEmail(String email);
    boolean existsByEmail(String email);
}

