package com.ecommerce.api.controller;

import com.ecommerce.api.dto.OrdenRequest;
import com.ecommerce.api.dto.OrdenResponse;
import com.ecommerce.api.service.OrdenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/ordenes")
@RequiredArgsConstructor
public class OrdenController {

    private final OrdenService ordenService;

    @PostMapping
    public ResponseEntity<OrdenResponse> crear(@Valid @RequestBody OrdenRequest request) {
        OrdenResponse creada = ordenService.crear(request);
        return ResponseEntity.created(URI.create("/api/ordenes/" + creada.id())).body(creada);
    }

    @GetMapping("/{id}")
    public OrdenResponse obtener(@PathVariable Long id) {
        return ordenService.obtener(id);
    }

    /** GET /api/ordenes?usuarioId=1 → historial de compras de un usuario. */
    @GetMapping
    public List<OrdenResponse> listarPorUsuario(@RequestParam Long usuarioId) {
        return ordenService.listarPorUsuario(usuarioId);
    }
}
