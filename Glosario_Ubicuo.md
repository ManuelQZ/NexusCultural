# Glosario del Lenguaje Ubicuo - [Nexus Cultural]

## Conceptos Centrales

## ¿Quién compra y por qué?

El nicho central serian aquellos que se encuentren interesados en coleccionar piezas artesanales de alto valor cultural y aquellos turistas que quieran conservar un recuerdo de su visita.

## ¿Qué hace único a sus vendedores?

El valor cultural que aporta cada vendedor tanto por su edad ya que es centrado para adultos mayores y aquellos artesanos interesados en mostrar sus productos locales.

## Lenguaje Ublicuo


### [Origen]
**Definición:** [Representa la tierra, el resguardo o el municipio de creación artesanal donde habita el creador y nace la pieza.]

**Sinónimos aceptados:** [Territorio / Lote]
**No usar:** [Comunidad / Ubicación]

**Ejemplo en conversación:**
"Debemos validar que cada pieza registre su Origen antes de desplegarla en la exhibición."

**Ejemplo de uso en código:**
//
Origen origen = Origen.de("Filandia", "Quindío", "Resguardo Chami");
//

---

### [Artesano]
**Definición:** [Rol de usuario correspondiente al adulto mayor custodio de saberes tradicionales que elabora y gestiona las artesanías.]

**Sinónimos aceptados:** [Maestro / Creador]
**No usar:** [Vendedor / Proveedor]

**Ejemplo en conversación:** 
"El Artesano puede autenticarse mediante un código SMS de un solo uso sin necesidad de recordar contraseñas."

**Ejemplo de uso en código:**
//
Artesano artesano = Artesano.registrar(artesanoId, "Don José", celularSMS, origen);
//

---

### [Articulo]
**Definición:** [Obra o artesanía física hecha a mano por el adulto mayor que se encuentra disponible para su preservación y adquisición.]

**Sinónimos aceptados:** [Pieza / Obra]
**No usar:** [Producto / Item]

**Ejemplo en conversación:** 
"Cada Articulo debe estar aprobado por un moderador antes de ponerse visible en la galería."

**Ejemplo de uso en código:**
//
Articulo articulo = Articulo.crear(articuloId, "Sombrero Tejido", precio, historia, artesanoId);
//

---

### [HistoriaProducto]
**Definición:** [Seria la descripción narrativa del Origen (historia personal o tradicional), la Técnica Ancestral empleada y la Región de Procedencia.]

**Sinónimos aceptados:** [Relato]
**No usar:** [Descripción / Metadata]

**Ejemplo en conversación:** 
"El dominio no debe permitir la publicación de un artículo si su HistoriaProducto está vacía."

**Ejemplo de uso en código:**
//
HistoriaProducto historia = HistoriaProducto.documentar(
    "Tejido a mano con fibra de iraca heredado de tres generaciones", 
    TecnicaAncestral.TEJEDURIA, 
    origen
);
//

---

### [Reseña]
**Definición:** [Valoración cualitativa y cuantitativa (1 a 5 estrellas) otorgada por quien adquirió el artículo para dar fe de su valor patrimonial.]

**Sinónimos aceptados:** [Valoración / Calificación]
**No usar:** [Comentario / Review]

**Ejemplo en conversación:** 
"Un usuario solo puede registrar una Reseña si existe una adquisición completada en el sistema."

**Ejemplo de uso en código:**
//
Reseña reseña = Reseña.publicar(Calificacion.CINCO_ESTRELLAS, "Hermosa pieza y excelente relato del artesano", clienteId);
//

---

### [Galeria]
**Definición:** [Módulo o espacio de la plataforma que expone las artesanías aprobadas para su exploración y búsqueda cultural.]

**Sinónimos aceptados:** [Exhibición]
**No usar:** [Catalogo / Inventario]

**Ejemplo en conversación:** 
"La Galeria aplicará filtros predictivos por técnica ancestral y región de origen."

**Ejemplo de uso en código:**
//
List<Articulo> galeria = galeriaRepository.obtenerArticulosAprobados(origen, tecnica);
//

---

### [Grupo]
**Definición:** [Clasificación por especialidad o técnica ancestral (como tejeduría, alfarería, cestería o talla en madera) que organiza y agrupa los artículos dentro de la galería.]

**Sinónimos aceptados:** [Colección]
**No usar:** [Categoria / Tipos]

**Ejemplo en conversación:** 
"Al momento de registrar un nuevo artículo, el artesano debe seleccionar el grupo correspondiente a su técnica tradicional."

**Ejemplo de uso en código:**
//
Grupo grupo = Grupo.TEJEDURIA;
List<Articulo> articulos = galeriaRepository.buscarPorGrupo(grupo);
//

---

### [Contacto]
**Definición:** [Canal de comunicación o interacción directa que se abre entre un comprador y un Artesano para consultar detalles sobre una obra o su valor tradicional.]

**Sinónimos aceptados:** [Enlace]
**No usar:** [Chat]

**Ejemplo en conversación:** 
"El comprador puede iniciar un vinculo con el artesano antes de solicitar la adquisición."

**Ejemplo de uso en código:**
//
Contacto contacto = Contacto.abrir(compradorId, artesanoId, articuloId);
//

---



## Anti-patrones (Términos a EVITAR en nuestro proyecto)

| No usar | Usar |
|---|---|
| [Ubicación] | [Origen] |
| [Vendedor] | [Artesano] |
| [Producto] | [Articulo] |
| [Descripción] | [HistoriaProducto] |
| [Comentario] | [Reseña] |
| [Catalogo] | [Galeria] |
| [Categoria] | [Grupo] |
| [Chat] | [Contacto] |



## Reglas Innegociables

1. Ninguna artesania puede ser publicada en el catalogo sin incluir la descripción de su tecnica de elaboración o la historia cultural asociada.
2. Cada artesano debe mencionar a que categoria cultural corresponde cada producto.
3. Cada artesania debe tener la ficha de identidad de su creador.
4. Cada producto debe tener minimo 2 imagenes expositorias del mismo. 
5. Cada producto debe mostrar el stock en tiempo real y al terminarse debe realizarse un softdelete automatico.
6. El costo de envio sera calculado de acuerdo al peso y volumen del producto y anexado directamente a la facturación.
7. Se definiran roles especificos de acuerdo al tipo con permisos establecidos para cada uno.(Un artesano no puede comprar sus propios productos, pero si podria comprar aquellos de otros artesanos).

