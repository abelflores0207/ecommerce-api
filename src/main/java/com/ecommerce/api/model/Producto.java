package com.ecommerce.api.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * Producto del catálogo.
 * Se usan @Getter/@Setter (y no @Data) en las entidades para evitar problemas
 * con equals/hashCode/toString en relaciones JPA.
 */
@Entity
@Table(name = "productos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(length = 1000)
    private String descripcion;

    // BigDecimal para dinero: evita errores de redondeo de double/float.
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal precio;

    @Column(nullable = false)
    private Integer stock;

    @Column(length = 80)
    private String categoria;

    /**
     * Bloqueo optimista: si dos órdenes simultáneas modifican el stock del mismo producto,
     * la segunda falla en vez de pisar silenciosamente el valor (se responde 409).
     */
    @Version
    private Long version;
}
