package com.ecommerce.api.service;

import com.ecommerce.api.dto.ProductoRequest;
import com.ecommerce.api.dto.ProductoResponse;

import java.util.List;

/** Contrato de la lógica de negocio del catálogo de productos. */
public interface ProductoService {

    ProductoResponse crear(ProductoRequest request);

    /** Lista todos los productos o, si se indica categoría, solo los de esa categoría. */
    List<ProductoResponse> listar(String categoria);

    ProductoResponse obtener(Long id);

    ProductoResponse actualizar(Long id, ProductoRequest request);

    void eliminar(Long id);
}
