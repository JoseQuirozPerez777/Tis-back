package com.teamsys.portafolios.controllers;

import com.teamsys.portafolios.dto.ReporteUsuariosResponseDTO;
import com.teamsys.portafolios.entities.Usuario;
import com.teamsys.portafolios.entities.Rol;
import com.teamsys.portafolios.repositories.UsuarioRepository;
import com.teamsys.portafolios.services.ReporteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/reportes")
@CrossOrigin(origins = "*")
public class AdminReporteController {

    @Autowired
    private ReporteService reporteService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/usuarios")
    public ResponseEntity<?> obtenerReporteUsuarios(
            @RequestParam(required = false) String fechaInicio,
            @RequestParam(required = false) String fechaFin,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String profesion,
            @RequestParam(required = false) String busqueda,
            Authentication authentication) {

        try {
            // 1. Validar que exista una sesión activa
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No autorizado: Sesión inválida");
            }

            // 2. Obtener el correo desde el token JWT inyectado en la autenticación
            String correoActual = authentication.getName();

            // 3. Buscar al usuario en la base de datos
            Usuario usuario = usuarioRepository.findByCorreo(correoActual)
                    .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado"));

            // 4. Verificar manualmente si el usuario cuenta con el rol ADMIN
            boolean esAdmin = usuario.getRoles().stream()
                    .map(Rol::getNombreRol)
                    .anyMatch(rol -> rol.equalsIgnoreCase("ADMIN") || rol.equalsIgnoreCase("ROLE_ADMIN"));

            if (!esAdmin) {
                // Si no es administrador, bloqueamos el acceso inmediatamente (403 Forbidden)
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Acceso denegado: Se requieren permisos de administrador");
            }

            // 5. Si pasa la validación, procedemos con la lógica del reporte
            ReporteUsuariosResponseDTO reporte = reporteService.obtenerReporteUsuarios(
                    fechaInicio, fechaFin, estado, profesion, busqueda);

            return ResponseEntity.ok(reporte);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error en el servidor: " + e.getMessage());
        }
    }
}