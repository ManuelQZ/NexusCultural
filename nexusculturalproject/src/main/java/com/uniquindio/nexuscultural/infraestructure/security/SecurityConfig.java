package com.uniquindio.nexuscultural.infraestructure.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;



@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(e -> e
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                .authorizeHttpRequests(auth -> auth
                        // 1. Endpoints públicos
                        .requestMatchers("/api/usuarios/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/administradores", "/api/artesanos", "/api/compradores").permitAll()

                        // 2. Catálogo público
                        .requestMatchers(HttpMethod.GET, "/api/catalogo/**", "/api/articulos/**").permitAll()

                        // 3. Almacenamiento (ajusta si debe exigir autenticación)
                        .requestMatchers("/api/v1/storage/**").permitAll()

                        // 4. Gestión de artículos
                        .requestMatchers(HttpMethod.POST, "/api/articulos/**").hasAnyRole("ARTESANO", "ADMINISTRADOR")
                        .requestMatchers(HttpMethod.PUT, "/api/articulos/**").hasAnyRole("ARTESANO", "ADMINISTRADOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/articulos/**").hasAnyRole("ARTESANO", "ADMINISTRADOR")

                        // 5. Endpoints protegidos por rol
                        .requestMatchers("/api/administradores/**").hasRole("ADMINISTRADOR")
                        .requestMatchers("/api/artesanos/**").hasAnyRole("ARTESANO", "ADMINISTRADOR")
                        .requestMatchers("/api/compradores/**").hasAnyRole("COMPRADOR", "ADMINISTRADOR")

                        // 6. Todo lo demás requiere autenticación
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
