package com.teamsys.portafolios.controllers;

import com.teamsys.portafolios.dto.ProyectoRequestDTO;
import com.teamsys.portafolios.dto.ProyectoResponseDTO;
import com.teamsys.portafolios.entities.Usuario;
import com.teamsys.portafolios.repositories.UsuarioRepository;
import com.teamsys.portafolios.services.ProyectoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/proyectos")
public class ProyectoController {

    @Autowired
    private ProyectoService proyectoService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private Usuario getUsuarioAutenticado(Authentication auth) {
        return usuarioRepository.findByCorreo(auth.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    @GetMapping
    public ResponseEntity<?> listar(Authentication auth) {
        try {
            List<ProyectoResponseDTO> proyectos = proyectoService.obtenerProyectosPorUsuario(getUsuarioAutenticado(auth));
            return ResponseEntity.ok(Map.of("success", true, "data", proyectos));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @PostMapping("/registrar")
    public ResponseEntity<?> crear(@RequestBody ProyectoRequestDTO dto, Authentication auth) {
        try {
            ProyectoResponseDTO nuevo = proyectoService.agregarProyecto(dto, getUsuarioAutenticado(auth));
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "success", true, "message", "Proyecto creado", "data", nuevo));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody ProyectoRequestDTO dto, Authentication auth) {
        try {
            ProyectoResponseDTO actualizado = proyectoService.actualizarProyecto(id, dto, getUsuarioAutenticado(auth));
            return ResponseEntity.ok(Map.of("success", true, "message", "Proyecto actualizado", "data", actualizado));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id, Authentication auth) {
        try {
            proyectoService.eliminarProyecto(id, getUsuarioAutenticado(auth));
            return ResponseEntity.ok(Map.of("success", true, "message", "Proyecto eliminado"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}