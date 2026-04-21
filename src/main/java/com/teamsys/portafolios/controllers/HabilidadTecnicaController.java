package com.teamsys.portafolios.controllers;

import com.teamsys.portafolios.dto.HabilidadTecnicaRequestDTO;
import com.teamsys.portafolios.dto.HabilidadTecnicaUpdateDTO;
import com.teamsys.portafolios.entities.HabilidadTecnica;
import com.teamsys.portafolios.entities.Usuario;
import com.teamsys.portafolios.repositories.UsuarioRepository;
import com.teamsys.portafolios.services.HabilidadTecnicaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/habilidades-tecnicas")
@CrossOrigin(origins = "*")
public class HabilidadTecnicaController {

    @Autowired
    private HabilidadTecnicaService tecnicaService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/mis-habilidades")
    public ResponseEntity<List<HabilidadTecnica>> listar(Authentication authentication) {
        Usuario usuario = obtenerUsuarioAutenticado(authentication);
        return ResponseEntity.ok(tecnicaService.listarPorUsuario(usuario));
    }

    @PostMapping("/registrar")
    public ResponseEntity<?> crear(@Valid @RequestBody HabilidadTecnicaRequestDTO dto, Authentication authentication) {
        Usuario usuario = obtenerUsuarioAutenticado(authentication);
        HabilidadTecnica nueva = tecnicaService.guardar(dto, usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Habilidad técnica guardada", "id", nueva.getId()));
    }

    @PutMapping("/actualizar")
    public ResponseEntity<?> actualizar(@Valid @RequestBody HabilidadTecnicaUpdateDTO dto, Authentication authentication) {
        Usuario usuario = obtenerUsuarioAutenticado(authentication);
        tecnicaService.actualizar(dto, usuario);
        return ResponseEntity.ok(Map.of("message", "Habilidad técnica actualizada"));
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        tecnicaService.eliminar(id);
        return ResponseEntity.ok(Map.of("message", "Habilidad eliminada correctamente"));
    }

    private Usuario obtenerUsuarioAutenticado(Authentication authentication) {
        return usuarioRepository.findByCorreo(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }
}