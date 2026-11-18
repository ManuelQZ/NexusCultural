# Nexus Cultural • Frontend Web
> **Entrega 3: Frontend conectado a su backend**  
> **Asignatura:** Programación Avanzada · Universidad del Quindío  
> **Docente:** Valentina Oviedo Sánchez  
> **Ponderación:** 25% de la nota del curso  

---

## 1. Información General del Proyecto
* **Nicho de Mercado:** Marketplace E-commerce de Comercio Justo y Artesanías Tradicionales Colombianas (*Nexus Cultural*). Plataforma para conectar comunidades indígenas y maestros artesanos (Wayuu, Chamba, Nariño, Putumayo) con compradores nacionales e internacionales, garantizando autenticidad y trazabilidad de pagos mediante Wompi Bancolombia.
* **Integrantes:**
  * Wilson Delgado
  * Equipo de Desarrollo Nexus Cultural

---

## 2. Cómo Ejecutar el Proyecto

### 2.1 Requisitos Previos
* **Node.js:** Versión `>= 22.22.3` (Recomendado v22.23.4 mediante NVM).
* **NPM:** Versión `>= 10.9.0`.
* **Angular CLI:** Versión 22.

### 2.2 Ejecución del Frontend (Angular)
1. Clonar o abrir la carpeta del frontend:
   ```bash
   cd nexus-cultural-web
   ```
2. Instalar dependencias:
   ```bash
   npm install
   ```
3. Iniciar el servidor de desarrollo con proxy CORS integrado:
   ```bash
   npm start
   ```
   *La aplicación quedará disponible en:* `http://localhost:4200`

> **Nota sobre CORS y Proxy:** El script `npm start` ejecuta `ng serve` enlazado con [proxy.conf.json](proxy.conf.json), reenviando las llamadas `/api/*` directamente a la API de AWS CloudFront con enmascaramiento de cabeceras `Origin`, evitando bloqueos en cualquier navegador (Firefox, Chrome, Edge).

### 2.3 Conexión con el Backend
* **Servidor Desplegado en la Nube (AWS):**
  * **Base URL de la API:** `https://d3gt0752ebjv7t.cloudfront.net/api`
  * **Base de Datos:** PostgreSQL en AWS RDS.
  * **Almacenamiento de Multimedia:** AWS S3 Bucket (`nexus-cultural-storage-bucket`).
  * **Pasarela de Pagos:** API oficial Wompi Sandbox Cloud.
* **Backend Local (Opcional):**
  * Para correr el backend localmente en Spring Boot:
    ```bash
    cd Nexus-cultural-desplegado-en-aws
    ./gradlew bootRun
    ```
    (Puerto por defecto: `8080`).

---

## 3. Usuarios de Prueba para Sustentación

El sistema de Nexus Cultural utiliza autenticación moderna **sin contraseñas basada en Código OTP y Tokens JWT** (`/api/usuarios/solicitar` y `/api/usuarios/verificar`), enviando un código numérico temporal de 6 dígitos:

| Rol | Correo de Prueba | Tipo de Acceso / Panel |
| :--- | :--- | :--- |
| **Comprador Cultural** | `comprador@nexuscultural.com` *(o cualquier correo registrado en `/registro`)* | Acceso a Catálogo Paginado, Detalle con Audio TTS, Carrito, Pasarela Wompi y Mis Compras. |
| **Maestro Artesano** | `artesano@nexuscultural.com` *(o registrado en `/registro`)* | Acceso a Taller Artesanal, Publicación con Formulario Reactivo, Carga a S3 y Reposición de Stock. |
| **Administrador** | `admin@nexuscultural.com` | Acceso exclusivo a Panel Admin, Creación Interna de Admins, Moderación y Reembolsos. |

---

## 4. Lista de Pantallas y Rutas del Sistema

| Ruta | Componente | Descripción | Protección / Guard |
| :--- | :--- | :--- | :--- |
| `/` | `Inicio` | Pantalla de bienvenida limpia con únicamente botones de Iniciar Sesión y Registro. | Pública |
| `/login` | `Login` | Autenticación OTP en dos pasos (Correo $\rightarrow$ Código 6 dígitos) con Formulario Reactivo y JWT. | `guestGuard` (expulsa a quien ya tiene sesión) |
| `/registro` | `Registro` | Registro público exclusivo para Compradores y Maestros Artesanos. | `guestGuard` |
| `/catalogo` | `Catalogo` | Catálogo oficial paginado (`page`, `size`), búsqueda por texto en backend y filtros de categorías. | `authGuard` |
| `/detalle-producto` | `DetalleProducto` | Detalle dinámico de la pieza por ID, galería S3, cálculo de stock y narrador asistido por voz TTS. | `authGuard` |
| `/carrito` | `Carrito` | Carrito de compras reactivo (`CarritoService`) persistido en `localStorage` con badge en header. | `authGuard` |
| `/checkout` | `Checkout` | Proceso de compra con autocompletado de perfil y **Pasarela Oficial Wompi Sandbox (VISA 4242)**. | `authGuard` |
| `/pedidos` | `Pedidos` | Historial real de compras del comprador consultado en AWS, trazabilidad y radicación de devoluciones. | `authGuard` |
| `/artesano` | `ArtesanoPanel` | Panel exclusivo del artesano: inventario, reposición de stock, subida a S3 y **Formulario Reactivo de publicación**. | `artisanGuard` (solo rol ARTESANO) |
| `/admin` | `AdminPanel` | Panel administrativo: creación interna de administradores, moderación y resolución de devoluciones. | `adminGuard` (solo rol ADMINISTRADOR) |

