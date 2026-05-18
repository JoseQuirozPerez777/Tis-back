package com.teamsys.portafolios.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioPublicoDTO {

    private String nombre;
    private String correo;
    private String foto;
    private String biografia;
    private String telefono;
    private String direccion;
    private String nombreProfesion; 
    private List<RedSocialResumenDTO> redes;// En lugar del ID, exponemos el nombre directo

    @Data @Builder
    public static class RedSocialResumenDTO {
        private String nombreRed;
        private String urlPerfil;
    }
}