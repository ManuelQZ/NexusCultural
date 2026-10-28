package com.uniquindio.nexuscultural.infraestructure.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
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
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
                .exceptionHandling(e -> e
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                        .accessDeniedHandler((request, response, accessDeniedException) ->
                                response.sendError(HttpStatus.FORBIDDEN.value(), "Acceso denegado: rol no autorizado"))
                )
                .authorizeHttpRequests(auth -> auth
                        // 1. Endpoints públicos (autenticación OTP, registros, H2, Swagger)
                        .requestMatchers(
                                "/api/usuarios/**",
                                "/h2-console/**",
                                "/error",
                                "/api-docs/**",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/artesanos", "/api/compradores", "/api/administradores").permitAll()

                        // 2. Catálogo público
                        .requestMatchers(HttpMethod.GET, "/api/catalogo/**", "/api/articulos/**").permitAll()

                        // 3. Rutas protegidas por rol:
                        // Solo ARTESANO (vendedor) publica o modifica artículos
                        .requestMatchers(HttpMethod.POST, "/api/articulos/**").hasRole("ARTESANO")
                        .requestMatchers(HttpMethod.PUT, "/api/articulos/**").hasRole("ARTESANO")
                        .requestMatchers(HttpMethod.DELETE, "/api/articulos/**").hasRole("ARTESANO")
                        .requestMatchers("/api/artesanos/**").hasRole("ARTESANO")

                        // Solo COMPRADOR solicita reembolsos / devoluciones
                        .requestMatchers(HttpMethod.POST, "/api/devoluciones/**").hasRole("COMPRADOR")
                        .requestMatchers("/api/compradores/**").hasRole("COMPRADOR")

                        // Administrador
                        .requestMatchers("/api/administradores/**").hasAnyRole("ADMIN", "ADMINISTRADOR")

                        // 4. Cualquier otra petición requiere autenticación
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
