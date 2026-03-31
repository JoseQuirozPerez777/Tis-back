package com.teamsys.portafolios.services;

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
import com.teamsys.portafolios.utils.ValidadorDatos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.Duration;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private ProfesionRepository profesionRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

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


    public boolean actualizarInformacionBasica(UsuarioInformacionBasicaDTO dto) {
        try {

            if (!ValidadorDatos.esNombreValido(dto.getNombre())) {
                throw new Exception("El nombre debe iniciar con mayúscula y no contener números.");
            }
            // 1. Buscar al usuario por el ID que viene en el DTO
            Usuario usuario = usuarioRepository.findById(dto.getIdUsuario())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            // 2. Actualizar campos simples
            usuario.setNombre(dto.getNombre());
            usuario.setBiografia(dto.getBiografia());

            // 3. Actualizar relación con Profesión
            if (dto.getIdProfesion() != null) {
                Profesion profesion = profesionRepository.findById(dto.getIdProfesion())
                        .orElseThrow(() -> new RuntimeException("Profesión no encontrada"));
                usuario.setProfesion(profesion);
            }

            // 4. Guardar cambios
            usuarioRepository.save(usuario);
            return true;

        } catch (Exception e) {
            // Opcional: imprimir error en consola para debug
            // System.out.println("Error al actualizar: " + e.getMessage());
            return false;
        }
    }

    public String procesarRecuperacionPassword(String correo) {
        // 1. Validar que el usuario exista
        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 2. Verificar si ya tiene un código generado que aún sea válido
        Optional<CodigoVerificacion> codigoExistente = codigoRepository.findByUsuario(usuario);

        if (codigoExistente.isPresent()) {
            CodigoVerificacion cv = codigoExistente.get();
            // Si la fecha de expiración es después de "ahora", el código sigue vigente
            if (cv.getFechaExpiracion().isAfter(LocalDateTime.now())) {
                throw new RuntimeException("Ya tienes un código activo. Por favor, espera a que expire.");
            }
            // Si ya expiró, lo borramos para crear uno nuevo limpio
            codigoRepository.delete(cv);
        }

        // 3. Generar nuevo código de 6 dígitos
        String nuevoCodigo = String.valueOf((int)(Math.random() * 900000) + 100000);

        // 4. Guardar el nuevo código en la tabla dedicada
        CodigoVerificacion nuevoRegistro = new CodigoVerificacion();
        nuevoRegistro.setCodigo(nuevoCodigo);
        nuevoRegistro.setUsuario(usuario);
        nuevoRegistro.setTipo("RECUPERACION_PASSWORD");
        nuevoRegistro.setFechaExpiracion(LocalDateTime.now().plusMinutes(2)); // Duración de 2 min

        codigoRepository.save(nuevoRegistro);

        // 5. Simulación de envío (Aquí iría tu emailService)
        System.out.println("CÓDIGO DE SEGURIDAD (Válido por 2 min): " + nuevoCodigo);

        return "Código enviado con éxito.";
    }

    public boolean validarCodigoRecuperacion(String correo, String codigoEnviado) {
        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        CodigoVerificacion cv = codigoRepository.findByUsuario(usuario)
                .orElseThrow(() -> new RuntimeException("No hay códigos solicitados para este usuario"));

        // Validamos coincidencia y tiempo
        if (cv.getCodigo().equals(codigoEnviado) && cv.getFechaExpiracion().isAfter(LocalDateTime.now())) {
            // Código válido: Lo eliminamos para que no se use de nuevo (Seguridad)
            codigoRepository.delete(cv);
            return true;
        }

        return false;
    }
}