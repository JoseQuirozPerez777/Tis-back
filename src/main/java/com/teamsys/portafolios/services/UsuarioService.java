package com.teamsys.portafolios.services;

import com.teamsys.portafolios.dto.UsuarioRegistroDTO;
import com.teamsys.portafolios.entities.Rol;
import com.teamsys.portafolios.entities.Usuario;
import com.teamsys.portafolios.entities.Profesion;
import com.teamsys.portafolios.repositories.RolRepository;
import com.teamsys.portafolios.repositories.UsuarioRepository;
import com.teamsys.portafolios.repositories.ProfesionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.Duration;

@Service
public class UsuarioService {

    @Autowired
    private ProfesionRepository profesionRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // Método para validar si el usuario debe seguir esperando
    public void validarEspera(Usuario usuario) {
        if (usuario.getIntentosFallidos() >= 5 && usuario.getFechaBloqueo() != null) {
            LocalDateTime ahora = LocalDateTime.now();
            LocalDateTime tiempoPermitido = usuario.getFechaBloqueo().plusMinutes(5);

            if (ahora.isBefore(tiempoPermitido)) {
                long minutosRestantes = Duration.between(ahora, tiempoPermitido).toMinutes();
                if (minutosRestantes == 0) {
                    throw new RuntimeException("Demasiados intentos. Intente de nuevo en menos de un minuto.");
                }
                throw new RuntimeException("Demasiados intentos. Por favor, espere " + minutosRestantes + " minutos.");
            } else {
                // Si ya pasaron los 5 min, reseteamos
                usuario.setIntentosFallidos(0);
                usuario.setFechaBloqueo(null);
                usuarioRepository.save(usuario);
            }
        }
    }

    // Método unificado para registrar errores de login/registro
    public void registrarFallo(String correo) {
        usuarioRepository.findByCorreo(correo).ifPresent(u -> {
            u.setIntentosFallidos(u.getIntentosFallidos() + 1);
            u.setFechaUltimoIntentoFallido(LocalDateTime.now());

            if (u.getIntentosFallidos() >= 5) {
                u.setFechaBloqueo(LocalDateTime.now());
            }
            usuarioRepository.save(u);
        });
    }

    public Usuario registrar(UsuarioRegistroDTO dto) {
        // 1. Validación de duplicados
        if(usuarioRepository.existsByCorreo(dto.getCorreo())) {
            throw new RuntimeException("Error: El correo electrónico ya está en uso.");
        }

        // 2. Creación de la instancia
        Usuario usuario = new Usuario();
        usuario.setNombre(dto.getNombre());
        usuario.setCorreo(dto.getCorreo());

        // 3. Encriptación de contraseña
        usuario.setPassword(passwordEncoder.encode(dto.getPassword()));

        // 4. Asignación de Profesión
        if (dto.getIdProfesion() != null) {
            Profesion prof = profesionRepository.findById(dto.getIdProfesion())
                    .orElseThrow(() -> new RuntimeException("Error: La profesión seleccionada no existe."));
            usuario.setProfesion(prof);
        }

        // 5. ASIGNACIÓN DE ROL POR DEFECTO (Crítico para la seguridad)
        // Buscamos el objeto Rol "ROLE_USER" en la base de datos
        Rol userRol = rolRepository.findByNombreRol("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Error Crítico: El rol ROLE_USER no está inicializado en la base de datos."));

        // Asignamos el rol al set de roles del usuario (un usuario puede tener varios)
        usuario.setRoles(java.util.Set.of(userRol));

        // 6. Inicialización de campos de seguridad
        usuario.setIntentosFallidos(0);
        usuario.setFechaRegistro(java.time.LocalDateTime.now());

        // 7. Persistencia en MySQL
        return usuarioRepository.save(usuario);
    }
}