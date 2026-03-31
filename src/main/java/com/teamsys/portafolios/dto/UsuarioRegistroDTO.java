package com.teamsys.portafolios.dto;

import com.fasterxml.jackson.annotation.JsonProperty; // Importante
import lombok.Data;

@Data
public class UsuarioRegistroDTO {
    private String nombre;
    private String correo;
    private String password;

    /*@JsonProperty("id_profesion") // Esto enlaza el JSON con esta variable
    private Long idProfesion;*/
}