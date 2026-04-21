package com.teamsys.portafolios.controllers;

import com.teamsys.portafolios.dto.HabilidadBlandaRequestDTO;
import com.teamsys.portafolios.dto.HabilidadBlandaUpdateDTO;
import com.teamsys.portafolios.entities.HabilidadBlanda;
import com.teamsys.portafolios.entities.Usuario;
import com.teamsys.portafolios.repositories.UsuarioRepository;
import com.teamsys.portafolios.services.HabilidadBlandaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/habilidades-blandas")
@CrossOrigin(origins = "*")
public class HabilidadBlandaController {

    @Autowired
    private HabilidadBlandaService blandaService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/mis-habilidades")
    public ResponseEntity<List<HabilidadBlanda>> listar(Authentication authentication) {
        Usuario usuario = obtenerUsuarioAutenticado(authentication);
        return ResponseEntity.ok(blandaService.listarPorUsuario(usuario));
    }

    @PostMapping("/registrar")
    public ResponseEntity<?> crear(@Valid @RequestBody HabilidadBlandaRequestDTO dto, Authentication authentication) {
        Usuario usuario = obtenerUsuarioAutenticado(authentication);
        HabilidadBlanda nueva = blandaService.guardar(dto, usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Habilidad blanda guardada", "id", nueva.getId()));
    }

    @PutMapping("/actualizar")
    public ResponseEntity<?> actualizar(@Valid @RequestBody HabilidadBlandaUpdateDTO dto, Authentication authentication) {
        Usuario usuario = obtenerUsuarioAutenticado(authentication);
        blandaService.actualizar(dto, usuario);
        return ResponseEntity.ok(Map.of("message", "Habilidad blanda actualizada"));
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        blandaService.eliminar(id);
        return ResponseEntity.ok(Map.of("message", "Habilidad eliminada correctamente"));
    }

    private Usuario obtenerUsuarioAutenticado(Authentication authentication) {
        return usuarioRepository.findByCorreo(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }
}