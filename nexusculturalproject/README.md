# NexusCultural

Proyecto de comercio de artesanías colombianas implementado con arquitectura hexagonal y Spring Boot 4.

## Pruebas avanzadas

### Punto 1: Prueba de endpoint con seguridad
Las pruebas de seguridad con MockMvc utilizan `jwt().authorities()` para simular usuarios autenticados sin pasar por el login real. Esto permite probar de forma aislada las reglas de autorización (401 sin token, 403 con rol incorrecto, 200 con rol correcto) sobre la SecurityFilterChain configurada en el proyecto. Al usar `@SpringBootTest` con `@AutoConfigureMockMvc`, se carga la configuración de seguridad completa, a diferencia de `@WebMvcTest` que aplicaría una configuración por defecto.

### Punto 2: @DataJpaTest contra @SpringBootTest
`@DataJpaTest` carga únicamente la capa de persistencia JPA (entidades y repositorios Spring Data), lo que la hace más rápida que `@SpringBootTest` que levanta toda la aplicación. Sin embargo, `@DataJpaTest` no carga los adaptadores personalizados, por lo que es necesario importarlos explícitamente con `@Import`. Esta anotación también usa H2 por defecto y deshace cada prueba automáticamente con `@Transactional`, ideal para tests de repositorio aislados.

### Punto 3: Prueba de integración real (End-to-End)
Las pruebas de integración con `@SpringBootTest` y `@AutoConfigureMockMvc` recorren el flujo completo sin mocks: Controller → Caso de Uso → Repositorio → Base de datos H2. A diferencia de las unitarias, estas pruebas detectan errores de configuración entre capas y problemas de mapeo que las pruebas aisladas no revelarían. Son más lentas pero necesarias para validar que el sistema funciona en conjunto. El uso de `@Transactional` deshace los cambios al terminar cada test para no contaminar datos entre pruebas.
