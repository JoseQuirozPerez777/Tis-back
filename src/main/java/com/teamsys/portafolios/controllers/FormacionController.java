package com.teamsys.portafolios.controllers;

import com.teamsys.portafolios.dto.FormacionRequestDTO;
import com.teamsys.portafolios.entities.Usuario;
import com.teamsys.portafolios.repositories.UsuarioRepository;
import com.teamsys.portafolios.services.FormacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/formacion")
public class FormacionController {

    @Autowired
    private FormacionService formacionService;
    @Autowired
    private UsuarioRepository usuarioRepository;

    private Usuario getAuthUser(Authentication auth) {
        return usuarioRepository.findByCorreo(auth.getName()).orElseThrow();
    }

    @GetMapping
    public ResponseEntity<?> listar(Authentication auth) {
        return ResponseEntity.ok(Map.of("data", formacionService.obtenerPorUsuario(getAuthUser(auth))));
    }

    @PostMapping("/registrar")
    public ResponseEntity<?> crear(@RequestBody FormacionRequestDTO dto, Authentication auth) {
        return ResponseEntity.ok(Map.of("message", "Educación agregada", "data", formacionService.agregar(dto, getAuthUser(auth))));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editar(@PathVariable Long id, @RequestBody FormacionRequestDTO dto, Authentication auth) {
        return ResponseEntity.ok(Map.of("message", "Educación actualizada", "data", formacionService.actualizar(id, dto, getAuthUser(auth))));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id, Authentication auth) {
        formacionService.eliminar(id, getAuthUser(auth));
        return ResponseEntity.ok(Map.of("message", "Educación eliminada"));
    }
}