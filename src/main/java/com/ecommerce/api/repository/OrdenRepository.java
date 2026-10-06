package com.ecommerce.api.repository;

import com.ecommerce.api.model.Orden;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrdenRepository extends JpaRepository<Orden, Long> {

    // EntityGraph: trae detalles y productos en una sola consulta (evita el problema N+1).
    @Override
    @EntityGraph(attributePaths = {"detalles", "detalles.producto"})
    Optional<Orden> findById(Long id);

    @EntityGraph(attributePaths = {"detalles", "detalles.producto"})
    List<Orden> findByUsuarioIdOrderByFechaDesc(Long usuarioId);
}
