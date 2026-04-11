package com.teamsys.portafolios.dto;

import com.teamsys.portafolios.entities.Habilidad;
import lombok.Data;

@Data
public class HabilidadRequestDTO {
    private String nombreHabilidad;
    private Habilidad.TipoHabilidad tipo;
    private Long idCategoria;
    private Habilidad.NivelDominio nivelDominio; // Puede ser null
    private Integer anosExperiencia;             // Puede ser null
    private String certificado;                  // Puede ser null
}