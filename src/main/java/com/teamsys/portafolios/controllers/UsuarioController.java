package com.teamsys.portafolios.controllers;

import com.teamsys.portafolios.dto.*;
import com.teamsys.portafolios.entities.Rol;
import com.teamsys.portafolios.entities.Usuario;
import com.teamsys.portafolios.repositories.UsuarioRepository;
import com.teamsys.portafolios.services.UsuarioService;
import com.teamsys.portafolios.security.JwtUtil; // Asegúrate de importar tu JwtUtil
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JwtUtil jwtUtil; // Inyectamos el motor de JWT

    @PostMapping("/registro")
    public ResponseEntity<?> registrarUsuario(@RequestBody UsuarioRegistroDTO registroDTO) {
        try {
            // 1. Guardamos el usuario usando el servicio
            Usuario nuevoUsuario = usuarioService.registrar(registroDTO);

            // 2. Generamos el token JWT usando el correo del usuario recién creado
            String token = jwtUtil.generarToken(nuevoUsuario.getCorreo());

            // 3. Extraemos los nombres de los roles (de objetos Rol a Strings)
            java.util.Set<String> rolesNombres = nuevoUsuario.getRoles().stream()
                    .map(Rol::getNombreRol)
                    .collect(Collectors.toSet());

            // 4. Construimos la respuesta estructurada
            UsuarioRespuestaDTO.UsuarioInfo info = new UsuarioRespuestaDTO.UsuarioInfo(
                    nuevoUsuario.getIdUsuario(),
                    nuevoUsuario.getNombre(),
                    nuevoUsuario.getCorreo(),
                    rolesNombres
            );

            UsuarioRespuestaDTO respuesta = new UsuarioRespuestaDTO(token, info);

            return ResponseEntity.ok(respuesta);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Dentro de UsuarioController.java

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginDTO) {
        try {
            // 1. Validar identidad
            Usuario usuario = usuarioService.autenticar(loginDTO.getCorreo(), loginDTO.getPassword());

            // 2. Generar Token
            String token = jwtUtil.generarToken(usuario.getCorreo());

            // 3. Mapear roles a String
            java.util.Set<String> rolesNombres = usuario.getRoles().stream()
                    .map(Rol::getNombreRol)
                    .collect(Collectors.toSet());

            // 4. Devolver respuesta idéntica al registro
            UsuarioRespuestaDTO.UsuarioInfo info = new UsuarioRespuestaDTO.UsuarioInfo(
                    usuario.getIdUsuario(),
                    usuario.getNombre(),
                    usuario.getCorreo(),
                    rolesNombres
            );

            return ResponseEntity.ok(new UsuarioRespuestaDTO(token, info));

        } catch (Exception e) {
            // Si está bloqueado o las credenciales fallan, llega aquí
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }

    @PostMapping("/perfil")
    public ResponseEntity<?> actualizarPerfil(
            @RequestBody UsuarioInformacionBasicaDTO dto,
            Authentication authentication) {

        String correoAutenticado = authentication.getName();

        Usuario usuarioLogueado = usuarioRepository.findByCorreo(correoAutenticado)
                .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado"));

        boolean actualizado = usuarioService.actualizarInformacionBasica(dto, usuarioLogueado);

        if (actualizado) {
            // Devolvemos un objeto JSON estructurado
            return ResponseEntity.ok(java.util.Map.of(
                    "success", true,
                    "message", "Perfil actualizado con éxito",
                    "data", dto
            ));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(java.util.Map.of(
                            "success", false,
                            "message", "Error al actualizar los datos."
                    ));
        }
    }

    @GetMapping("/perfil")
    public ResponseEntity<?> obtenerPerfil(Authentication authentication) {
        try {
            // 1. Obtener el correo desde el JWT
            String correo = authentication.getName();

            // 2. Buscar al usuario
            Usuario usuario = usuarioRepository.findByCorreo(correo)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            // 3. Mapear manualmente al DTO
            UsuarioPerfilDTO perfil = new UsuarioPerfilDTO();
            perfil.setNombre(usuario.getNombre());
            perfil.setBiografia(usuario.getBiografia());
            perfil.setFoto(usuario.getFoto());
            // 4. Si tiene profesión, extraemos solo el ID
            if (usuario.getProfesion() != null) {
                perfil.setIdProfesion(usuario.getProfesion().getIdProfesion());
            } else {
                perfil.setIdProfesion(null);
            }

            return ResponseEntity.ok(perfil);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PatchMapping("/foto-perfil")
    public ResponseEntity<?> actualizarFotoPerfil(
            @RequestBody java.util.Map<String, String> body,
            Authentication authentication) {
        try {
            // 1. Extraer el campo del Map (debe coincidir con tu interfaz TS: fotoPerfil)
            String urlFoto = body.get("fotoPerfil");

            if (urlFoto == null || urlFoto.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(java.util.Map.of(
                        "message", "El campo 'fotoPerfil' es obligatorio"
                ));
            }

            // 2. Obtener el usuario autenticado desde el SecurityContext
            String correo = authentication.getName();
            Usuario usuario = usuarioRepository.findByCorreo(correo)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            // 3. Actualizar la entidad
            usuario.setFoto(urlFoto);
            usuarioRepository.save(usuario);

            // 4. Retornar respuesta exitosa
            return ResponseEntity.ok(java.util.Map.of(
                    "success", true,
                    "message", "Foto de perfil actualizada con éxito"
                    //"fotoPerfil", urlFoto
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(java.util.Map.of("message", "Error interno: " + e.getMessage()));
        }
    }
}