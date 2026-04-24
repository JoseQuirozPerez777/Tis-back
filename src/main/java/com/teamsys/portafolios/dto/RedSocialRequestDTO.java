package com.teamsys.portafolios.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RedSocialRequestDTO {
    private String nombreRed;
    private String urlPerfil;
    private boolean esPublico;

    public boolean getEsPublico() {
        return esPublico;
    }
    
    
}