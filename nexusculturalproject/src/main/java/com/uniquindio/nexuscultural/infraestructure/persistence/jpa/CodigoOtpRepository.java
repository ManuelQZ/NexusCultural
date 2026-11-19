package com.uniquindio.nexuscultural.infraestructure.persistence.jpa;

import com.uniquindio.nexuscultural.domain.valueobject.CodigoOtp;
import com.uniquindio.nexuscultural.infraestructure.persistence.jpa.entity.CodigoOtpEntity;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Primary
@Repository
public class CodigoOtpRepository implements com.uniquindio.nexuscultural.domain.repository.CodigoOtpRepository {

    private final CodigoOtpJpaDataRepository codigoOtpJpaRepository;

    public CodigoOtpRepository(CodigoOtpJpaDataRepository codigoOtpJpaRepository) {
        this.codigoOtpJpaRepository = codigoOtpJpaRepository;
    }

    @Override
    public void guardar(String email, CodigoOtp otp) {
        CodigoOtpEntity entity = new CodigoOtpEntity();
        entity.setId(UUID.randomUUID());
        entity.setEmail(email);
        entity.setCodigo(otp.codigo());
        entity.setFechaExpiracion(otp.fechaExpiracion());
        entity.setEstado(otp.estado());
        entity.setFechaCreacion(LocalDateTime.now());

        codigoOtpJpaRepository.save(entity);
    }

    @Override
    public Optional<CodigoOtp> buscarUltimoPorEmail(String email) {
        return codigoOtpJpaRepository.findTopByEmailOrderByFechaCreacionDesc(email)
                .map(entity -> new CodigoOtp(
                        entity.getCodigo(),
                        entity.getFechaExpiracion(),
                        entity.getEstado()
                ));
    }

    @Override
    public void actualizar(String email, CodigoOtp otp) {
        codigoOtpJpaRepository.findTopByEmailOrderByFechaCreacionDesc(email)
                .ifPresent(entity -> {
                    entity.setEstado(otp.estado());
                    codigoOtpJpaRepository.save(entity);
                });
    }
}