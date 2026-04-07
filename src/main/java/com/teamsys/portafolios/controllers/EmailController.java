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

@RestController
@RequestMapping("/api/password")
public class EmailController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/codigo-verificacion")
    public ResponseEntity<?> solicitarRecuperacion(@RequestBody EmailRequestDTO request) {
        try {
            String enviado = usuarioService.procesarRecuperacionPassword(request.getCorreo());

            if (enviado!=null) {
                return ResponseEntity.ok("Si el correo existe en nuestro sistema, recibirá un código de seguridad.");
            } else {
                // Usamos el mismo mensaje por seguridad (User Enumeration Protection)
                return ResponseEntity.ok("Si el correo existe en nuestro sistema, recibirá un código de seguridad.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al procesar la solicitud.");
        }
    }
}
