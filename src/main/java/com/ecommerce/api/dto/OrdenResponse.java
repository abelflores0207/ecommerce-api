package com.ecommerce.api.dto;

import com.ecommerce.api.model.EstadoOrden;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrdenResponse(
        Long id,
        Long usuarioId,
        LocalDateTime fecha,
        EstadoOrden estado,
        BigDecimal total,
        List<DetalleOrdenResponse> detalles
) {
}
