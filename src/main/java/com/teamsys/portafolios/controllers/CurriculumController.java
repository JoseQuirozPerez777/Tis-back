package com.teamsys.portafolios.controllers;

import com.teamsys.portafolios.dto.CurriculumDTO;
import com.teamsys.portafolios.dto.CurriculumRequestDTO;
import com.teamsys.portafolios.entities.Usuario;
import com.teamsys.portafolios.repositories.UsuarioRepository;
import com.teamsys.portafolios.services.CurriculumService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cvs")
@CrossOrigin(origins = "*")
public class CurriculumController {

    @Autowired
    private CurriculumService curriculumService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // 1. LEER (LISTAR TODOS LOS CVS DEL USUARIO)
    @GetMapping("/mis-cvs")
    public ResponseEntity<List<CurriculumDTO>> listarCvs(Authentication authentication) {
        Usuario usuario = obtenerUsuario(authentication);
        List<CurriculumDTO> cvs = curriculumService.listarCvsDelUsuario(usuario.getIdUsuario());
        return ResponseEntity.ok(cvs);
    }

    // 2. CREAR (REGISTRAR NUEVO CV)
    @PostMapping("/registrar")
    public ResponseEntity<?> registrarCv(
            @Valid @RequestBody CurriculumRequestDTO dto, 
            Authentication authentication) {
        
        Usuario usuario = obtenerUsuario(authentication);
        CurriculumDTO nuevoCv = curriculumService.guardarCurriculum(dto, usuario);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "message", "Currículum agregado correctamente",
                "data", nuevoCv
        ));
    }

    // 3. ACTUALIZAR (COMPLETO)
    @PutMapping("/actualizar/{idCurriculum}")
    public ResponseEntity<?> actualizarCv(
            @PathVariable Long idCurriculum,
            @Valid @RequestBody CurriculumRequestDTO dto,
            Authentication authentication) {
        
        Usuario usuario = obtenerUsuario(authentication);
        CurriculumDTO actualizado = curriculumService.actualizarCurriculum(idCurriculum, dto, usuario.getIdUsuario());
        
        return ResponseEntity.ok(Map.of(
                "message", "Currículum actualizado correctamente",
                "data", actualizado
        ));
    }

    // 4. ELIMINAR
    @DeleteMapping("/eliminar/{idCurriculum}")
    public ResponseEntity<?> eliminarCv(
            @PathVariable Long idCurriculum,
            Authentication authentication) {
        Usuario usuario = obtenerUsuario(authentication);
        curriculumService.eliminarCurriculum(idCurriculum, usuario.getIdUsuario());
        return ResponseEntity.ok(Map.of("message", "Currículum eliminado correctamente."));
    }
    

    private Usuario obtenerUsuario(Authentication authentication) {
        return usuarioRepository.findByCorreo(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }
}