#Mapeo Dominio -> API - Articulo

|Operación del dominio | Método HTTP | Endpoint |
|---|---|---|
|Articulo.crear(...)|POST|/articulos|
|Consultar una articulo|GET|/articulos/{id}|
|inactivar()|PUT|/articulos/{id}/inactivar|
|reactivar()|PUT|/articulos/{id}/inactivar|
|reponerStock(cantidad)|PUT|/articulos/{id}/stock|
|eliminar(tienePedidos)|DELETE|/articulos/{id}|


#Mapeo Dominio -> API - Devolución

|Operación del dominio | Método HTTP | Endpoint |
|---|---|---|
|Devolucion.crear(...)|POST|/devoluciones|
|Consultar estado de devolución|GET|/devoluciones/{id}|
|Devolucion.aprobar()|PUT|/devoluciones/{id}/aprobar|
|Devolucion.rechazar()|PUT|/devoluciones/{id}/rechazar|


