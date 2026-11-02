package com.uniquindio.nexuscultural.infraestructure.persistence.jpa;

import com.uniquindio.nexuscultural.infraestructure.persistence.jpa.entity.CodigoOtpEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CodigoOtpJpaDataRepository extends JpaRepository<CodigoOtpEntity, UUID> {
    Optional<CodigoOtpEntity> findTopByEmailOrderByFechaCreacionDesc(String email);
}