package com.teamsys.portafolios.controllers;

import com.teamsys.portafolios.dto.EnlacePublicoDTO;
import com.teamsys.portafolios.dto.PortafolioCompletoDTO;
import com.teamsys.portafolios.dto.UsuarioPublicoDTO;
import com.teamsys.portafolios.entities.Usuario;
import com.teamsys.portafolios.repositories.UsuarioRepository;
import com.teamsys.portafolios.services.EnlacePublicoService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/enlace")
@CrossOrigin(origins = "*")
public class EnlacePublicoController {

    @Autowired
    private EnlacePublicoService enlacePublicoService;
    @Autowired
    private UsuarioRepository usuarioRepository;

    // 1. Generar la URL reversible basado en la sesión
    @PostMapping("/mi-enlace-publico")
    public ResponseEntity<EnlacePublicoDTO> gestionarEnlacePublico(Authentication authentication) {
        try {
            Usuario usuario = getUsuarioAutenticado(authentication);
            // Pasamos el correo de la autenticación
            EnlacePublicoDTO dto = enlacePublicoService.generarEnlace(usuario.getNombre(), authentication.getName());
            return ResponseEntity.ok(dto);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // 2. NUEVO ENDPOINT: Recibe la parte final de la URL, decodifica el correo y retorna el perfil
    // Ejemplo de llamada: GET /api/enlace/decodificar/juan-carlos-perez-amF2YUB0ZXN0LmNvbQ
    @GetMapping("/profile/{textoUrl}")
    public ResponseEntity<?> obtenerUsuarioPorUrlDecodificada(@PathVariable String textoUrl) {
        try {
            UsuarioPublicoDTO usuarioDto = enlacePublicoService.obtenerPerfilPorUrlMapeada(textoUrl);
            return ResponseEntity.ok(usuarioDto);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @GetMapping("/experiencias/{textoUrl}")
    public ResponseEntity<?> obtenerExperiencias(@PathVariable String textoUrl) {
        try {
            List<PortafolioCompletoDTO.ExperienciaLaboralResumenDTO> usuarioDto = enlacePublicoService.obtenerExperiencias(textoUrl);
            return ResponseEntity.ok(usuarioDto);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @GetMapping("/proyectos/{textoUrl}")
    public ResponseEntity<?> obtenerProyectos(@PathVariable String textoUrl) {
        try {
            List<PortafolioCompletoDTO.ProyectoResumenDTO> proyectosDto = enlacePublicoService.obtenerProyectos(textoUrl);
            return ResponseEntity.ok(proyectosDto);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @GetMapping("/habilidades-tecnicas/{textoUrl}")
    public ResponseEntity<?> obtenerHabilidadesTecnicas(@PathVariable String textoUrl) {
        try {
            List<PortafolioCompletoDTO.HabilidadTecnicaResumenDTO> habTecnicasDto = enlacePublicoService.obtenerHabilidadesTecnicas(textoUrl);
            return ResponseEntity.ok(habTecnicasDto);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @GetMapping("/habilidades-blandas/{textoUrl}")
    public ResponseEntity<?> obtenerHabilidadesBlandas(@PathVariable String textoUrl) {
        try {
            List<PortafolioCompletoDTO.HabilidadBlandaResumenDTO> habBlandasDto = enlacePublicoService.obtenerHabilidadesBlandas(textoUrl);
            return ResponseEntity.ok(habBlandasDto);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @GetMapping("/formaciones/{textoUrl}")
    public ResponseEntity<?> obtenerFormacionesAcademicas(@PathVariable String textoUrl) {
        try {
            List<PortafolioCompletoDTO.FormacionAcademicaResumenDTO> formacionesDto = enlacePublicoService.obtenerFormacionesAcademicas(textoUrl);
            return ResponseEntity.ok(formacionesDto);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @GetMapping("/curriculum/{textoUrl}")
    public ResponseEntity<?> obtenerCurriculumOficial(@PathVariable String textoUrl) {
        try {
            EnlacePublicoService.CurriculumResumenDTO curriculumDto = enlacePublicoService.obtenerCurriculumOficial(textoUrl);
            if (curriculumDto == null) {
                return ResponseEntity.noContent().build(); // Retorna 204 si no hay un CV oficial marcado
            }
            return ResponseEntity.ok(curriculumDto);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    private Usuario getUsuarioAutenticado(Authentication auth) {
        return usuarioRepository.findByCorreo(auth.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }
}