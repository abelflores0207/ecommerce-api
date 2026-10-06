package com.ecommerce.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/** Un producto y la cantidad que se quiere comprar. */
public record ItemOrdenRequest(
        @NotNull(message = "El productoId es obligatorio") Long productoId,
        @NotNull(message = "La cantidad es obligatoria")
        @Min(value = 1, message = "La cantidad mínima es 1") Integer cantidad
) {
}
