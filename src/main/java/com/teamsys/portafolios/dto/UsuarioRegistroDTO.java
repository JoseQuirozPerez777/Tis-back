package com.teamsys.portafolios.dto;

import lombok.Data;
import java.util.Set;

@Data
public class UsuarioRegistroDTO {
    private String nombre;
    private String correo;
    private String password;
    private Long idProfesion; // Solo enviamos el ID de la profesión

    /*public String getNombre() {
        return nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public String getPassword() {
        return password;
    }

    public Long getIdProfesion() {
        return idProfesion;
    }*/

}