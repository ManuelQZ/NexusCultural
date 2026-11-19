# Nexus Cultural • Frontend Web
> **Entrega 3: Frontend conectado a su backend**  
> **Asignatura:** Programación Avanzada · Universidad del Quindío   

---

## 1. Información General del Proyecto
* **Nicho de Mercado:** Marketplace E-commerce de Comercio Justo y Artesanías Tradicionales Colombianas (*Nexus Cultural*). Plataforma para conectar comunidades indígenas y maestros artesanos (Wayuu, Chamba, Nariño, Putumayo) con compradores nacionales e internacionales, garantizando autenticidad y trazabilidad de pagos mediante Wompi Bancolombia.
* **Integrantes:**
  * Wilson Delgado
  * Equipo de Desarrollo Nexus Cultural

---

## 2. Cómo Ejecutar el Proyecto en Local

### 2.1 Requisitos Previos
* **Node.js:** Versión `>= 22.22.3` (Recomendado `v22.23.4` mediante NVM).
* **NPM:** Versión `>= 10.9.0`.
* **Angular CLI:** Versión 22.
* **Java SDK (Solo si deseas correr el backend en local):** JDK 21.

---

### 2.2 Opción 1: Ejecución del Frontend conectado al Backend en AWS (Recomendada)
Esta opción es inmediata: el backend ya se encuentra desplegado, conectado a PostgreSQL en AWS RDS, con almacenamiento en AWS S3 y pasarela Wompi en la nube.

1. Abre la terminal y entra a la carpeta del frontend:
   ```bash
   cd nexus-cultural-web
   ```
2. Instala las dependencias de Node:
   ```bash
   npm install
   ```
3. Inicia el servidor de desarrollo con proxy CORS integrado:
   ```bash
   ng serve -o
   ```
4. Abre tu navegador web en:
   ```
   http://localhost:4200
   ```

> **¿Cómo funciona el Proxy CORS?**  
> El comando `npm start` ejecuta `ng serve --proxy-config proxy.conf.json`. Las peticiones `/api/*` se envían al proxy local de desarrollo, el cual las reenvía a `https://d3gt0752ebjv7t.cloudfront.net` enmascarando las cabeceras `Origin` y `Referer`, evitando cualquier bloqueo de CORS en Firefox, Chrome o Edge.

---

### 2.3 Opción 2: Ejecución Completa en Local (Frontend + Backend en Spring Boot)

Si deseas levantar el backend en tu propia máquina:

#### Paso 1: Iniciar el Backend (Spring Boot en puerto 8080)
1. Abre una nueva terminal y navega a la carpeta del backend:
   ```bash
   cd NexusCultural
   ```
2. Ejecuta el backend con Gradle:
   ```bash
   ./gradlew bootRun
   ```
   *El backend quedará escuchando en:* `http://localhost:8080/api`

#### Paso 2: Conectar el Frontend al Backend Local
1. En [proxy.conf.json](proxy.conf.json), cambia el objetivo (`target`) a tu servidor local:
   ```json
   {
     "/api": {
       "target": "http://localhost:8080",
       "secure": false,
       "changeOrigin": true
     }
   }
   ```
2. Inicia el frontend:
   ```bash
   cd nexus-cultural-web
   npm start
   ```
3. Abre tu navegador en `http://localhost:4200`.

---

## 3. Usuarios de Prueba para Sustentación

El sistema de Nexus Cultural utiliza autenticación moderna **basada en Código de Acceso OTP y Tokens JWT** (`/api/usuarios/solicitar` y `/api/usuarios/verificar`), enviando un código temporal de 6 dígitos numéricos:

| Rol | Correo de Prueba | Tipo de Acceso / Panel Habilitado |
| :--- | :--- | :--- |
| **Comprador Cultural** | `comprador@nexuscultural.com` *(o cualquier correo registrado en `/registro`)* | Catálogo Paginado, Detalle con Narrador por Voz TTS, Carrito persistido, Checkout con Wompi Sandbox (VISA 4242) e Historial de Compras con Devoluciones. |
| **Maestro Artesano** | `artesano@nexuscultural.com` *(o registrado en `/registro`)* | Taller Artesanal, Carga de Fotografías de Obras, Publicación con Formulario Reactivo y Reposición de Inventario. |
| **Administrador** | `admin@nexuscultural.com` | Panel de Administración, Creación interna de Administradores, Moderación y Gestión de Solicitudes de Devolución. |

---

## 4. Lista de Pantallas y Rutas del Sistema

