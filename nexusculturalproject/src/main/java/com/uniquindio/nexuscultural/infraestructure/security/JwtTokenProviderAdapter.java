package com.uniquindio.nexuscultural.infraestructure.security;

import com.uniquindio.nexuscultural.application.dto.response.AuthTokenResponse;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtTokenProviderAdapter {

    private final SecretKey key;
    private final long expirationInMinutes;

    public JwtTokenProviderAdapter(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiry:60}") long expirationInMinutes) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationInMinutes = expirationInMinutes;
    }

    public AuthTokenResponse generarToken(UUID usuarioId, String email, String rol) {
        Instant ahora = Instant.now();
        Instant expiracion = ahora.plusSeconds(expirationInMinutes * 60);

        // PAYLOAD: Datos contenidos en el token (lo que en el backend de referencia llaman "lo que dice la manilla")
        String token = Jwts.builder()
                .subject(email)
                .claim("id", usuarioId.toString())
                .claim("rol", rol)
                .issuedAt(Date.from(ahora))
                .expiration(Date.from(expiracion))
                .signWith(key)
                .compact();

        return new AuthTokenResponse(
                token,
                "Bearer",
                rol,
                expirationInMinutes * 60
        );
    }
}
