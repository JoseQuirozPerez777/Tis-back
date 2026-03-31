package com.teamsys.portafolios.controllers;

import com.teamsys.portafolios.dto.UsuarioRegistroDTO;
import com.teamsys.portafolios.entities.Usuario;
import com.teamsys.portafolios.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController // Indica que esta clase recibe peticiones REST (JSON)
@RequestMapping("/api/usuarios") // Esta es la ruta base
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    // Aquí es donde "aterriza" la petición POST
    @PostMapping("/registro")
    public ResponseEntity<?> registrarUsuario(@RequestBody UsuarioRegistroDTO registroDTO) {
        // @RequestBody le dice a Spring que convierta el JSON del Front en un objeto Java
        try {
            Usuario nuevoUsuario = usuarioService.registrar(registroDTO);
            return ResponseEntity.ok("Usuario registrado con éxito");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}