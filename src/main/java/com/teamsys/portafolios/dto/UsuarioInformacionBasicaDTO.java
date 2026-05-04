package com.teamsys.portafolios.dto;

import com.fasterxml.jackson.annotation.JsonProperty; // Importante
import lombok.Data;

@Data
public class UsuarioInformacionBasicaDTO {

    private String fullName;

    //@JsonProperty("id_profesion") // Esto enlaza el JSON con esta variable
    private String profession;

    private String bio;
     
    private String telefono;

    private String direccion;

}
