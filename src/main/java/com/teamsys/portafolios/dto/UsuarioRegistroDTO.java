package com.teamsys.portafolios.dto;

import com.fasterxml.jackson.annotation.JsonProperty; // Importante
import lombok.Data;

@Data
public class UsuarioRegistroDTO {
    private String nombre;
    private String correo;
    private String password;

}