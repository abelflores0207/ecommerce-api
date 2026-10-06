package com.ecommerce.api.service;

import com.ecommerce.api.dto.UsuarioRequest;
import com.ecommerce.api.dto.UsuarioResponse;
import com.ecommerce.api.exception.EmailDuplicadoException;
import com.ecommerce.api.exception.ResourceNotFoundException;
import com.ecommerce.api.model.Usuario;
import com.ecommerce.api.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UsuarioResponse crear(UsuarioRequest request) {
        if (usuarioRepository.existsByEmailIgnoreCase(request.email())) {
            throw new EmailDuplicadoException(request.email());
        }
        Usuario usuario = Usuario.builder()
                .nombre(request.nombre())
                .email(request.email().toLowerCase())
                // Se guarda el hash BCrypt, jamás la contraseña original.
                .contrasena(passwordEncoder.encode(request.contrasena()))
                .build();
        return toResponse(usuarioRepository.save(usuario));
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponse> listar() {
        return usuarioRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public UsuarioResponse obtener(Long id) {
        return usuarioRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", id));
    }

    private UsuarioResponse toResponse(Usuario u) {
        return new UsuarioResponse(u.getId(), u.getNombre(), u.getEmail());
    }
}
