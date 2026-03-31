package com.teamsys.portafolios.dto;

import com.fasterxml.jackson.annotation.JsonProperty; // Importante
import lombok.Data;

@Data
public class UsuarioInformacionBasicaDTO {

    private Long idUsuario;
    private String nombre;
    private String biografia;

    @JsonProperty("id_profesion") // Esto enlaza el JSON con esta variable
    private Long idProfesion;

}
