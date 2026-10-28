package com.uniquindio.nexuscultural.infraestructure.persistence;

import com.uniquindio.nexuscultural.domain.entity.Artesano;
import com.uniquindio.nexuscultural.domain.repository.ArtesanoRepository;
import com.uniquindio.nexuscultural.domain.valueobject.Email;
import com.uniquindio.nexuscultural.domain.valueobject.TrayectoriaCultural;
import com.uniquindio.nexuscultural.domain.valueobject.UbicacionGeografica;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class ArtesanoRepositoryEnMemoria implements ArtesanoRepository {

    private final Map<UUID, Artesano> artesanos = new ConcurrentHashMap<>();

    public ArtesanoRepositoryEnMemoria() {
        Artesano semilla = Artesano.registrar(
                "Artesano Quindiano",
                new Email("artesano@test.com"),
                new TrayectoriaCultural(5, List.of("Premio Nacional")),
                new UbicacionGeografica("Quindío", "Armenia", "Calle 10 # 5-20"),
                LocalDateTime.now()
        );
        guardar(semilla);
    }

    @Override
    public Artesano guardar(Artesano artesano) {
        artesanos.put(artesano.getId(), artesano);
        return artesano;
    }

    @Override
    public Optional<Artesano> buscarPorId(UUID id) {
        return Optional.ofNullable(artesanos.get(id));
    }

    @Override
    public Optional<Artesano> buscarPorEmail(Email email) {
        return artesanos.values().stream()
                .filter(a -> a.getEmail().equals(email))
                .findFirst();
    }
}
