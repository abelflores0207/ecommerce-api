package com.ecommerce.api.service.impl;

import com.ecommerce.api.dto.ProductoRequest;
import com.ecommerce.api.dto.ProductoResponse;
import com.ecommerce.api.exception.ResourceNotFoundException;
import com.ecommerce.api.model.Producto;
import com.ecommerce.api.repository.ProductoRepository;
import com.ecommerce.api.service.ProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Implementación de {@link ProductoService}. */
@Service
@RequiredArgsConstructor // inyección por constructor (recomendada sobre @Autowired en campos)
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;

    @Override
    @Transactional
    public ProductoResponse crear(ProductoRequest request) {
        Producto producto = Producto.builder()
                .nombre(request.nombre())
                .descripcion(request.descripcion())
                .precio(request.precio())
                .stock(request.stock())
                .categoria(request.categoria())
                .build();
        return toResponse(productoRepository.save(producto));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoResponse> listar(String categoria) {
        List<Producto> productos = (categoria == null || categoria.isBlank())
                ? productoRepository.findAll()
                : productoRepository.findByCategoriaIgnoreCase(categoria);
        return productos.stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ProductoResponse obtener(Long id) {
        return toResponse(buscar(id));
    }

    @Override
    @Transactional
    public ProductoResponse actualizar(Long id, ProductoRequest request) {
        Producto producto = buscar(id);
        producto.setNombre(request.nombre());
        producto.setDescripcion(request.descripcion());
        producto.setPrecio(request.precio());
        producto.setStock(request.stock());
        producto.setCategoria(request.categoria());
        // No hace falta save(): la entidad es "managed" y JPA persiste los cambios al cerrar la transacción.
        return toResponse(producto);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        productoRepository.delete(buscar(id));
    }

    private Producto buscar(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", id));
    }

    private ProductoResponse toResponse(Producto p) {
        return new ProductoResponse(p.getId(), p.getNombre(), p.getDescripcion(),
                p.getPrecio(), p.getStock(), p.getCategoria());
    }
}
