package com.ecommerce.api.dto;

import java.math.BigDecimal;

public record DetalleOrdenResponse(
        Long productoId,
        String productoNombre,
        Integer cantidad,
        BigDecimal precioUnitario,
        BigDecimal subtotal
) {
}
