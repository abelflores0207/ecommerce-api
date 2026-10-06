package com.ecommerce.api.repository;

import com.ecommerce.api.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    // Query derivada: Spring Data genera el SQL a partir del nombre del método.
    List<Producto> findByCategoriaIgnoreCase(String categoria);
}
