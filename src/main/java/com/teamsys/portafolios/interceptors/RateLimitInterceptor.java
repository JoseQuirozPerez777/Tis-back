package com.teamsys.portafolios.interceptors;

import com.teamsys.portafolios.entities.Usuario;
import com.teamsys.portafolios.repositories.UsuarioRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import java.time.LocalDateTime;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String correo = request.getHeader("X-User-Email");

        if (correo != null) {
            Usuario usuario = usuarioRepository.findByCorreo(correo).orElse(null);
            if (usuario != null && usuario.getIntentosFallidos() >= 5 && usuario.getFechaBloqueo() != null) {
                if (usuario.getFechaBloqueo().plusMinutes(5).isAfter(LocalDateTime.now())) {
                    response.setStatus(429); // Too Many Requests
                    response.getWriter().write("Acceso bloqueado por 5 minutos debido a demasiados intentos.");
                    return false;
                } else {
                    usuario.setIntentosFallidos(0);
                    usuario.setFechaBloqueo(null);
                    usuarioRepository.save(usuario);
                }
            }
        }
        return true;
    }
}