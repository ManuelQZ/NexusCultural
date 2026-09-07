# Glosario del Lenguaje Ubicuo - [Nexus Cultural]

## Conceptos Centrales

## ¿Quién compra y por qué?

El nicho central serian aquellos que se encuentren interesados en coleccionar piezas artesanales de alto valor cultural y aquellos turistas que quieran conservar un recuerdo de su visita.

## ¿Qué hace único a sus vendedores?

El valor cultural que aporta cada vendedor tanto por su edad ya que es centrado para adultos mayores y aquellos artesanos interesados en mostrar sus productos locales.

## Lenguaje Ublicuo


### [Origen]
**Definición:** [Representa la tierra, el resguardo o el municipio de creación del producto]

**Sinónimos aceptados:** [Territorio / Lote]
**No usar:** [Comunidad / Ubicación]

**Ejemplo de uso en código:**
\`\`\`java
[una línea de código de ejemplo usando este término]
\`\`\`

---

### [Artesano]
**Definición:** [Es el vendedor o creador de los productos]

**Sinónimos aceptados:** [Maestro]
**No usar:** [Vendedor]

**Ejemplo de uso:**
\`\`\`java
[...]
\`\`\`

---

### [Articulo]
**Definición:** [Las artesanías hechas a mano osea los productos a vender]

**Sinónimos aceptados:** [Pieza]
**No usar:** [Producto]

**Ejemplo de uso en código:**
\`\`\`java
[una línea de código de ejemplo usando este término]
\`\`\`

---

### [HistoriaProducto]
**Definición:** [Seria la descripción narrativa del Origen (historia personal o tradicional), la Técnica Ancestral empleada y la Región de Procedencia.]

**Sinónimos aceptados:** [Relato]
**No usar:** [Descripción]

**Ejemplo de uso en código:**
\`\`\`java
[una línea de código de ejemplo usando este término]
\`\`\`

---

### [Reseña]
**Definición:** [Es el medio por el cual es comprador da fe del valor de la obra y del impacto del producto.]

**Sinónimos aceptados:** [Valoración / Calificación]
**No usar:** [Comentario]

**Ejemplo de uso en código:**
\`\`\`java
[una línea de código de ejemplo usando este término]
\`\`\`

---

### [Galeria]
**Definición:** [Representa todos los productos en exposición.]

**Sinónimos aceptados:** [Exhibición]
**No usar:** [Catalogo]

**Ejemplo de uso en código:**
\`\`\`java
[una línea de código de ejemplo usando este término]
\`\`\`

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



## Reglas Innegociables

1. Ninguna artesania puede ser publicada en el catalogo sin incluir la descripción de su tecnica de elaboración o la historia cultural asociada.
2. Cada artesano debe mencionar a que categoria cultural corresponde cada producto.
3. Cada artesania debe tener la ficha de identidad de su creador.
4. Cada producto debe tener minimo 2 imagenes expositorias del mismo. 
5. Cada producto debe mostrar el stock en tiempo real y al terminarse debe realizarse un softdelete automatico.
6. El costo de envio sera calculado de acuerdo al peso del producto y anexado directamente a la facturación.
7. Se definiran roles especificos de acuerdo al tipo conj permisos establecidos para cada uno.(Un artesano no puede comprar sus propios productos, pero si podria comprar aquellos de otros artesanos).

