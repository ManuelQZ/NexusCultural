package com.uniquindio.nexuscultural.infraestructure.persistence;

import com.uniquindio.nexuscultural.domain.entity.Comprador;
import com.uniquindio.nexuscultural.domain.repository.CompradorRepository;
import com.uniquindio.nexuscultural.domain.valueobject.Email;
import com.uniquindio.nexuscultural.domain.valueobject.UbicacionGeografica;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class CompradorRepositoryEnMemoria implements CompradorRepository {

    private final Map<UUID, Comprador> compradores = new ConcurrentHashMap<>();

    public CompradorRepositoryEnMemoria() {
        Comprador semilla = Comprador.registrar(
                "Comprador Cultural",
                new Email("comprador@test.com"),
                new UbicacionGeografica("Quindío", "Armenia", "Carrera 14 # 21-00"),
                LocalDateTime.now()
        );
        guardar(semilla);
    }

    @Override
    public Comprador guardar(Comprador comprador) {
        compradores.put(comprador.getId(), comprador);
        return comprador;
    }

    @Override
    public Optional<Comprador> buscarPorId(UUID id) {
        return Optional.ofNullable(compradores.get(id));
    }

    @Override
    public Optional<Comprador> buscarPorEmail(Email email) {
        return compradores.values().stream()
                .filter(c -> c.getEmail().equals(email))
                .findFirst();
    }
}
