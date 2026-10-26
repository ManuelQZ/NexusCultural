# NexusCultural — Pruebas Avanzadas del Backend

## Sección: Pruebas Avanzadas

### 1. Pruebas de Seguridad en Endpoints JWT
Validamos el comportamiento del filtro de Spring Security sobre rutas protegidas mediante tokens JWT:
- **401 Unauthorized**: Ocurre al intentar acceder a un endpoint restringido sin enviar un token de autenticación.
- **403 Forbidden**: Ocurre cuando la solicitud incluye un token JWT válido pero el rol del usuario (ej. `ROLE_INVITADO`) no coincide con las autoridades requeridas por el endpoint.
- **200 OK**: Se otorga acceso exitoso al enviar un token con el rol autorizado (ej. `ROLE_COMPRADOR`) simulado mediante `jwt().authorities(...)`.

### 2. Diferencia entre @DataJpaTest y @SpringBootTest
- `@DataJpaTest` es una prueba de slice (capa focalizada) que únicamente inicializa los componentes de JPA, Hibernate y la base de datos H2. En Spring Boot 4 requiere importar explícitamente el adaptador del puerto del dominio con `@Import(Adaptador.class)` para probar la infraestructura sin cargar la aplicación completa.
- `@SpringBootTest` inicializa el contexto completo de Spring (Controladores, Casos de Uso, Seguridad y Persistencia), siendo ideal para pruebas de integración globales pero con mayor tiempo de ejecución.

### 3. Pruebas de Integración End-to-End
Una prueba de integración real verifica la comunicación continua entre todas las capas (Controller -> Caso de Uso -> Entidad de Dominio -> Repositorio -> BD H2). A diferencia de los tests unitarios con mocks, se ejecuta sin ningún `@MockitoBean` para garantizar que la traducción entre DTOs, entidades y tablas relacionales funcione correctamente en un entorno real.

---

### Evidencia de Ejecución

![Pruebas en Verde](./docs/captura_tests_verde.png)
*Resultado de la ejecucion del comando `./gradlew test` con todas las suites en verde.*
