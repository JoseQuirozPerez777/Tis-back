package com.teamsys.portafolios.dto;

import com.fasterxml.jackson.annotation.JsonProperty; // Importante
import lombok.Data;

@Data
public class PasswordRequestDTO {
    private String passwordActual;
    private String passwordNuevo;

}