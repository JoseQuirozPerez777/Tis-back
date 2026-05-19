package com.teamsys.portafolios.controllers;

import com.teamsys.portafolios.dto.PortafolioCompletoDTO;
import com.teamsys.portafolios.entities.Usuario;
import com.teamsys.portafolios.repositories.UsuarioRepository;
import com.teamsys.portafolios.services.PortafolioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/portafolio")
@CrossOrigin(origins = "*")
public class PortafolioPublicoController {

    @Autowired
    private PortafolioService portafolioService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/mi-resumen")
    public ResponseEntity<PortafolioCompletoDTO> obtenerPortafolioCompleto(Authentication authentication) {
        Usuario usuario = usuarioRepository.findByCorreo(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        PortafolioCompletoDTO portafolio = portafolioService.compilarPortafolio(usuario);
        return ResponseEntity.ok(portafolio);
    }
}