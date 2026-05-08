package com.teamsys.portafolios.controllers;

import com.teamsys.portafolios.dto.ExperienciaLaboralRequestDTO;
import com.teamsys.portafolios.dto.ExperienciaLaboralResponseDTO;
import com.teamsys.portafolios.entities.Usuario;
import com.teamsys.portafolios.repositories.UsuarioRepository;
import com.teamsys.portafolios.services.ExperienciaLaboralService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/experiencias")
@CrossOrigin(origins = "*")
public class ExperienciaLaboralController {

    @Autowired
    private ExperienciaLaboralService service;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/mis-experiencias")
    public ResponseEntity<List<ExperienciaLaboralResponseDTO>> listar(Authentication authentication) {
        Usuario usuario = obtenerUsuario(authentication);
        return ResponseEntity.ok(service.listarPorUsuario(usuario));
    }

    @PostMapping("/registrar")
    public ResponseEntity<?> crear(@Valid @RequestBody ExperienciaLaboralRequestDTO dto, Authentication authentication) {
        Usuario usuario = obtenerUsuario(authentication);
        ExperienciaLaboralResponseDTO nueva = service.guardar(dto, usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "message", "Experiencia laboral agregada correctamente",
                "data", nueva
        ));
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<?> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ExperienciaLaboralRequestDTO dto,
            Authentication authentication) {

        Usuario usuario = obtenerUsuario(authentication);
        ExperienciaLaboralResponseDTO actualizada = service.actualizar(id, dto, usuario);
        return ResponseEntity.ok(Map.of(
                "message", "Experiencia laboral actualizada correctamente",
                "data", actualizada
        ));
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id, Authentication authentication) {
        // Es recomendable pasar el usuario al service también en delete para validar dueño
        service.eliminar(id);
        return ResponseEntity.ok(Map.of("message", "Experiencia eliminada correctamente"));
    }

    private Usuario obtenerUsuario(Authentication authentication) {
        return usuarioRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }
}