| Ruta | Componente | Descripción | Protección / Guard |
| :--- | :--- | :--- | :--- |
| `/` | `Inicio` | Pantalla de bienvenida limpia con botones de Iniciar Sesión y Registro. | Pública |
| `/login` | `Login` | Autenticación OTP en dos pasos (Correo $\rightarrow$ Código 6 dígitos) con Formulario Reactivo y JWT. | `guestGuard` (expulsa a quien ya tiene sesión) |
| `/registro` | `Registro` | Registro público exclusivo para Compradores y Maestros Artesanos. | `guestGuard` |
| `/catalogo` | `Catalogo` | Catálogo oficial paginado (`page`, `size`), búsqueda por texto y filtros de categorías. | `authGuard` |
| `/detalle-producto` | `DetalleProducto` | Detalle dinámico de la pieza por ID, galería, stock y narrador asistido por voz TTS. | `authGuard` |
| `/carrito` | `Carrito` | Carrito de compras reactivo (`CarritoService`) persistido en `localStorage` con badge en header. | `authGuard` |
| `/checkout` | `Checkout` | Proceso de compra con autocompletado de perfil y **Pasarela Oficial Wompi Sandbox (VISA 4242)**. | `authGuard` |
| `/pedidos` | `Pedidos` | Historial real de compras del comprador consultado en la nube, trazabilidad y radicación de devoluciones. | `authGuard` |
| `/artesano` | `ArtesanoPanel` | Panel exclusivo del artesano: inventario, reposición de stock, subida de fotos y **Formulario Reactivo de publicación**. | `artisanGuard` (solo rol ARTESANO) |
| `/admin` | `AdminPanel` | Panel administrativo: creación interna de administradores, moderación y resolución de devoluciones. | `adminGuard` (solo rol ADMINISTRADOR) |

---

## 5. Rúbrica de Evaluación y Extras Implementados

### 5.1 Criterios Mínimos (Sección 2 de la Guía)
1. **Conexión y Servicios:** Interfaces espejo en TypeScript para cada DTO del backend, servicios modulares por dominio (`CatalogoService`, `PedidoService`, `AuthService`, `ArticuloService`, `StorageService`) y CORS resuelto. Cero datos quemados.
2. **Login JWT:** Formulario reactivo con validaciones, almacenamiento en `localStorage`, menú dinámico según rol, cierre de sesión sin recargar y traducción de errores.
3. **Interceptor HTTP:** `authInterceptor` añade `Authorization: Bearer <token>` automáticamente y captura errores **401 y 403** (sesión expirada o token inválido) para cerrar sesión, notificar con toast y redirigir a `/login`.
4. **Guards:** `authGuard`, `guestGuard`, `artisanGuard` y `adminGuard`.
5. **Catálogo Paginado y Búsqueda:** Parámetros `page` y `size` enviados al backend, botones Anterior/Siguiente, indicador *"Página X de Y"* y búsqueda en servidor.
6. **Operación de Negocio con Formulario Reactivo:** Publicación de artesanías mediante `FormGroup` con validaciones sincronizadas con las reglas de dominio del backend (título $\ge 5$, descripción $\ge 50$, stock $\ge 1$), desactivación de botón mientras envía y traducción de códigos HTTP (400, 401, 403, 404).
7. **Avisos (Notificaciones):** Servicio de Toasts temporales (`NotificacionesService`) para éxito, advertencia y error que se auto-destruyen a los 4 segundos.
8. **Bootstrap y Responsive:** Clases formales de Bootstrap 5, navbar colapsable hamburguesa y tarjetas adaptables a pantallas móviles.
9. **Funcionalidad Propia:** Integración de la **Pasarela Wompi Sandbox** (`POST /api/pagos/wompi/confirmar`) reflejando la transacción en la nube de Wompi.

### 5.2 Extras Implementados (+18 puntos)
* **Guard por rol (+3 pts):** Rutas exclusivas separadas para comprador, artesano y administrador.
* **BD Relacional en Producción (+4 pts):** PostgreSQL desplegado en AWS RDS con persistencia real.
* **Despliegue en la Nube (+5 pts):** Backend y API desplegados con HTTPS en AWS CloudFront.
* **Archivos e Imágenes Externas (+4 pts):** Carga binaria a AWS S3 mediante Presigned URLs (cero base64 en base de datos).
* **Buenas Prácticas (+2 pts):** Uso de `environment.ts`, Signals de Angular 22 y componentes modulares.
