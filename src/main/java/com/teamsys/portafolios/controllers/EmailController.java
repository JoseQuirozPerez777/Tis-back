package com.teamsys.portafolios.controllers;

import com.teamsys.portafolios.dto.EmailRequestDTO;
import com.teamsys.portafolios.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/password")
public class EmailController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/email")
    public ResponseEntity<?> solicitarRecuperacion(@RequestBody EmailRequestDTO request) {
        try {
            // Ejecutamos la lógica (el String 'enviado' no lo usamos directamente en la respuesta por seguridad)
            usuarioService.procesarRecuperacionPassword(request.getCorreo());

            // Retornamos un JSON estructurado
            return ResponseEntity.ok(java.util.Map.of(
                    "success", true,
                    "message", "Si el correo existe en nuestro sistema, recibirá un código de seguridad."
            ));

        } catch (Exception e) {
            // También devolvemos el error como JSON para que el front no explote
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(java.util.Map.of(
                            "success", false,
                            "message", "Error al procesar la solicitud."
                    ));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetearPassword(
            @RequestBody java.util.Map<String, String> body,
            Authentication authentication) { // Spring inyecta el usuario autenticado por el JWT
        try {
            String nuevoPassword = body.get("password");

            // El nombre de usuario (correo) viene del SecurityContext
            String correo = authentication.getName();

            usuarioService.actualizarPasswordDirecto(correo, nuevoPassword);

            return ResponseEntity.ok(java.util.Map.of(
                    "success", true,
                    "message", "Contraseña actualizada correctamente."
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(java.util.Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }
}
