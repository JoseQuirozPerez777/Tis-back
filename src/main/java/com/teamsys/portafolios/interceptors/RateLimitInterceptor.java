package com.teamsys.portafolios.interceptors;

import com.teamsys.portafolios.entities.Usuario;
import com.teamsys.portafolios.repositories.UsuarioRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    // Mapa temporal para bloquear IPs que abusan del sistema
    private final Map<String, Integer> intentosPorIp = new ConcurrentHashMap<>();
    private final Map<String, LocalDateTime> bloqueoIp = new ConcurrentHashMap<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String ipCliente = request.getRemoteAddr();

        // 1. Verificar si la IP está en tiempo de bloqueo
        if (bloqueoIp.containsKey(ipCliente)) {
            if (bloqueoIp.get(ipCliente).isAfter(LocalDateTime.now())) {
                response.setStatus(429); // Too Many Requests
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"Demasiadas peticiones. Intente más tarde.\"}");
                return false;
            } else {
                bloqueoIp.remove(ipCliente);
                intentosPorIp.put(ipCliente, 0);
            }
        }

        // 2. Contar intentos (esto se resetea al tener éxito en el controller)
        int intentos = intentosPorIp.getOrDefault(ipCliente, 0) + 1;
        intentosPorIp.put(ipCliente, intentos);

        if (intentos > 10) { // Límite de 10 intentos seguidos desde la misma IP
            bloqueoIp.put(ipCliente, LocalDateTime.now().plusMinutes(5));
            return false;
        }

        return true;
    }
}