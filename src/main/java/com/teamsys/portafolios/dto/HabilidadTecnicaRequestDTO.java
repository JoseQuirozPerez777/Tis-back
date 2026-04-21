package com.teamsys.portafolios.dto;

import com.teamsys.portafolios.entities.HabilidadTecnica.NivelDominio;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class HabilidadTecnicaRequestDTO {

    @NotBlank(message = "El nombre de la habilidad es obligatorio")
    private String nombre;

    @NotNull(message = "La categoría es obligatoria")
    private Long idCategoria;

    private NivelDominio nivelDominio;

    private Integer anosExperiencia;

    private String descripcion;

    private String certificadoUrl;
}