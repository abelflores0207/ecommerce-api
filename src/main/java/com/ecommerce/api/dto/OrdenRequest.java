package com.ecommerce.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record OrdenRequest(
        @NotNull(message = "El usuarioId es obligatorio") Long usuarioId,
        @NotEmpty(message = "La orden debe tener al menos un producto")
        @Valid List<ItemOrdenRequest> items
) {
}
