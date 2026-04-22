package com.teamsys.portafolios.controllers;

import com.teamsys.portafolios.dto.ExperienciaLaboralRequestDTO;
import com.teamsys.portafolios.dto.ExperienciaLaboralUpdateDTO;
import com.teamsys.portafolios.entities.ExperienciaLaboral;
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
@RequestMapping("/api/experiencia-laboral")
@CrossOrigin(origins = "*")
public class ExperienciaLaboralController {

    @Autowired
    private ExperienciaLaboralService service;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/mis-experiencias")
    public ResponseEntity<List<ExperienciaLaboral>> listar(Authentication authentication) {
        Usuario usuario = obtenerUsuario(authentication);
        return ResponseEntity.ok(service.listarPorUsuario(usuario));
    }

    @PostMapping("/registrar")
    public ResponseEntity<?> crear(@Valid @RequestBody ExperienciaLaboralRequestDTO dto, Authentication authentication) {
        Usuario usuario = obtenerUsuario(authentication);
        ExperienciaLaboral nueva = service.guardar(dto, usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
            "message", "Experiencia laboral agregada correctamente",
            "id", nueva.getId()
        ));
    }

    @PutMapping("/actualizar")
    public ResponseEntity<?> actualizar(@Valid @RequestBody ExperienciaLaboralUpdateDTO dto, Authentication authentication) {
        Usuario usuario = obtenerUsuario(authentication);
        service.actualizar(dto, usuario);
        return ResponseEntity.ok(Map.of("message", "Experiencia laboral actualizada correctamente"));
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.ok(Map.of("message", "Experiencia eliminada correctamente"));
    }

    private Usuario obtenerUsuario(Authentication authentication) {
        return usuarioRepository.findByCorreo(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }
}