package com.teamsys.portafolios.services;

import com.teamsys.portafolios.dto.PasswordRequestDTO;
import com.teamsys.portafolios.dto.UsuarioInformacionBasicaDTO;
import com.teamsys.portafolios.dto.UsuarioRegistroDTO;
import com.teamsys.portafolios.entities.CodigoVerificacion;
import com.teamsys.portafolios.entities.Rol;
import com.teamsys.portafolios.entities.Usuario;
import com.teamsys.portafolios.entities.Profesion;
import com.teamsys.portafolios.repositories.CodigoVerificacionRepository;
import com.teamsys.portafolios.repositories.RolRepository;
import com.teamsys.portafolios.repositories.UsuarioRepository;
import com.teamsys.portafolios.repositories.ProfesionRepository;
import com.teamsys.portafolios.security.JwtUtil;
import com.teamsys.portafolios.utils.ValidadorDatos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private ProfesionRepository profesionRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JwtUtil jwtUtil; // Necesario para crear el token de recuperación

    @Value("${url.front}")
    private String urlFront;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private CodigoVerificacionRepository codigoRepository;

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

    public Usuario registrar(UsuarioRegistroDTO dto) throws Exception {

        // 1. Validar Nombre (Mayúsculas, sin números)
        if (!ValidadorDatos.esNombreValido(dto.getNombre())) {
            throw new Exception("El nombre debe iniciar con mayúscula y no contener números.");
        }

        // 2. Validar Correo (Formato estándar)
        if (!ValidadorDatos.esCorreoValido(dto.getCorreo())) {
            throw new Exception("El formato del correo electrónico no es válido.");
        }

        // 3. Validar Password (Mínimo 8 caracteres)
        if (!ValidadorDatos.esPasswordSegura(dto.getPassword())) {
            throw new Exception("La contraseña debe tener al menos 8 caracteres.");
        }
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

    // Dentro de UsuarioService.java

    public Usuario autenticar(String correo, String password) throws Exception {

        if (!ValidadorDatos.esCorreoValido(correo)) {
            throw new Exception("El formato del correo electrónico no es válido.");
        }

        // 3. Validar Password (Mínimo 8 caracteres)
        if (!ValidadorDatos.esPasswordSegura(password)) {
            throw new Exception("La contraseña debe tener al menos 8 caracteres.");
        }

        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new Exception("Credenciales incorrectas"));

        // 1. Verificar si está bloqueado (Tu lógica de validarEspera)
        validarEspera(usuario);

        // 2. Comprobar contraseña
        if (passwordEncoder.matches(password, usuario.getPassword())) {
            // Éxito: Limpiamos fallos
            usuario.setIntentosFallidos(0);
            usuario.setFechaBloqueo(null);
            usuarioRepository.save(usuario);
            return usuario;
        } else {
            // Fallo: Registramos el error
            registrarFallo(correo);
            throw new Exception("Credenciales incorrectas");
        }
    }


    public void actualizarInformacionBasica(UsuarioInformacionBasicaDTO dto,Usuario usuarioLogueado)throws Exception  {


            if (!ValidadorDatos.esNombreValido(dto.getFullName())) {
                throw new Exception("El nombre debe iniciar con mayúscula y no contener números.");
            }
            if (!ValidadorDatos.telefonoValido(dto.getTelefono())) {
                throw new Exception("El formato de telefono no es valido ");
            }

            // 2. Actualizar campos simples
            usuarioLogueado.setNombre(dto.getFullName());
            usuarioLogueado.setBiografia(dto.getBio());
            usuarioLogueado.setTelefono(dto.getTelefono());

            // 3. Actualizar relación con Profesión
            if (dto.getProfession() != null) {
                Profesion profesion = profesionRepository.findByNombreProfesion(dto.getProfession())
                        .orElseThrow(() -> new RuntimeException("Profesión no encontrada"));
                usuarioLogueado.setProfesion(profesion);
            }

            // 4. Guardar cambios
            usuarioRepository.save(usuarioLogueado);
 
    }

    public String procesarRecuperacionPassword(String correo) throws Exception {
        if (!ValidadorDatos.esCorreoValido(correo)) {
            throw new Exception("El formato del correo electrónico no es válido.");
        }

        // 1. Validar que el usuario exista
        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 2. Generar un JWT específico para recuperación
        // Recomendación: Que este token expire en 10-15 minutos (configúralo en JwtUtil)
        String tokenRecuperacion = jwtUtil.generarToken(usuario.getCorreo());

        // 3. Construir la URL completa para el Frontend
        // Ejemplo: https://tu-front.com/reset-password?token=eyJhbG...
        String enlaceRecuperacion = urlFront + "/reset-password?token=" + tokenRecuperacion;

        // 4. Envío Real mediante API de Brevo
        try {
            emailService.enviarEnlaceRecuperacion(usuario.getCorreo(), enlaceRecuperacion);
        } catch (Exception e) {
            System.err.println("Error al contactar con Brevo: " + e.getMessage());
        }

        return "Si el correo es válido, recibirás instrucciones en tu bandeja de entrada.";
    }

    public void actualizarPasswordDirecto(String correo, String nuevoPassword) throws Exception {
        // 1. Validar fortaleza del nuevo password
        if (!ValidadorDatos.esPasswordSegura(nuevoPassword)) {
            throw new Exception("La nueva contraseña debe tener al menos 8 caracteres.");
        }

        // 2. Buscar al usuario por el correo extraído del token
        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new Exception("Usuario no encontrado"));

        // 3. Encriptar la nueva contraseña
        usuario.setPassword(passwordEncoder.encode(nuevoPassword));

        usuarioRepository.save(usuario);
    }
     public void actualizarPassword(String correo, PasswordRequestDTO nuevoPassword) throws Exception {
        
         // 2. Buscar al usuario por el correo extraído del token
        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new Exception("Usuario no encontrado"));

        if(!passwordEncoder.matches(nuevoPassword.getPasswordActual(), usuario.getPassword())) 
            throw new Exception("La contraseña actual no es correcta.");
           
        // 1. Validar fortaleza del nuevo password
        if (!ValidadorDatos.esPasswordSegura(nuevoPassword.getPasswordNuevo())) {
            throw new Exception("La nueva contraseña debe tener al menos 8 caracteres.");
        }

        // 3. Encriptar la nueva contraseña
        usuario.setPassword(passwordEncoder.encode(nuevoPassword.getPasswordNuevo()));

        usuarioRepository.save(usuario);
    }

}