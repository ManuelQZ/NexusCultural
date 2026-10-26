# NexusCultural — Pruebas Avanzadas del Backend

## Sección: Pruebas y dificultades encontradas




## 0. Pruebas de repositorios con H2

Mockito no permite hacer consultas SQL, por lo que probar los repositorios debe ser sí o sí contra la base de datos en H2. Se necesita verificar que repositorios, mappers y las reglas establecidas por anotaciones funcionen correctamente.

@Transactional hace que cada prueba se ejecute dentro de una transacción que se revierte al terminar, de modo que los datos de una prueba no afectan a las demás y la base queda limpia.

Lo que más se nos dificultó en el código fue crear mappers con formatos correctos y adaptados a las reglas que hicimos respecto a devolución, ya que el cambio de formato llegaba a tener choques con el uso de la clase Devolución.

## 1. Pruebas de seguridad con Spring Security

spring-security-test nos permite probar la seguridad y la autenticación en un proyecto hecho con Spring Security. Ofrece herramientas como el mock-user para no tener problemas con los test, asignándole a este user un rol y autenticándolo.

WebMVC ignora completamente las anotaciones @Config, ya que solo busca componentes @Controller. SecurityFilterChain normalmente está bajo la anotación @Config o, en nuestro caso, @Bean.

En esta parte, ajustar el tema de autentificación para que sea realmente seguro es complejo. Sin embargo, con lo que tenemos, se puede hacer algo temporal con fines académicos, pero el uso de Cookies y la protección de datos más delicados pueden suponer dificultad a largo plazo.

En este ejercicio en específico, lo que más nos costó fue settear correctamente el AuthenticationFilter para que pasara las pruebas.

### 1.1. Pruebas de seguridad en endpoints JWT

Validamos el comportamiento del filtro de Spring Security sobre rutas protegidas mediante tokens JWT:

- *401 Unauthorized:* ocurre al intentar acceder a un endpoint restringido sin enviar un token de autenticación.
- *403 Forbidden:* ocurre cuando la solicitud incluye un token JWT válido, pero el rol del usuario (ej. ROLE_INVITADO) no coincide con las autoridades requeridas por el endpoint.
- *200 OK:* se otorga acceso exitoso al enviar un token con el rol autorizado (ej. ROLE_COMPRADOR) simulado mediante jwt().authorities(...).

## 2. Pruebas con SpringBootTest y DataJpaTest

Mientras que SpringBootTest carga el contexto completo de la aplicación, como si estuviera en producción, DataJpaTest permite solo probar el "slice" de repositorios JPA, configurando una base de datos en memoria H2.

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) es la anotación que se utiliza para que el reemplazo automático no se dé y se pueda definir una base de datos a elección en el application.properties.

El Transactional viene por defecto en las pruebas de DataJpa, mientras que en el SpringBoot lo tenemos que definir nosotros. Sin embargo, en ambos cumple la misma función: revertir la acción cometida por el test.

En Spring 4 se separaron los módulos de pruebas para evitar consumir memoria innecesaria al incluir pruebas que ni siquiera se iban a realizar.

Nuestra dificultad principal con esta parte fue darnos cuenta de que realmente necesitaba imports para funcionar. Más allá de eso, funcionar bastante similar a las pruebas a las que estamos acostumbrados, excepto por el uso de métodos especiales de DataRepository.

### 2.1. Diferencias entre @DataJpaTest y @SpringBootTest

- *@DataJpaTest:* es una prueba de slice (capa focalizada) que únicamente inicializa los componentes de JPA, Hibernate y la base de datos H2. En Spring Boot 4 requiere importar explícitamente el adaptador del puerto del dominio con @Import(Adaptador.class) para probar la infraestructura sin cargar la aplicación completa.
- *@SpringBootTest:* inicializa el contexto completo de Spring (controladores, casos de uso, seguridad y persistencia), siendo ideal para pruebas de integración globales, pero con mayor tiempo de ejecución.

## 3. Pruebas de integración

Las pruebas de integración se utilizan principalmente para la interacción entre todos los componentes del sistema. Mientras que las pruebas unitarias son veloces, las de integración son lentas y difíciles de mantener debido a su robustez.

Tener que entrelazar los diferentes componentes nos fue demasiado útil para encontrar errores, he de admitir. Algunas partes fueron solucionadas mediante el método "machetazo"; sin embargo, se logró coordinar el funcionamiento de los componentes en la prueba CrearPedidoIntegración.

### 3.1. Pruebas de integración end-to-end

Una prueba de integración real verifica la comunicación continua entre todas las capas:

Controller -> Caso de Uso -> Entidad de Dominio -> Repositorio -> BD H2

A diferencia de los test unitarios con mocks, se ejecuta sin ningún @MockitoBean para garantizar que la traducción entre DTOs, entidades y tablas relacionales funcione correctamente en un entorno real.

## 4. Manejo de excepciones y validación del flujo

Para que Mock lance excepciones necesita usar términos tales como when y thenThrow, los cuales nos permiten establecer un condicional y una consecuencia, la cual es un comportamiento previsto, todo esto con el fin de hacer pruebas.

@RestControllAdvice maneja las excepciones que están fuera del try-catch mediante el uso de @ExceptionHandler, convirtiéndolas en HTTP.status.

En esta parte tuvimos un pequeño problema al usar nuestra ReglaDeDominioExceptionHandler con un BAD_REQUEST en vez de un CONFLICT, y eso afectó los test hasta que descubrimos el error.

verify(mock, never()).metodo(...) permite evaluar el flujo del controller, cortándolo una vez encuentre un error para no guardar en la base de datos algo que no queremos que se guarde.