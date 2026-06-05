package com.teamsys.portafolios.controllers;

import com.teamsys.portafolios.dto.PortafolioVisibilidadElementosDTO;
import com.teamsys.portafolios.dto.VisibilidadElementosMasivaDTO;
import com.teamsys.portafolios.dto.VisibilidadPerfilDTO;
import com.teamsys.portafolios.entities.Usuario;
import com.teamsys.portafolios.repositories.UsuarioRepository;
import com.teamsys.portafolios.services.VisibilidadElementosService;
import com.teamsys.portafolios.services.VisibilidadPerfilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/visibilidad")
@CrossOrigin(origins = "*")
public class VisibilidadPerfilController {

    @Autowired
    private VisibilidadElementosService visibilidadElementosService;
    @Autowired
    private VisibilidadPerfilService visibilidadPerfilService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/mis-ajustes")
    public ResponseEntity<?> obtenerMisAjustes(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body("No autorizado");
        }
        try {
            Usuario usuario = getUsuarioAutenticado(authentication);
            VisibilidadPerfilDTO dto = visibilidadPerfilService.obtenerVisibilidadConfig(usuario);
            return ResponseEntity.ok(dto);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/guardar-ajustes")
    public ResponseEntity<?> guardarAjustes(@RequestBody VisibilidadPerfilDTO visibilidadDTO, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body("No autorizado");
        }
        try {
            Usuario usuario = getUsuarioAutenticado(authentication);
            visibilidadPerfilService.guardarVisibilidadConfig(usuario, visibilidadDTO);
            return ResponseEntity.ok(java.util.Map.of("message", "Configuración de privacidad actualizada correctamente."));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private Usuario getUsuarioAutenticado(Authentication auth) {
        return usuarioRepository.findByCorreo(auth.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    @PutMapping("/guardar-elementos")
    public ResponseEntity<?> guardarVisibilidadElementosMasiva(
            @RequestBody VisibilidadElementosMasivaDTO dtoGlobal,
            Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body("No autorizado");
        }

        try {
            // Verificamos que el usuario exista de forma segura mediante token
            getUsuarioAutenticado(authentication);

            visibilidadElementosService.guardarVisibilidadMasiva(dtoGlobal);
            return ResponseEntity.ok(Map.of("message", "Visibilidad de los elementos del portafolio guardada con éxito."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/mis-elementos")
    public ResponseEntity<?> obtenerMisElementosVisibilidad(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body("No autorizado");
        }
        try {
            Usuario usuario = getUsuarioAutenticado(authentication);
            PortafolioVisibilidadElementosDTO dto = visibilidadElementosService.obtenerVisibilidadElementos(usuario);
            return ResponseEntity.ok(dto);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}