---

## 5. Rúbrica de Evaluación y Extras Implementados

### 5.1 Criterios Mínimos (Sección 2)
1. **Conexión y Servicios:** Interfaces espejo en TypeScript para cada DTO del backend, servicios por módulo (`CatalogoService`, `PedidoService`, etc.) y CORS resuelto. Cero datos quemados.
2. **Login JWT:** Formulario reactivo con validaciones, almacenamiento en `localStorage`, menú dinámico según rol, cierre de sesión sin recargar y traducción de errores.
3. **Interceptor HTTP:** `authInterceptor` añade `Authorization: Bearer <token>` automáticamente y captura el error **401** para cerrar sesión y notificar.
4. **Guards:** `authGuard`, `guestGuard`, `artisanGuard` y `adminGuard`.
5. **Catálogo Paginado y Búsqueda:** Parámetros `page` y `size` enviados al backend, botones Anterior/Siguiente, indicador *"Página X de Y"* y búsqueda en servidor.
6. **Operación de Negocio con Formulario Reactivo:** Publicación de artesanías mediante `FormGroup` con validaciones, desactivación de botón mientras envía y traducción de códigos HTTP (400, 401, 403, 404).
7. **Avisos (Notificaciones):** Servicio in-memory de Toasts temporales (`NotificacionesService`) para éxito, advertencia y error.
8. **Bootstrap y Responsive:** Clases formales de Bootstrap 5, navbar colapsable hamburguesa y tarjetas adaptables a pantallas móviles.
9. **Funcionalidad Propia:** Integración de la **Pasarela Wompi Sandbox** (`POST /api/pagos/wompi/confirmar`) reflejando la transacción en la nube de Wompi.

### 5.2 Extras Implementados (+18 puntos)
* **Guard por rol (+3 pts):** Rutas exclusivas separadas para comprador, artesano y administrador.
* **BD Relacional en Servidor (+4 pts):** PostgreSQL desplegado en AWS RDS con persistencia real.
* **Despliegue en la Nube (+5 pts):** Backend y API desplegados con HTTPS en AWS CloudFront.
* **Archivos e Imágenes Externas (+4 pts):** Carga binaria a AWS S3 mediante Presigned URLs (cero base64 en BD).
* **Buenas Prácticas (+2 pts):** Uso de `environment.ts`, Signals de Angular 22 y componentes modulares.

---

## 6. Guía Rápida para la Sustentación (Respuestas a Preguntas 4.2)

1. **¿Por qué el componente no escribe la dirección de la API y le pide al servicio?**  
   Por el principio de separación de responsabilidades y mantenibilidad. Si la URL del backend cambia o se migra de entorno (desarrollo a AWS), solo se modifica el servicio o la variable de entorno, sin alterar los componentes visuales.
2. **¿Por qué el token se guarda y se envía en cada petición, y quién lo envía?**  
   Porque la arquitectura REST es sin estado (*stateless*); el servidor no guarda sesiones en memoria y valida la autenticidad en cada llamado. Quien lo inyecta automáticamente en los encabezados es el **Interceptor HTTP** (`authInterceptor`).
3. **¿Por qué un guard no es seguridad real?**  
   Porque el guard corre en el cliente (JavaScript en el navegador) y cualquier usuario con herramientas de desarrollador puede manipular el código local. La seguridad real y definitiva reside siempre en el backend (Spring Security validando el JWT y los roles).
4. **¿Qué pasa si un token vence? ¿Quién lo detecta?**  
   El backend rechaza la petición con un código HTTP **401 Unauthorized**. El `authInterceptor` en Angular captura este código de error en la tubería `catchError`, borra el token expirado de `localStorage`, muestra un aviso al usuario y lo redirige a `/login`.
5. **¿Por qué la paginación y la búsqueda las hace el backend?**  
   Por rendimiento y escalabilidad. Si existen miles de productos, traerlos todos a Angular saturaría la memoria del navegador y la red. El backend utiliza sentencias SQL optimizadas (`LIMIT`, `OFFSET` y filtros indexados) trayendo únicamente los elementos necesarios.
6. **¿Qué es una señal (signal) y en qué se diferencia de una variable normal?**  
   Una *Signal* es una envoltura reactiva síncrona que notifica a Angular exactamente qué partes del DOM dependen de su valor cuando cambia, permitiendo una detección de cambios granular (*fine-grained reactivity*) sin necesidad de re-evaluar todo el árbol de componentes.
7. **¿Qué hace el interceptor y por qué se escribe una sola vez?**  
   Actúa como un middleware para todas las peticiones y respuestas HTTP. Se escribe una sola vez para centralizar políticas transversales (como inyectar el token Bearer o interceptar errores 401), evitando duplicar encabezados en cada servicio.
