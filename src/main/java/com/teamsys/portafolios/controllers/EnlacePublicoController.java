package com.teamsys.portafolios.controllers;

import com.teamsys.portafolios.dto.*;
import com.teamsys.portafolios.entities.LikePerfil;
import com.teamsys.portafolios.entities.Usuario;
import com.teamsys.portafolios.repositories.UsuarioRepository;
import com.teamsys.portafolios.services.EnlacePublicoService;

import java.util.List;
import java.util.Collections;

import com.teamsys.portafolios.services.VisibilidadPerfilService;
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
    @Autowired
    private VisibilidadPerfilService visibilidadPerfilService;

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
    @GetMapping("/profile/{textoUrl}")
    public ResponseEntity<?> obtenerUsuarioPorUrlDecodificada(@PathVariable String textoUrl, Authentication authentication) {
        try {
            // 1. Obtener los datos del perfil
            UsuarioPublicoDTO usuarioDto = enlacePublicoService.obtenerPerfilPorUrlMapeada(textoUrl);

            // 2. Buscar al usuario en la BD usando el correo del DTO para obtener sus "ojitos" de privacidad
            Usuario usuario = usuarioRepository.findByCorreo(usuarioDto.getCorreo())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            //VisibilidadPerfilService visibilidadPerfilService = new VisibilidadPerfilService();

            // 3. Obtener la configuración de visibilidad actual
            VisibilidadPerfilDTO visibilidad = visibilidadPerfilService.obtenerVisibilidadConfig(usuario);

            // 4. Filtrar los datos en el controlador antes de responder al Front-end (false = oculto)
            if (!visibilidad.isNombreUsr()) {
                usuarioDto.setNombre("Usuario Privado");
            }
            if (!visibilidad.isCorreoUsr()) {
                usuarioDto.setCorreo(null);
            }
            if (!visibilidad.isBiografiaUsr()) {
                usuarioDto.setBiografia(null);
            }
            if (!visibilidad.isTelefonoUsr()) {
                usuarioDto.setTelefono(null);
            }
            if (!visibilidad.isDireccionUsr()) {
                usuarioDto.setDireccion(null);
            }
            if (!visibilidad.isProfesionUsr()) {
                usuarioDto.setNombreProfesion(null);
            }

            // 5. Registrar la visita de forma segura (Se corrigió a tu método original registrarVisita)
            String correoVisitante = (authentication != null) ? authentication.getName() : null;
            enlacePublicoService.registrarVisita(textoUrl, correoVisitante);

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
                // Modificado: Si es null, devolvemos un JSON con la propiedad en null o vacío, según prefieras
                return ResponseEntity.ok(Collections.singletonMap("urlCv", null));
            }
            
            // Extraemos la URL
            String urlCv = curriculumDto.getUrlCv(); 
            
            // Retornamos un mapa que Spring convertirá automáticamente a: { "urlCv": "valor" }
            return ResponseEntity.ok(Collections.singletonMap("urlCv", urlCv));
            
        } catch (RuntimeException e) {
            // Para mantener la consistencia de JSON en los errores, puedes envolver el mensaje también
            return ResponseEntity.status(404).body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    @PostMapping("/profile/{textoUrl}/like")
    public ResponseEntity<?> darLikeAlPerfil(@PathVariable String textoUrl, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body("Debe estar autenticado para dar un 'Like'.");
        }

        try {
            String correoUsuarioQueDaLike = authentication.getName();
            String result = enlacePublicoService.registrarLike(textoUrl, correoUsuarioQueDaLike);
            return ResponseEntity.ok().body(java.util.Map.of("message", "Like "+result+" correctamente."));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 2. Obtener el listado de likes de un perfil de forma pública usando el textoUrl
    @GetMapping("/profile/{textoUrl}/likes")
    public ResponseEntity<?> obtenerLikesDelPerfil(@PathVariable String textoUrl) {
        try {
            List<LikePerfilDTO> likesDto = enlacePublicoService.obtenerLikesPorUrl(textoUrl);
            return ResponseEntity.ok(likesDto);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    // 3. Obtener el listado de likes de MI propio perfil (Autenticado)
    @GetMapping("/mis-likes")
    public ResponseEntity<?> obtenerMisLikes(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body("No autorizado");
        }
        try {
            Usuario usuario = getUsuarioAutenticado(authentication);
            List<LikePerfilDTO> likesDto = enlacePublicoService.obtenerLikesPorUsuario(usuario);
            return ResponseEntity.ok(likesDto);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 4. Obtener el TOTAL numérico de likes de forma pública usando textoUrl
    @GetMapping("/profile/{textoUrl}/likes/total")
    public ResponseEntity<?> obtenerTotalLikesDelPerfil(@PathVariable String textoUrl) {
        try {
            long totalLikes = enlacePublicoService.obtenerTotalLikesPorUrl(textoUrl);
            return ResponseEntity.ok(java.util.Map.of("totalLikes", totalLikes));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

@GetMapping("/profile/{textoUrl}/liked")
public ResponseEntity<?> verificarLike(
        @PathVariable String textoUrl,
        Authentication authentication) {

    if (authentication == null || !authentication.isAuthenticated()) {
        return ResponseEntity.ok(
                java.util.Map.of("liked", false)
        );
    }

    try {

        boolean liked = enlacePublicoService.usuarioYaDioLike(
                textoUrl,
                authentication.getName()
        );

        return ResponseEntity.ok(
                java.util.Map.of("liked", liked)
        );

    } catch (Exception e) {

        return ResponseEntity.ok(
                java.util.Map.of("liked", false)
        );

    }
}

    // 5. Obtener el TOTAL numérico de likes de MI propio perfil (Autenticado)
    @GetMapping("/mis-likes/total")
    public ResponseEntity<?> obtenerTotalMisLikes(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body("No autorizado");
        }
        try {
            Usuario usuario = getUsuarioAutenticado(authentication);
            long totalLikes = enlacePublicoService.obtenerTotalLikesPorUsuario(usuario);
            return ResponseEntity.ok(java.util.Map.of("totalLikes", totalLikes));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Nuevo endpoint para eliminar el LIKE
    @DeleteMapping("/profile/{textoUrl}/like")
    public ResponseEntity<?> quitarLikeAlPerfil(@PathVariable String textoUrl, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body("Debe estar autenticado para realizar esta acción.");
        }

        try {
            String correoUsuarioQueQuitaLike = authentication.getName();
            enlacePublicoService.registrarLike(textoUrl, correoUsuarioQueQuitaLike);
            return ResponseEntity.ok().body(java.util.Map.of("message", "Like eliminado correctamente."));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    private Usuario getUsuarioAutenticado(Authentication auth) {
        return usuarioRepository.findByCorreo(auth.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }
}