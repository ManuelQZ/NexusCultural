package com.uniquindio.nexuscultural.application.dto.request;

import com.uniquindio.nexuscultural.domain.valueobject.Grupo;
import com.uniquindio.nexuscultural.domain.valueobject.Precio;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

//Mapea a Articulo.crear()
//Aqui va la información que el artesano debe ingresar cuando crea un producto

public record CrearArticuloRequest(

        @NotNull(message = "El ID del artesano es obligatorio")
        UUID artesanoId,

        @NotBlank(message = "El título del artículo es obligatorio")
        String titulo,

        @NotBlank(message = "La descripción del artículo es obligatoria")
        String descripcion,

        @Positive(message = "El stock debe ser mayor a cero")
        int stock,

        @NotNull(message = "Se debe agregar el precio del artículo")
        Precio precio,

        @NotNull(message = "El grupo del artículo es obligatorio")
        Grupo grupo,

        @NotNull(message = "Se deben incluir las imágenes del artículo")
        @Size(min = 2, message = "Un artículo debe tener mínimo 2 imágenes")
        List<String> imagenes
) {
    //En esta sección va el código nuestro
    //Cuando no hay nada extar que agregar, como metodos adicionales o
    //validaciones en el contructor compacto, esta sección queda vacia

    //Cuando la lógia está en otra parte, estas llaves quedan vacias
}
