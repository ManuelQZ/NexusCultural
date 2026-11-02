package com.uniquindio.nexuscultural.infraestructure.persistence;

import com.uniquindio.nexuscultural.domain.entity.Administrador;
import com.uniquindio.nexuscultural.domain.repository.AdministradorRepository;
import com.uniquindio.nexuscultural.domain.valueobject.Email;
import com.uniquindio.nexuscultural.domain.valueobject.NivelAcceso;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class AdministradorRepositoryEnMemoria implements AdministradorRepository {

    private final Map<UUID, Administrador> administradores = new ConcurrentHashMap<>();

    public AdministradorRepositoryEnMemoria() {
        Administrador semilla = Administrador.builder()
                .id(UUID.randomUUID())
                .nombreCompleto("Administrador General")
                .email(new Email("admin@test.com"))
                .fechaRegistro(LocalDateTime.now())
                .nivelAcceso(NivelAcceso.SUPER_ADMIN)
                .build();
        guardar(semilla);
    }

    @Override
    public Administrador guardar(Administrador administrador) {
        administradores.put(administrador.getId(), administrador);
        return administrador;
    }

    @Override
    public Optional<Administrador> buscarPorId(UUID id) {
        return Optional.ofNullable(administradores.get(id));
    }

    @Override
    public Optional<Administrador> buscarPorEmail(Email email) {
        return administradores.values().stream()
                .filter(a -> a.getEmail().equals(email))
                .findFirst();
    }
}
