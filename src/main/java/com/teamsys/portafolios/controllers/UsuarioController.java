package com.teamsys.portafolios.controllers;

import com.teamsys.portafolios.dto.LoginRequestDTO;
import com.teamsys.portafolios.dto.UsuarioRegistroDTO;
import com.teamsys.portafolios.dto.UsuarioRespuestaDTO;
import com.teamsys.portafolios.entities.Rol;
import com.teamsys.portafolios.entities.Usuario;
import com.teamsys.portafolios.services.UsuarioService;
import com.teamsys.portafolios.security.JwtUtil; // Asegúrate de importar tu JwtUtil
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

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
}