package com.ecommerce.api.service;

import com.ecommerce.api.dto.DetalleOrdenResponse;
import com.ecommerce.api.dto.ItemOrdenRequest;
import com.ecommerce.api.dto.OrdenRequest;
import com.ecommerce.api.dto.OrdenResponse;
import com.ecommerce.api.exception.ResourceNotFoundException;
import com.ecommerce.api.exception.StockInsuficienteException;
import com.ecommerce.api.model.*;
import com.ecommerce.api.repository.OrdenRepository;
import com.ecommerce.api.repository.ProductoRepository;
import com.ecommerce.api.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/** Lógica central del negocio: creación de órdenes con control de stock. */
@Service
@RequiredArgsConstructor
public class OrdenService {

    private final OrdenRepository ordenRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;

    /**
     * Crea una orden de forma ATÓMICA:
     * 1) valida que el usuario exista,
     * 2) por cada ítem valida que el producto exista y que haya stock suficiente,
     * 3) descuenta el stock y calcula subtotales y total.
     *
     * Al ser @Transactional, si CUALQUIER ítem falla (ej: sin stock) se hace rollback de todo:
     * no queda stock descontado a medias ni una orden parcial.
     */
    @Transactional
    public OrdenResponse crear(OrdenRequest request) {
        Usuario usuario = usuarioRepository.findById(request.usuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", request.usuarioId()));

        Orden orden = Orden.builder()
                .usuario(usuario)
                .fecha(LocalDateTime.now())
                .estado(EstadoOrden.CREADA)
                .total(BigDecimal.ZERO)
                .build();

        BigDecimal total = BigDecimal.ZERO;

        for (ItemOrdenRequest item : request.items()) {
            Producto producto = productoRepository.findById(item.productoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Producto", item.productoId()));

            // --- Validación de stock ---
            if (producto.getStock() < item.cantidad()) {
                throw new StockInsuficienteException(
                        producto.getNombre(), producto.getStock(), item.cantidad());
            }

            // --- Descuento de stock (la entidad es managed: se persiste al hacer commit) ---
            producto.setStock(producto.getStock() - item.cantidad());

            BigDecimal subtotal = producto.getPrecio().multiply(BigDecimal.valueOf(item.cantidad()));

            DetalleOrden detalle = DetalleOrden.builder()
                    .producto(producto)
                    .cantidad(item.cantidad())
                    .precioUnitario(producto.getPrecio())   // precio histórico
                    .subtotal(subtotal)
                    .build();

            orden.agregarDetalle(detalle);
            total = total.add(subtotal);
        }

        orden.setTotal(total);
        return toResponse(ordenRepository.save(orden));
    }

    @Transactional(readOnly = true)
    public OrdenResponse obtener(Long id) {
        return ordenRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Orden", id));
    }

    @Transactional(readOnly = true)
    public List<OrdenResponse> listarPorUsuario(Long usuarioId) {
        if (!usuarioRepository.existsById(usuarioId)) {
            throw new ResourceNotFoundException("Usuario", usuarioId);
        }
        return ordenRepository.findByUsuarioIdOrderByFechaDesc(usuarioId).stream()
                .map(this::toResponse)
                .toList();
    }

    /** Convierte la entidad a DTO (se hace dentro de la transacción para poder leer relaciones lazy). */
    private OrdenResponse toResponse(Orden orden) {
        List<DetalleOrdenResponse> detalles = orden.getDetalles().stream()
                .map(d -> new DetalleOrdenResponse(
                        d.getProducto().getId(),
                        d.getProducto().getNombre(),
                        d.getCantidad(),
                        d.getPrecioUnitario(),
                        d.getSubtotal()))
                .toList();

        return new OrdenResponse(orden.getId(), orden.getUsuario().getId(), orden.getFecha(),
                orden.getEstado(), orden.getTotal(), detalles);
    }
}
