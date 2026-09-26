package com.uniquindio.nexuscultural.application.dto.request;

import com.uniquindio.nexuscultural.domain.valueobject.Precio;
import jakarta.validation.constraints.NotBlank;

//Mapea a Articulo.crear()
//Aqui va la información que el artesano debe ingresar cuando crea un producto

public record CrearArticuloRequest(

        //En los campos que no pueden ir vacios ni nulls, deben anteponerse el @NotBlank
        //El mensaje es el texto que va a ver el cliente si falla la validación.
        @NotBlank(message = "El título del artículo es obligatorio")
        String titulo,

        @NotBlank(message = "La descripción del artículo es obligatoria")
        String descripcion,

        @NotBlank(message = "Se debe incluir la cantidad disponible")
        int stock,

        @NotBlank(message = "Se debe agregar el precio del artículo")
        Precio precio
) {
    //En esta sección va el código nuestro
    //Cuando no hay nada extar que agregar, como metodos adicionales o
    //validaciones en el contructor compacto, esta sección queda vacia

    //Cuando la lógia está en otra parte, estas llaves quedan vacias
}
