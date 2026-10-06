package com.ecommerce.api.dto;

/** Nunca se expone la contraseña (ni siquiera hasheada) en las respuestas. */
public record UsuarioResponse(Long id, String nombre, String email) {
}
