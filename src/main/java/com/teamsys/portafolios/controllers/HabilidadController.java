package com.teamsys.portafolios.controllers;

import com.teamsys.portafolios.dto.HabilidadRequestDTO;
import com.teamsys.portafolios.entities.Categoria;
import com.teamsys.portafolios.entities.Habilidad;
import com.teamsys.portafolios.entities.Usuario;
import com.teamsys.portafolios.repositories.UsuarioRepository;
import com.teamsys.portafolios.services.HabilidadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import java.util.List;

@RestController
@RequestMapping("/api/habilidades")
@CrossOrigin(origins = "*") // Ajustar según tu configuración de seguridad
public class HabilidadController {

    @Autowired
    private HabilidadService habilidadService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/categorias")
    public ResponseEntity<?> obtenerCategorias() {
        try {
            List<Categoria> categorias = habilidadService.obtenerTodasLasCategorias();

            // Si la lista está vacía, igual devolvemos 200 con lista vacía o podrías manejar un 204
            return ResponseEntity.ok(categorias);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al obtener categorías: " + e.getMessage());
        }
    }

    @PostMapping("/registrar")
    public ResponseEntity<?> crearHabilidad(@RequestBody HabilidadRequestDTO dto, Authentication authentication) {
        try {
            // 1. Obtener usuario autenticado
            String correo = authentication.getName();
            Usuario usuario = usuarioRepository.findByCorreo(correo)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            // 2. Guardar
            Habilidad nuevaHabilidad = habilidadService.guardarHabilidad(dto, usuario);

            return ResponseEntity.status(HttpStatus.CREATED).body(java.util.Map.of(
                    "success", true,
                    "message", "Habilidad agregada correctamente",
                    "id", nuevaHabilidad.getIdHabilidad()
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }
}