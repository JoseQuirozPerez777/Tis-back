package com.teamsys.portafolios.controllers;

import com.teamsys.portafolios.dto.RedSocialRequestDTO;
import com.teamsys.portafolios.dto.RedSocialResponseDTO;
import com.teamsys.portafolios.entities.Usuario;
import com.teamsys.portafolios.repositories.UsuarioRepository;
import com.teamsys.portafolios.services.RedSocialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/redes-sociales")
public class RedSocialController {

    @Autowired
    private RedSocialService redSocialService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // 1. Obtener redes sociales del usuario autenticado
    @GetMapping
    public ResponseEntity<?> obtenerRedes(Authentication authentication) {
        try {
            String correo = authentication.getName();
            Usuario usuario = usuarioRepository.findByCorreo(correo)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            List<RedSocialResponseDTO> redes = redSocialService.obtenerRedesPorUsuario(usuario);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", redes
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    // 2. Agregar una lista de redes sociales
    @PostMapping("/registrar")
    public ResponseEntity<?> agregarRedes(
            @RequestBody List<RedSocialRequestDTO> redesDTO,
            Authentication authentication) {
        try {
            String correo = authentication.getName();
            Usuario usuario = usuarioRepository.findByCorreo(correo)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            List<RedSocialResponseDTO> redesGuardadas = redSocialService.agregarRedes(redesDTO, usuario);

            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "success", true,
                    "message", "Redes sociales guardadas correctamente",
                    "data", redesGuardadas
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    // 3. Actualizar una red social del usuario autenticado
    @PutMapping("/{idRed}")
    public ResponseEntity<?> actualizarRed(
            @PathVariable Long idRed,
            @RequestBody RedSocialRequestDTO dto,
            Authentication authentication) {
        try {
            String correo = authentication.getName();
            Usuario usuario = usuarioRepository.findByCorreo(correo)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            RedSocialResponseDTO redActualizada =
                    redSocialService.actualizarRedSocial(idRed, dto, usuario);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Red social actualizada correctamente",
                    "data", redActualizada
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    // 4. Eliminar una red social del usuario autenticado
    @DeleteMapping("/{idRed}")
    public ResponseEntity<?> eliminarRed(
            @PathVariable Long idRed,
            Authentication authentication) {
        try {
            String correo = authentication.getName();
            Usuario usuario = usuarioRepository.findByCorreo(correo)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            redSocialService.eliminarRedSocial(idRed, usuario);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Red social eliminada correctamente"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }
}