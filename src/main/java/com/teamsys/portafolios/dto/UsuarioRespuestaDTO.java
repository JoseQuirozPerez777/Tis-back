package com.teamsys.portafolios.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.Set;

@Data
@AllArgsConstructor
public class UsuarioRespuestaDTO {
    private String token;
    private UsuarioInfo usuario;

    @Data
    @AllArgsConstructor
    public static class UsuarioInfo {
        private Long id;
        private String nombre;
        private String correo;
        private Set<String> roles;
    }
}