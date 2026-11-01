package com.uniquindio.nexuscultural.infraestructure.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

/**
 * Declara el esquema Bearer JWT para que Swagger UI muestre el botón "Authorize"
 * y envíe el header Authorization en las peticiones.
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(title = "Nexus Cultural API", version = "1.0",
                description = "API del marketplace de artesanías Nexus Cultural"),
        security = @SecurityRequirement(name = OpenApiConfig.BEARER_SCHEME)
)
@SecurityScheme(
        name = OpenApiConfig.BEARER_SCHEME,
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "Pegue el token JWT (sin el prefijo 'Bearer ')."
)
public class OpenApiConfig {
    public static final String BEARER_SCHEME = "bearerAuth";
}
